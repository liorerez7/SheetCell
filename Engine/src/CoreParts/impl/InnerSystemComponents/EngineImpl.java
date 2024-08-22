package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.SheetCell;
import CoreParts.api.SheetConvertor;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocationFactory;
import GeneratedClasses.STLSheet;
import Utility.CellUtils;
import CoreParts.api.Engine;
import CoreParts.smallParts.CellLocation;
import Utility.EngineUtilies;
import Utility.RefDependencyGraph;
import Utility.RefGraphBuilder;
import expression.Operation;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.processing.ExpressionParser;
import expression.impl.Processing.ExpressionParserImpl;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.util.*;

public class EngineImpl implements Engine {
    Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges = new HashMap<>();
    private SheetCellImp sheetCell = new SheetCellImp(0, 0, "Sheet1", 0, 0);

    public EngineImpl() {
        versionToCellsChanges.put(sheetCell.getLatestVersion(), new HashMap<>());
    }


    @Override
    public DtoCell getRequestedCell(String cellId, boolean updateCell) {
        if (updateCell)
            return new DtoCell(getCell(CellLocationFactory.fromCellId(cellId)));
        else
            if (!CellLocationFactory.isContained(cellId))
            {
                throw new IllegalArgumentException("cell does not exist(you can create a cell using the updateCell command)");
            }

        return new DtoCell(getCell(CellLocationFactory.fromCellId(cellId)));

    }
    public Cell getCell(CellLocation location) {
        // Fetch the cell directly from the map in SheetCellImp
        return sheetCell.getCell(location);
    }
    private void versionControl() {
    int sheetCellLatestVersion = sheetCell.getLatestVersion();
    versionToCellsChanges.put(sheetCellLatestVersion,new HashMap<>());
    Map<CellLocation, EffectiveValue> changedCells = versionToCellsChanges.get(sheetCellLatestVersion);
        for (Map.Entry<CellLocation, Cell> entry : sheetCell.getSheetCell().entrySet()) {
            CellLocation location = entry.getKey();
            Cell cell = entry.getValue();
            // Check if the cell's latest version matches the sheet's latest version
            if (cell.getLatestVersion() == sheetCellLatestVersion)   // Assuming Cell has a getVersion() method// Replace with your logic to calculate the effective value
                changedCells.put(location, cell.getEffectiveValue().evaluate());
        }
    }
    @Override
    public DtoSheetCell getSheetCell() {
        return new DtoSheetCell(sheetCell);
    }

    public SheetCellImp getInnerSystemSheetCell() {
        return sheetCell;
    }

    @Override
    public DtoSheetCell getSheetCell(int versionNumber) {
        return new DtoSheetCell(versionToCellsChanges, sheetCell, versionNumber);
    }
    public List<Cell> gettopologicalSortOfExpressions() throws Exception {
        RefDependencyGraph graph = sheetCell.getRefDependencyGraph();
        List<Cell> topologicalOrder;
        topologicalOrder = graph.topologicalSort();
        return topologicalOrder;
    }
    private void setUpSheet() throws Exception {
        sheetCell.createRefDependencyGraph();
        List<Cell> topologicalOrder = gettopologicalSortOfExpressions();
        topologicalOrder.forEach(cell -> {
        Expression expression = CellUtils.processExpressionRec(cell.getOriginalValue(), cell, getInnerSystemSheetCell());
        expression.evaluate();
        cell.setEffectiveValue(expression);
    });
        sheetCell.updateEffectedByAndOnLists();
    }
    @Override
    public void readSheetCellFromXML(String path) throws Exception {
        InputStream in = new FileInputStream(new File(path));
        STLSheet sheet = EngineUtilies.deserializeFrom(in);
        SheetConvertor convertor = new SheetConvertorImpl();
        sheetCell = (SheetCellImp) convertor.convertSheet(sheet);
        setUpSheet();
    }
    @Override
    public void updateCell(String newValue, char col,String row) throws Exception {
        Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));
        Expression expression = CellUtils.processExpressionRec(newValue,targetCell,getInnerSystemSheetCell());
        // 1.save sheet serilize
        //2. change cell orignal and effective value.
        targetCell.setOriginalValue(newValue);
        targetCell.setEffectiveValue(expression);
        //3. create graph
        sheetCell.createRefDependencyGraph();
        sheetCell.getRefDependencyGraph().topologicalSort();

        //4.change effected by and on list from the graph
        sheetCell.updateEffectedByAndOnLists();
        // alternitive way
        // topological sort for graph
        try {
            expression.evaluate().getValue();
            for(Cell cell : targetCell.getAffectingOn()){
                ExpressionParser parser = new ExpressionParserImpl(cell.getOriginalValue());
                if(Operation.fromString(parser.getFunctionName()) == Operation.REF){
                    continue;
                }
                if(expression.evaluate().getCellType() != cell.getEffectiveValue().evaluate().getCellType()){
                    throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed");
                }
            }
            Expression oldExpression = targetCell.getEffectiveValue(); // old expression
            sheetCell.updateVersion();
            targetCell.updateVersion(sheetCell.getLatestVersion());
            CellUtils.recalculateCellsRec(targetCell, oldExpression);
            versionControl();
        }
        catch(Exception illegalArgumentException){
            throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed");
        }
    }
    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public void save(String path) throws Exception {
        try (FileOutputStream fileOut = new FileOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(versionToCellsChanges);
            out.writeObject(sheetCell);
            out.flush();
            System.out.println("Data saved to " + path);
        } catch (IOException e) {
            throw new Exception("Error saving data to " + path + ": " + e.getMessage(), e);
        }
    }

    public void load(String path) throws Exception {
        try (FileInputStream fileIn = new FileInputStream(new File(path));
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges = (Map<Integer, Map<CellLocation, EffectiveValue>>) in.readObject();
            SheetCellImp sheetCell = (SheetCellImp) in.readObject();
            System.out.println("sheet loaded.");
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Invalid path: " + path);
        }
    }
}