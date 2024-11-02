package engine.core_parts.impl;

import engine.core_parts.api.Cell;
import engine.core_parts.api.sheet.SheetCell;
import engine.core_parts.api.SheetConvertor;
import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import ex2.STLSheet;
import engine.utilities.CellUtils;
import engine.core_parts.api.SheetManager;
import engine.utilities.EngineUtilities;
import engine.utilities.exception.CycleDetectedException;
import engine.utilities.exception.RefToUnSetCellException;

import engine.expression.api.Expression;
import jakarta.xml.bind.JAXBException;
import dto.small_parts.CellLocation;
import dto.small_parts.CellLocationFactory;

import java.io.*;
import java.util.*;


public class SheetManagerImpl implements SheetManager {

    private SheetCell sheetCell;
    private byte[] savedSheetCellState;
    private Map<Integer, SheetCellImp> sheetCellVersions = new HashMap<>();

    public SheetManagerImpl() {
        sheetCell = new SheetCellImp(0, 0, "Sheet1", 0, 0, null);
        byte[] savedVersion = sheetCell.saveSheetCellState();
        SheetCellImp sheetCellInVersionZero = restoreSheetCellState(savedVersion, true);
        sheetCellVersions.put(0, sheetCellInVersionZero);
    }

    public SheetManagerImpl(SheetCellImp sheetCell) {
        this.sheetCell = sheetCell;
    }

    @Override
    public SheetManagerImpl createSheetCellOnlyForRunTime(int versionNumber) {
        SheetCellImp sheetCellOnVersion =  sheetCellVersions.get(versionNumber);
        byte[] saved = sheetCellOnVersion.saveSheetCellState();
        SheetCellImp sheetCellInVersion = restoreSheetCellState(saved, true);
        return new SheetManagerImpl(sheetCellInVersion);
    }

    public DtoSheetCell getSheetCell() {
        Map<String,DtoCell> cellIdToDtoCell = new HashMap<>();

        for (Map.Entry<CellLocation, Cell> entry : sheetCell.getSheetCell().entrySet()) {
            DtoCell dtoCell = new DtoCell(entry.getValue());
            cellIdToDtoCell.put(entry.getKey().getCellId(),dtoCell);

        }

        return new DtoSheetCell(sheetCell, cellIdToDtoCell);
    }

    @Override
    public DtoSheetCell getSheetCell(int versionNumber) {
        return new DtoSheetCell(sheetCell, versionNumber);
    }

    @Override
    public void readSheetCellFromXML(InputStream path) throws Exception {


        byte[] savedSheetCellState = sheetCell.saveSheetCellState();

        try {
            sheetCell.clearVersionNumber();
            getSheetFromSTL(path);
            sheetCell.setUpSheet();

            saveSheetVersionUsingSerialize();

        } catch (Exception e) {
            restoreSheetCellState(savedSheetCellState);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void UpdateNewRange(String name, String range) throws IllegalArgumentException {
        sheetCell.updateNewRange(name, range);
    }

    @Override
    public void deleteRange(String name) {
        sheetCell.deleteRange(name);
    }

    @Override
    public List<CellLocation> getRequestedRange(String name) {
        return sheetCell.getRequestedRange(name);
    }

    @Override
    public void updateCell(String newValue, char col, String row) throws
            CycleDetectedException, IllegalArgumentException, RefToUnSetCellException {

        byte[] savedSheetCellState = sheetCell.saveSheetCellState();

        if (!sheetCell.isCellPresent(CellLocationFactory.fromCellId(col, row)) && newValue.isEmpty()) {
            return;
        }

        Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));

        try {
            Expression expression = CellUtils.processExpressionRec(newValue, targetCell, getInnerSystemSheetCell(), false);
            sheetCell.applyCellUpdates(targetCell, newValue, expression);
            sheetCell.updateVersions(targetCell);
            sheetCell.performGraphOperations();
            sheetCell.versionControl();

            saveSheetVersionUsingSerialize();

        } catch (Exception e) {
            restoreSheetCellState(savedSheetCellState);


            throw e;
        }
    }


    private void updateCellForReplacedFunction(String newValue, char col, String row) throws
            CycleDetectedException, IllegalArgumentException, RefToUnSetCellException {

        byte[] savedSheetCellState = sheetCell.saveSheetCellState();

        if (!sheetCell.isCellPresent(CellLocationFactory.fromCellId(col, row)) && newValue.isEmpty()) {
            return;
        }

        Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));

        try {
            Expression expression = CellUtils.processExpressionRec(newValue, targetCell, getInnerSystemSheetCell(), false);
            sheetCell.applyCellUpdates(targetCell, newValue, expression);
            sheetCell.updateVersions(targetCell);
            sheetCell.performGraphOperations();
            sheetCell.versionControl();
            saveSheetVersionUsingSerialize();
        } catch (Exception e) {
            restoreSheetCellState(savedSheetCellState);

        }
    }

    public void updateReplacedCells(String newValue, Set<CellLocation> newValueLocations){

        try{
            for(CellLocation location : newValueLocations){
                updateCellForReplacedFunction(newValue, location.getVisualColumn(), location.getVisualRow());
                sheetCell.setVersion(sheetCell.getLatestVersion() - 1); // removing the version
            }
            sheetCell.setVersion(sheetCell.getLatestVersion() + 1); // removing the version
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public void updateReplacedCells(Map<String, String> newCellsToBeUpdate) {

        newCellsToBeUpdate.forEach((key, value) -> {
            char col = key.charAt(0);
            String row = key.substring(1);
            try {
                updateCellForReplacedFunction(value, col, row);
                sheetCell.setVersion(sheetCell.getLatestVersion() - 1); // removing the version
            } catch (Exception e) {
                throw e;
            }
        });

        sheetCell.setVersion(sheetCell.getLatestVersion() + 1); // removing the version
    }

    @Override
    public Map<String, String> getPredictionsForSheet(String startingRangeCellLocation,
                                                      String endingRangeCellLocation,
                                                      String extendedRangeCellLocation,
                                                      Map<String, String> originalValuesByOrder) {

        List<String> initialStringsByOrder = new ArrayList<>();
        Map<String, String> result = new HashMap<>();

        int count = 0;

        // Determine if the range is column-based or row-based
        char startColumn = startingRangeCellLocation.charAt(0);
        char endColumn = endingRangeCellLocation.charAt(0);
        int startRow = Integer.parseInt(startingRangeCellLocation.substring(1));
        int endRow = Integer.parseInt(endingRangeCellLocation.substring(1));

        List<String> targetCells = new ArrayList<>();

        // Case 1: Row-based range (spanning columns, fixed row)
        if (startRow == endRow) {
            for (char col = startColumn; col <= endColumn; col++) {
                String cell = col + String.valueOf(startRow);
                if (originalValuesByOrder.containsKey(cell)) {
                    initialStringsByOrder.add(originalValuesByOrder.get(cell));
                }
                targetCells.add(cell); // Add to target cells list
            }

            // Extend the range up to extendedRangeCellLocation if applicable
            if (extendedRangeCellLocation != null) {
                char extendedEndColumn = extendedRangeCellLocation.charAt(0);
                count = extendedEndColumn - startColumn + 1; // Number of extra columns to extend

                for (char col = (char) (endColumn + 1); col <= extendedEndColumn; col++) {
                    String cell = col + String.valueOf(startRow);
                    targetCells.add(cell); // Add extended cells to target cells list
                }
            }

        }
        // Case 2: Column-based range (spanning rows, fixed column)
        else if (startColumn == endColumn) {
            for (int row = startRow; row <= endRow; row++) {
                String cell = startColumn + String.valueOf(row);
                if (originalValuesByOrder.containsKey(cell)) {
                    initialStringsByOrder.add(originalValuesByOrder.get(cell));
                }
                targetCells.add(cell); // Add to target cells list
            }

            // Extend the range up to extendedRangeCellLocation if applicable
            if (extendedRangeCellLocation != null) {
                int extendedEndRow = Integer.parseInt(extendedRangeCellLocation.substring(1));
                count = extendedEndRow - startRow + 1; // Number of extra rows to extend

                for (int row = endRow + 1; row <= extendedEndRow; row++) {
                    String cell = startColumn + String.valueOf(row);
                    targetCells.add(cell); // Add extended cells to target cells list
                }
            }
        }

        // Generate predictions
        List<String> newList = SequencePredictor.predictNext(initialStringsByOrder, count);

        // Populate result map with predictions
        int newValueIndex = 0;
        for (String cell : targetCells) {
            if (newValueIndex < newList.size()) {
                result.put(cell, newList.get(newValueIndex));
                newValueIndex++;
            }
        }

        return result;
    }

    public Map<String,String> updateMultipleCells(Map<String, String> resultStrings, Map<String, String> originalValuesByOrder) {

        Map<String,String> theCellsThatActuallyUpdated = new HashMap<>();

        for (Map.Entry<String, String> entry : resultStrings.entrySet()) {
            char col = entry.getKey().charAt(0);
            String row = entry.getKey().substring(1);
            try {
                String isStringInOriginalValues = originalValuesByOrder.get(entry.getKey());
                if(isStringInOriginalValues == null){

                    Cell cell = sheetCell.getSheetCell().get(CellLocationFactory.fromCellId(entry.getKey()));
                    if((cell == null) || (cell).getOriginalValue().isEmpty() || cell.getOriginalValue().equals(entry.getValue())) {

                        updateCell(entry.getValue(), col, row);
                        theCellsThatActuallyUpdated.put(entry.getKey(), entry.getValue());
                    }
                    else{
                        break;
                    }
                }else{
                    updateCell(entry.getValue(), col, row);
                    theCellsThatActuallyUpdated.put(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
                theCellsThatActuallyUpdated = null;
                break;
            }
        }

        return theCellsThatActuallyUpdated;
    }

    @Override
    public void saveCurrentSheetCellState() {
        savedSheetCellState = sheetCell.saveSheetCellState();
    }

    @Override
    public void restoreSheetCellState() {
        restoreSheetCellState(savedSheetCellState);
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

    private SheetCellImp restoreSheetCellState(byte[] savedSheetCellState, boolean b) throws IllegalStateException {
        try {
            if (savedSheetCellState != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(savedSheetCellState);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return (SheetCellImp) ois.readObject(); // Restore the original sheetCell
            }
        } catch (Exception restoreEx) {
            throw new IllegalStateException("Failed to restore the original sheetCell state", restoreEx);
        }
        return null;
    }

    public Cell getCell(CellLocation location) {
        // Fetch the cell directly from the map in SheetCellImp
        return sheetCell.getCell(location);
    }

    public SheetCell getInnerSystemSheetCell() {
        return sheetCell;
    }

    private void getSheetFromSTL(InputStream path) throws FileNotFoundException, JAXBException {
//        InputStream in = new FileInputStream(path);
        STLSheet sheet = EngineUtilities.deserializeFrom(path);
        SheetConvertor convertor = new SheetConvertorImpl();
        sheetCell = convertor.convertSheet(sheet);
    }

    @Override
    public Set<String> getAllRangeNames() {
        return sheetCell.getAllRangeNames();
    }

    public void createEmptyNewSheet(String sheetName, int cellWidth, int cellLength, int numColumns, int numRows) {
        sheetCell = new SheetCellImp(numRows, numColumns, sheetName, cellLength, cellWidth, null);
        byte[] savedVersion = sheetCell.saveSheetCellState();
        SheetCellImp sheetCellInVersionZero = restoreSheetCellState(savedVersion, true);
        sheetCellVersions.put(0, sheetCellInVersionZero);
    }

    private void saveSheetVersionUsingSerialize(){
        byte[] savedVersion = sheetCell.saveSheetCellState();
        SheetCellImp sheetCellInVersionZero = restoreSheetCellState(savedVersion, true);
        sheetCellVersions.put(sheetCell.getLatestVersion(), sheetCellInVersionZero);
    }
}