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
import Utility.Exception.DeleteWhileAffectingOtherCellException;
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
            if(sheetCell.isCellPresent(CellLocationFactory.fromCellId(cellId))){
                return new DtoCell(getCell(CellLocationFactory.fromCellId(cellId)));
            }
            else{
                return null;
            }
    }

    @Override
    public Map<Integer, Map<CellLocation, EffectiveValue>> getVersions(){
        return versionToCellsChanges;
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

        //Map<String, CellLocation> mappingCellLocations = CellLocationFactory.getCacheCoordiante();
        byte[] savedSheetCellState = saveSheetCellState();
        byte[] savedCellLocationFactoryState = saveCellLocationFactoryState();
        byte[] savedVersions = saveVersions();

        try {

            versionToCellsChanges.clear();
            sheetCell.clearVersionNumber();
            versionToCellsChanges.put(sheetCell.getLatestVersion(), new HashMap<>());
            InputStream in = new FileInputStream(new File(path));
            STLSheet sheet = EngineUtilies.deserializeFrom(in);
            SheetConvertor convertor = new SheetConvertorImpl();
            sheetCell = (SheetCellImp) convertor.convertSheet(sheet);
            setUpSheet();

        } catch (Exception e) {

            restoreSheetCellState(savedSheetCellState);
            restoreCellLocationFactoryState(savedCellLocationFactoryState);
            restoreVersions(savedVersions);


           // CellLocationFactory.setCacheCoordiante(mappingCellLocations);
            throw new Exception(e.getMessage());
        }


    }

    @Override
    public void updateCell(String newValue, char col, String row) throws
            CycleDetectedException, IllegalArgumentException, RefToUnSetCell {

        byte[] savedSheetCellState = saveSheetCellState();
        byte[] savedCellLocationFactoryState = saveCellLocationFactoryState();

        Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));

        if(!(targetCell.getAffectingOn().isEmpty()) && newValue.isEmpty()) {

            throw new DeleteWhileAffectingOtherCellException(targetCell);
        }

        else if(newValue.isEmpty()){

            updateVersions(targetCell);
            versionControl();
            sheetCell.removeCell(CellLocationFactory.fromCellId(col, row));
        }
        else{

            // Step 1: Serialize and save the current sheetCell

            try {

                Expression expression = CellUtils.processExpressionRec(newValue, targetCell, getInnerSystemSheetCell(), false);
                Expression oldExpression = targetCell.getEffectiveValue(); // old expression
                applyCellUpdates(targetCell, newValue, expression);
                updateVersions(targetCell);
                performGraphOperations();
                versionControl();

            } catch (Exception e) {

                restoreSheetCellState(savedSheetCellState);
                restoreCellLocationFactoryState(savedCellLocationFactoryState);

                throw e;

            }
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
            out.writeObject(CellLocationFactory.getCacheCoordiante());
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

        cells.forEach(cell ->{

            Object obj = cell.getActualValue().getValue();

            cell.setActualValue(sheetCell);

            if(!obj.equals(cell.getActualValue().getValue()))
            {
                cell.updateVersion(sheetCell.getLatestVersion());

            }
        } );
    }


    private void updateVersions(Cell targetCell) {
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

    private byte[] saveVersions() throws IllegalStateException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(versionToCellsChanges);
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save the versions state", e);
        }
    }

    private void restoreVersions(byte[] savedVersions) throws IllegalStateException {
        try {
            if (savedVersions != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(savedVersions);
                ObjectInputStream ois = new ObjectInputStream(bais);
                versionToCellsChanges = (Map<Integer, Map<CellLocation, EffectiveValue>>) ois.readObject();
            }
        } catch (Exception restoreEx) {
            throw new IllegalStateException("Failed to restore the versions state", restoreEx);
        }
    }

    public void load(String path) throws Exception, NoSuchFieldException {


        Path filePath = Paths.get(path);
        // Check if the path is absolute
        if (!filePath.isAbsolute()) {
            throw new Exception("Provided path is not absolute. Please provide a full path.");
        }

        try (FileInputStream fileIn = new FileInputStream(new File(path));
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            versionToCellsChanges = (Map<Integer, Map<CellLocation, EffectiveValue>>) in.readObject();
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
            Expression expression = CellUtils.processExpressionRec(cell.getOriginalValue(), cell, getInnerSystemSheetCell(), false);
            expression.evaluate(sheetCell);
            cell.setEffectiveValue(expression);
            cell.setActualValue(sheetCell);
            cell.updateVersion(sheetCell.getLatestVersion());
        });
        versionControl();
        sheetCell.updateEffectedByAndOnLists();
    }

    private byte[] saveCellLocationFactoryState() throws IllegalStateException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(CellLocationFactory.getCacheCoordiante());
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save the CellLocationFactory state", e);
        }
    }

    private void restoreCellLocationFactoryState(byte[] savedCellLocationFactoryState) throws IllegalStateException {
        try {
            if (savedCellLocationFactoryState != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(savedCellLocationFactoryState);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Map<String, CellLocation> cachedCoordinates = (Map<String, CellLocation>) ois.readObject();
                ois.close();
            }
        } catch (Exception restoreEx) {
            throw new IllegalStateException("Failed to restore the CellLocationFactory state", restoreEx);
        }
    }
}