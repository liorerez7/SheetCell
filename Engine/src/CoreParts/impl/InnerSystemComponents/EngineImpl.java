package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.api.SheetConvertor;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocationFactory;
import GeneratedClasses.STLSheet;
import Utility.CellUtils;
import CoreParts.api.Engine;
import CoreParts.smallParts.CellLocation;
import Utility.EngineUtilies;
import Utility.Exception.CycleDetectedException;
import Utility.Exception.DeleteWhileAffectingOtherCellException;
import Utility.Exception.RefToUnSetCell;
import expression.api.Expression;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EngineImpl implements Engine {

    private SheetCell sheetCell;

    public EngineImpl() {
        sheetCell = new SheetCellImp(0, 0, "Sheet1", 0, 0);
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
    public DtoSheetCell getSheetCell() {return new DtoSheetCell(sheetCell);}

    @Override
    public DtoSheetCell getSheetCell(int versionNumber) {return new DtoSheetCell(sheetCell, versionNumber);}

    @Override
    public void readSheetCellFromXML(String path) throws Exception {

        Path filePath = Paths.get(path);

        if (!filePath.isAbsolute()) {
            throw new IllegalArgumentException("Provided path is not absolute. Please provide a full path.");
        }
        byte[] savedSheetCellState =sheetCell.saveSheetCellState();

        try {
            sheetCell.clearVersionNumber();
            getsheetFromSTL(path);
            sheetCell.setUpSheet();

        } catch (Exception e) {
            restoreSheetCellState(savedSheetCellState);
            throw new Exception(e.getMessage());
        }
    }

    private void getsheetFromSTL(String path) throws FileNotFoundException, JAXBException {
        InputStream in = new FileInputStream(new File(path));
        STLSheet sheet = EngineUtilies.deserializeFrom(in);
        SheetConvertor convertor = new SheetConvertorImpl();
        sheetCell = (SheetCellImp) convertor.convertSheet(sheet);
    }

    @Override
    public void updateCell(String newValue, char col, String row) throws
            CycleDetectedException, IllegalArgumentException, RefToUnSetCell {

        //byte[] savedSheetCellState = sheetCell.saveSheetCellState();

        Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));

        if(newValue.isEmpty()){
            sheetCell.updateVersions(targetCell);
            sheetCell.versionControl();
            sheetCell.removeCell(CellLocationFactory.fromCellId(col, row));
        }
        else{
            try {
                Expression expression = CellUtils.processExpressionRec(newValue, targetCell, getInnerSystemSheetCell(), false);
                Expression oldExpression = targetCell.getEffectiveValue(); // old expression
                sheetCell.applyCellUpdates(targetCell,newValue, expression);
                sheetCell.updateVersions(targetCell);
                sheetCell.performGraphOperations();
                sheetCell.versionControl();

            } catch (Exception e) {
                //restoreSheetCellState(savedSheetCellState);
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

        if (Files.exists(filePath)) {
            if (!Files.isWritable(filePath)) {
                throw new IOException("No write permission for file: " + filePath.toString());
            }
        }
        try (FileOutputStream fileOut = new FileOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(sheetCell);
            out.flush();
        } catch (IOException e) {
            throw new Exception("Error saving data to " + path + ": " + e.getMessage(), e);
        }
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
        try (FileInputStream fileIn = new FileInputStream(new File(path));
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            sheetCell = (SheetCellImp) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new NoSuchFieldException("file not found at this path: " + path);
        }
    }
    public Cell getCell(CellLocation location) {
        // Fetch the cell directly from the map in SheetCellImp
        return sheetCell.getCell(location);
    }

    public SheetCell getInnerSystemSheetCell() {
        return sheetCell;
    }
}