//package CoreParts.impl.InnerSystemComponents;
//
//import CoreParts.api.SheetCell;
//import CoreParts.smallParts.CellLocation;
//import CoreParts.smallParts.CellLocationFactory;
//
//import java.io.*;
//import java.util.Map;
//
//public class StateHandler {
//
//
//    EngineImpl engine;
//
//    public StateHandler(EngineImpl engine) {
//        this.engine = engine;
//    }
//
//    public byte[] saveSheetCellState() throws IllegalStateException {
//
//        SheetCellImp sheetCell = engine.getInnerSystemSheetCell();
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(sheetCell);
//            oos.close();
//            return baos.toByteArray();
//        } catch (Exception e) {
//            throw new IllegalStateException("Failed to save the sheetCell state", e);
//        }
//    }
//
//    public void restoreSheetCellState(byte[] savedSheetCellState) throws IllegalStateException {
//        SheetCellImp sheetCell = engine.getInnerSystemSheetCell();
//        try {
//            if (savedSheetCellState != null) {
//                ByteArrayInputStream bais = new ByteArrayInputStream(savedSheetCellState);
//                ObjectInputStream ois = new ObjectInputStream(bais);
//                sheetCell = (SheetCellImp) ois.readObject(); // Restore the original sheetCell
//            }
//        } catch (Exception restoreEx) {
//            throw new IllegalStateException("Failed to restore the original sheetCell state", restoreEx);
//        }
//    }
//
//    public byte[] saveCellLocationFactoryState() throws IllegalStateException {
//        SheetCellImp sheetCell = engine.getInnerSystemSheetCell();
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(CellLocationFactory.getCacheCoordiante());
//            oos.close();
//            return baos.toByteArray();
//        } catch (Exception e) {
//            throw new IllegalStateException("Failed to save the CellLocationFactory state", e);
//        }
//    }
//
//    public void restoreCellLocationFactoryState(byte[] savedCellLocationFactoryState) throws IllegalStateException {
//
//        try {
//            if (savedCellLocationFactoryState != null) {
//                ByteArrayInputStream bais = new ByteArrayInputStream(savedCellLocationFactoryState);
//                ObjectInputStream ois = new ObjectInputStream(bais);
//                Map<String, CellLocation> cachedCoordinates = (Map<String, CellLocation>) ois.readObject();
//                CellLocationFactory.setCacheCoordiante(cachedCoordinates); // Restore the cache
//                ois.close();
//            }
//        } catch (Exception restoreEx) {
//            throw new IllegalStateException("Failed to restore the CellLocationFactory state", restoreEx);
//        }
//    }
//}