//package CoreParts.smallParts;
//
//import CoreParts.api.Cell;
//import CoreParts.api.SheetCell;
//import CoreParts.impl.DtoComponents.DtoCell;
//import CoreParts.impl.DtoComponents.DtoSheetCell;
//import CoreParts.impl.InnerSystemComponents.EngineImpl;
//import CoreParts.impl.InnerSystemComponents.SheetCellImp;
//import Utility.CellUtils;
//import Utility.Exception.CycleDetectedException;
//import Utility.Exception.CellCantBeEvaluated;
//import Utility.RefDependencyGraph;
//import expression.api.Expression;
//
//import java.util.List;
//
//public class SheetSetupHandler {
//
//    EngineImpl engine;
//
//
//    public SheetSetupHandler(EngineImpl engine) {
//        this.engine = engine;
//    }
//
//
//    public List<Cell> getTopologicalSortOfExpressions() throws CycleDetectedException{
//        SheetCellImp sheetCell = engine.getInnerSystemSheetCell();
//        RefDependencyGraph graph = sheetCell.getRefDependencyGraph();
//        List<Cell> topologicalOrder;
//        topologicalOrder = graph.topologicalSort();
//        return topologicalOrder;
//    }
//
//    public void setUpSheet() throws CycleDetectedException, CellCantBeEvaluated {
//        SheetCellImp sheetCell = engine.getInnerSystemSheetCell();
//        sheetCell.createRefDependencyGraph();
//        List<Cell> topologicalOrder = getTopologicalSortOfExpressions();
//        topologicalOrder.forEach(cell -> {
//            Expression expression = CellUtils.processExpressionRec(cell.getOriginalValue(), cell, engine.getInnerSystemSheetCell(), false);
//            expression.evaluate(sheetCell);
//            cell.setEffectiveValue(expression);
//            cell.setActualValue(sheetCell);
//            cell.updateVersion(sheetCell.getLatestVersion());
//        });
//        engine.versionControl();
//        sheetCell.updateEffectedByAndOnLists();
//    }
//}