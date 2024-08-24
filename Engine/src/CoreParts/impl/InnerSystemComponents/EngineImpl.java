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

    Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges;
    private SheetCellImp sheetCell;

    public EngineImpl() {
        versionToCellsChanges = new HashMap<>();
        sheetCell = new SheetCellImp(0, 0, "Sheet1", 0, 0);
        versionToCellsChanges.put(sheetCell.getLatestVersion(), new HashMap<>());
    }

    @Override
    public DtoCell getRequestedCell(String cellId) {

        if(CellLocationFactory.isContained(cellId)) {
            return new DtoCell(getCell(CellLocationFactory.fromCellId(cellId)));
        }
        else {
            return null;
        }
    }

    @Override
    public DtoSheetCell getSheetCell() {
        return new DtoSheetCell(sheetCell);
    }

    @Override
    public DtoSheetCell getSheetCell(int versionNumber) {
        return new DtoSheetCell(versionToCellsChanges, sheetCell, versionNumber);
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
    public void updateCell(String newValue, char col, String row) throws Exception {

        // Step 1: Serialize and save the current sheetCell
        byte[] savedSheetCellState = saveSheetCellState();

        Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));
        Expression expression = CellUtils.processExpressionRec(newValue, targetCell, getInnerSystemSheetCell());
        Expression oldExpression = targetCell.getEffectiveValue(); // old expression

        try {
            applyCellUpdates(targetCell, newValue, expression);
            performGraphOperations();
            validateExpression(expression, targetCell);
            updateVersionsAndRecalculate(targetCell, oldExpression);
            versionControl();
        } catch (Exception e) {
            restoreSheetCellState(savedSheetCellState);
            CellLocationFactory.removeKey(col + row);
            if(CellLocationFactory.isContained(col + row)){
                System.out.println("BUGGG");
            }
            throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed", e);
        }
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public void save(String path) throws Exception {

        /*
        example path which has permissions:
        String path1 = "C:\\Users\\Lior\\Documents\\my_sheet_state.dat";
        */

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

    private byte[] saveSheetCellState() throws Exception {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(sheetCell);
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save the sheetCell state", e);
        }
    }

    private void applyCellUpdates(Cell targetCell, String originalValue, Expression expression) {
        targetCell.setOriginalValue(originalValue);
        targetCell.setEffectiveValue(expression);
    }

    private void performGraphOperations() throws Exception {
        sheetCell.createRefDependencyGraph();
        sheetCell.getRefDependencyGraph().topologicalSort();
        sheetCell.updateEffectedByAndOnLists();
    }

    private void validateExpression(Expression expression, Cell targetCell) {
        try {
            expression.evaluate().getValue();

            for (Cell cell : targetCell.getAffectingOn()) {
                ExpressionParser parser = new ExpressionParserImpl(cell.getOriginalValue());
                if (Operation.fromString(parser.getFunctionName()) == Operation.REF) {
                    continue;
                }
                if (expression.evaluate().getCellType() != cell.getEffectiveValue().evaluate().getCellType()) {
                    throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed", e);
        }
    }

    private void updateVersionsAndRecalculate(Cell targetCell, Expression oldExpression) {
        sheetCell.updateVersion();
        targetCell.updateVersion(sheetCell.getLatestVersion());
        CellUtils.recalculateCellsRec(targetCell, oldExpression);
    }

    private void restoreSheetCellState(byte[] savedSheetCellState) throws Exception {
        try {
            if (savedSheetCellState != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(savedSheetCellState);
                ObjectInputStream ois = new ObjectInputStream(bais);
                sheetCell = (SheetCellImp) ois.readObject(); // Restore the original sheetCell
            }
        } catch (Exception restoreEx) {
            throw new IllegalStateException("Failed to restore the original sheetCell state", restoreEx);
        }
    }

    public void load(String path) throws Exception {
        try (FileInputStream fileIn = new FileInputStream(new File(path));
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges = (Map<Integer, Map<CellLocation, EffectiveValue>>) in.readObject();
            sheetCell = (SheetCellImp) in.readObject();
            System.out.println("sheet loaded.");
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Invalid path: " + path);
        }
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

    public SheetCellImp getInnerSystemSheetCell() {
        return sheetCell;
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
            cell.updateVersion(sheetCell.getLatestVersion());
            cell.setEffectiveValue(expression);
        });
        versionControl();
        sheetCell.updateEffectedByAndOnLists();
    }
}