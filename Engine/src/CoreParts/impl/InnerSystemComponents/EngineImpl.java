package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoLocation;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EngineImpl implements Engine {
    Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges = new HashMap<>();
    public EngineImpl() {
        versionToCellsChanges.put(sheetCell.getLatestVersion(),new HashMap<>());
    }
    private SheetCellImp sheetCell = new SheetCellImp(
            4, 3, "lior and niv sheet cell", 3, 15
    );

    @Override
    public DtoCell getRequestedCell(String cellId) {
        return new DtoCell(getCell(CellLocationFactory.fromCellId(cellId)));
    }
    public Cell getCell(CellLocation location) {
        // Fetch the cell directly from the map in SheetCellImp
        return sheetCell.getCell(location);

    }
    private void versionControl() {
        int sheetCellLatestVersion = sheetCell.getLatestVersion();
        versionToCellsChanges.put(sheetCellLatestVersion,new HashMap<>());
        Set<DtoLocation> markedLocations = new HashSet<>();
        Map<CellLocation, EffectiveValue> changedCells = versionToCellsChanges.get(sheetCellLatestVersion);
        while (markedLocations.size() < sheetCell.getActiveCellsCount()) {
            for (Map.Entry<CellLocation, Cell> entry : sheetCell.getSheetCell().entrySet()) {
                CellLocation location = entry.getKey();
                Cell cell = entry.getValue();
                // Check if the cell's latest version matches the sheet's latest version
                if (cell.getLatestVersion() == sheetCellLatestVersion) {  // Assuming Cell has a getVersion() method// Replace with your logic to calculate the effective value
                    changedCells.put(location, cell.getEffectiveValue().evaluate());
                }
            }
            sheetCellLatestVersion--;
            changedCells = versionToCellsChanges.get(sheetCellLatestVersion);
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

}