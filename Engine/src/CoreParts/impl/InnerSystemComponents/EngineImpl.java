package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.SheetConvertor;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocationFactory;
import GeneratedClasses.STLSheet;
import Utility.CellUtils;
import CoreParts.api.Engine;
import CoreParts.smallParts.CellLocation;
import Utility.EngineUtilies;
import expression.Operation;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.processing.ExpressionParser;
import expression.impl.Processing.ExpressionParserImpl;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EngineImpl implements Engine {
    Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges = new HashMap<>();

    public EngineImpl() {
        versionToCellsChanges.put(sheetCell.getLatestVersion(), new HashMap<>());
    }

    private SheetCellImp sheetCell = new SheetCellImp(
            4, 3, "lior and niv sheet cell", 3, 15
    );

    @Override
    public DtoCell getRequestedCell(String cellId, boolean updateCell) {
        if (updateCell)
            return new DtoCell(getCell(CellLocationFactory.fromCellId(cellId)));
        else
            if (!CellLocationFactory.isContained(cellId))
            {
                throw new IllegalArgumentException("cell does not exist(you can create a cell using the updateCell command)");
            }
        return null;
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
    @Override
    public void readSheetCellFromXML(String path) throws FileNotFoundException, JAXBException {
        InputStream in = new FileInputStream(new File(path));
        STLSheet sheet = EngineUtilies.deserializeFrom(in);
        SheetConvertor convertor = new SheetConvertorImpl();
        sheetCell = (SheetCellImp) convertor.convertSheet(sheet);
    }

    @Override
    public void updateCell(String newValue, char col, char row) {
        Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));

        Set<Cell> CloneAffectedBy = new HashSet<>();

        Expression expression = CellUtils.processExpressionRec(newValue,targetCell,getInnerSystemSheetCell(), CloneAffectedBy);

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
            targetCell.setOriginalValue(newValue);
            Expression oldExpression = targetCell.getEffectiveValue(); // old expression
            targetCell.setEffectiveValue(expression);
            CellUtils.updateAffectedByAndOnLists(targetCell, CloneAffectedBy);
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