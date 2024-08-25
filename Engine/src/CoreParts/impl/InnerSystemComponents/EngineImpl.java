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
import Utility.Exception.CellCantBeEvaluated;
import Utility.Exception.CycleDetectedException;
import Utility.Exception.RefToUnSetCell;
import Utility.RefDependencyGraph;
import expression.api.EffectiveValue;
import expression.api.Expression;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        if (CellLocationFactory.isContained(cellId)) {
            return new DtoCell(getCell(CellLocationFactory.fromCellId(cellId)));
        } else {
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


        Path filePath = Paths.get(path);

        // Check if the path is absolute
        if (!filePath.isAbsolute()) {
            throw new IllegalArgumentException("Provided path is not absolute. Please provide a full path.");
        }

        Map<String, CellLocation> mappingCellLocations = CellLocationFactory.getCacheCoordiante();
        byte[] savedSheetCellState = saveSheetCellState();

        try {

            CellLocationFactory.clearCache();
            InputStream in = new FileInputStream(new File(path));
            STLSheet sheet = EngineUtilies.deserializeFrom(in);
            SheetConvertor convertor = new SheetConvertorImpl();
            sheetCell = (SheetCellImp) convertor.convertSheet(sheet);
            setUpSheet();

        } catch (Exception e) {

            restoreSheetCellState(savedSheetCellState);
            CellLocationFactory.setCacheCoordiante(mappingCellLocations);
            throw new Exception(e.getMessage());
        }


    }

    @Override
    public void updateCell(String newValue, char col, String row) throws
            CycleDetectedException, IllegalArgumentException, RefToUnSetCell {

        // Step 1: Serialize and save the current sheetCell
        byte[] savedSheetCellState = saveSheetCellState();

        try {

            Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));
            Expression expression = CellUtils.processExpressionRec(newValue, targetCell, getInnerSystemSheetCell());
            Expression oldExpression = targetCell.getEffectiveValue(); // old expression

            applyCellUpdates(targetCell, newValue, expression);
            performGraphOperations();
            updateVersions(targetCell, oldExpression);
            versionControl();

        } catch (Exception e) {

            restoreSheetCellState(savedSheetCellState);
            CellLocationFactory.removeKey(col + row);
            throw e;

        }
    }

    @Override
    public void save(String path) throws Exception, IOException, IllegalArgumentException {


        Path filePath = Paths.get(path);

        // Check if the path is absolute
        if (!filePath.isAbsolute()) {
            throw new IllegalArgumentException("Provided path is not absolute. Please provide a full path.");
        }

        // Check if the file can be created (this also ensures the path is valid)
        if (Files.exists(filePath)) {
            if (!Files.isWritable(filePath)) {
                throw new IOException("No write permission for file: " + filePath.toString());
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(versionToCellsChanges);
            out.writeObject(sheetCell);
            out.flush();
        } catch (IOException e) {
            throw new Exception("Error saving data to " + path + ": " + e.getMessage(), e);
        }
    }

    private byte[] saveSheetCellState() throws IllegalStateException {

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
        targetCell.setActualValue(sheetCell);
    }

    private void performGraphOperations() throws CycleDetectedException, CellCantBeEvaluated {

        sheetCell.createRefDependencyGraph();

        List<Cell> cells = sheetCell.getRefDependencyGraph().topologicalSort();

        sheetCell.updateEffectedByAndOnLists();

        cells.forEach(cell -> cell.setActualValue(sheetCell));
    }


    private void updateVersions(Cell targetCell, Expression oldExpression) {
        sheetCell.updateVersion();
        targetCell.updateVersion(sheetCell.getLatestVersion());
    }

    private void restoreSheetCellState(byte[] savedSheetCellState) throws IllegalStateException {

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

    public void load(String path) throws Exception, NoSuchFieldException {


        Path filePath = Paths.get(path);
        // Check if the path is absolute
        if (!filePath.isAbsolute()) {
            throw new Exception("Provided path is not absolute. Please provide a full path.");
        }

        CellLocationFactory.clearCache();
        try (FileInputStream fileIn = new FileInputStream(new File(path));
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges = (Map<Integer, Map<CellLocation, EffectiveValue>>) in.readObject();
            sheetCell = (SheetCellImp) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new NoSuchFieldException("file not found at this path: " + path);
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
                changedCells.put(location, cell.getEffectiveValue().evaluate(sheetCell));
        }
    }

    public SheetCellImp getInnerSystemSheetCell() {
        return sheetCell;
    }

    public List<Cell> getTopologicalSortOfExpressions() throws CycleDetectedException{
        RefDependencyGraph graph = sheetCell.getRefDependencyGraph();
        List<Cell> topologicalOrder;
        topologicalOrder = graph.topologicalSort();
        return topologicalOrder;
    }

    private void setUpSheet() throws CycleDetectedException, CellCantBeEvaluated {
        sheetCell.createRefDependencyGraph();
        List<Cell> topologicalOrder = getTopologicalSortOfExpressions();
        topologicalOrder.forEach(cell -> {
            Expression expression = CellUtils.processExpressionRec(cell.getOriginalValue(), cell, getInnerSystemSheetCell());
            expression.evaluate(sheetCell);
            cell.setEffectiveValue(expression);
            cell.setActualValue(sheetCell);
            cell.updateVersion(sheetCell.getLatestVersion());
        });
        versionControl();
        sheetCell.updateEffectedByAndOnLists();
    }
}