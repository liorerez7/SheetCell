//package CoreParts.impl.InnerSystemComponents;
//
//import CoreParts.api.SheetCell;
//import CoreParts.impl.DtoComponents.DtoCell;
//import CoreParts.impl.DtoComponents.DtoSheetCell;
//import CoreParts.impl.InnerSystemComponents.StateHandler;
//import CoreParts.smallParts.CellLocation;
//import CoreParts.smallParts.CellLocationFactory;
//import GeneratedClasses.STLSheet;
//import Utility.EngineUtilies;
//import CoreParts.api.SheetConvertor;
//import CoreParts.impl.InnerSystemComponents.SheetConvertorImpl;
//import expression.api.EffectiveValue;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Map;
//
//public class FileHandler {
//
//    EngineImpl engine;
//
//    public FileHandler(EngineImpl engine) {
//        this.engine = engine;
//    }
//
//    public void readSheetCellFromXML(String path) throws Exception {
//
//        Path filePath = Paths.get(path);
//
//        // Check if the path is absolute
//        if (!filePath.isAbsolute()) {
//            throw new IllegalArgumentException("Provided path is not absolute. Please provide a full path.");
//        }
//
//        Map<String, CellLocation> mappingCellLocations = CellLocationFactory.getCacheCoordiante();
//        byte[] savedSheetCellState = engine.saveSheetCellState();
//        try {
//
//            CellLocationFactory.clearCache();
//            InputStream in = new FileInputStream(new File(path));
//            STLSheet sheet = EngineUtilies.deserializeFrom(in);
//            SheetConvertor convertor = new SheetConvertorImpl();
//            SheetCellImp sheetCell = (SheetCellImp) convertor.convertSheet(sheet);
//            engine.setSheetCell(sheetCell);
//            engine.setUpSheet();
//
//        } catch (Exception e) {
//
//            engine.restoreSheetCellState(savedSheetCellState);
//            CellLocationFactory.setCacheCoordiante(mappingCellLocations);
//            throw new Exception(e.getMessage());
//        }
//    }
//    public void save(String path) throws Exception, IOException, IllegalArgumentException {
//
//        Path filePath = Paths.get(path);
//
//        // Check if the path is absolute
//        if (!filePath.isAbsolute()) {
//            throw new IllegalArgumentException("Provided path is not absolute. Please provide a full path.");
//        }
//
//        // Check if the file can be created (this also ensures the path is valid)
//        if (Files.exists(filePath)) {
//            if (!Files.isWritable(filePath)) {
//                throw new IOException("No write permission for file: " + filePath.toString());
//            }
//        }
//
//        try (FileOutputStream fileOut = new FileOutputStream(path);
//             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
//            out.writeObject(engine.versionToCellsChanges);
//            out.writeObject(engine.getInnerSystemSheetCell());
//            out.flush();
//        } catch (IOException e) {
//            throw new Exception("Error saving data to " + path + ": " + e.getMessage(), e);
//        }
//    }
//
//    public void load(String path) throws Exception, NoSuchFieldException {
//
//        Path filePath = Paths.get(path);
//        // Check if the path is absolute
//        if (!filePath.isAbsolute()) {
//            throw new Exception("Provided path is not absolute. Please provide a full path.");
//        }
//
//        CellLocationFactory.clearCache();
//        try (FileInputStream fileIn = new FileInputStream(new File(path));
//             ObjectInputStream in = new ObjectInputStream(fileIn)) {
//            Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges = (Map<Integer, Map<CellLocation, EffectiveValue>>) in.readObject();
//            SheetCellImp sheetCell = (SheetCellImp) in.readObject();
//            engine.setSheetCell(sheetCell);
//
//        } catch (IOException | ClassNotFoundException e) {
//            throw new NoSuchFieldException("file not found at this path: " + path);
//        }
//    }
//}