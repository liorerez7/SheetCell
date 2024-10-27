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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
        return new SheetManagerImpl(sheetCellOnVersion);
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
    }

    private void saveSheetVersionUsingSerialize(){
        byte[] savedVersion = sheetCell.saveSheetCellState();
        SheetCellImp sheetCellInVersionZero = restoreSheetCellState(savedVersion, true);
        sheetCellVersions.put(sheetCell.getLatestVersion(), sheetCellInVersionZero);
    }
}