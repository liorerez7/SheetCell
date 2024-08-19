package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import GeneratedClasses.STLSheet;
import Utility.CellUtils;
import CoreParts.api.Engine;
import CoreParts.smallParts.CellLocation;
import Utility.EngineUtilies;
import expression.Operation;
import expression.api.Expression;
import expression.api.processing.ExpressionParser;
import expression.impl.Processing.ExpressionParserImpl;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class EngineImpl implements Engine {

    private SheetCellImp sheetCellImp = new SheetCellImp(
            4, 3, "lior and niv sheet cell", 3, 15
    );

    @Override
    public DtoCell getRequestedCell(String cellId) {
        return new DtoCell(getCell(CellLocation.fromCellId(cellId.charAt(0), cellId.charAt(1))));
    }

    public Cell getCell(CellLocation location) {
        // Fetch the cell directly from the map in SheetCellImp
        return sheetCellImp.getCell(location);

    }

    @Override
    public DtoSheetCell getSheetCell() {
        return new DtoSheetCell(sheetCellImp);
    }

    public SheetCellImp getInnerSystemSheetCell() {
        return sheetCellImp;
    }

    @Override
    public SheetCellImp getSheetCell(int versionNumber) {
        return null;
    }

    @Override
    public void readSheetCellFromXML(String path) throws FileNotFoundException, JAXBException {
        InputStream in = new FileInputStream(new File(path));
        STLSheet sheet = EngineUtilies.deserializeFrom(in);
    }

    @Override
    public void updateCell(String newValue, char col, char row) {

        Cell targetCell = getCell(CellLocation.fromCellId(col, row));

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

            CellUtils.recalculateCellsRec(targetCell, oldExpression);

        }
        catch(Exception illegalArgumentException){
            throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed");
        }
    }

}