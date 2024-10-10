package EngineManager;

import CoreParts.api.SheetManager;
import CoreParts.impl.InnerSystemComponents.SheetManagerImpl;
import DtoComponents.DtoCell;
import DtoComponents.DtoSheetCell;
import smallParts.CellLocation;

import java.io.InputStream;
import java.util.*;

public class Engine {

    private final Map<String, SheetManagerImpl> sheetCells = new HashMap<>();
    private final Set<String> sheetNames = new HashSet<>();


    public synchronized SheetManager getSheetCell(InputStream sheetInputStream) {

        SheetManagerImpl sheetManager = new SheetManagerImpl();
        try {
            sheetManager.readSheetCellFromXML(sheetInputStream);
            String currentSheetName = sheetManager.getSheetCell().getSheetName();
            if (sheetNames.contains(currentSheetName)) {
                return sheetCells.get(currentSheetName);
            } else {
                sheetNames.add(currentSheetName);
                sheetCells.put(currentSheetName, sheetManager);
                return sheetManager;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized SheetManager getSheetCell(String SheetName) {

        SheetManagerImpl sheetManager = new SheetManagerImpl();
        try {
            if (sheetNames.contains(SheetName)) {
                return sheetCells.get(SheetName);
            } else {
                sheetNames.add(SheetName);
                sheetCells.put(SheetName, sheetManager);
                return sheetManager;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void addUser(String username) {
        sheetNames.add(username);
    }

    public synchronized void removeUser(String username) {
        sheetNames.remove(username);
    }

    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(sheetNames);
    }

    public boolean isUserExists(String username) {
        return sheetNames.contains(username);
    }

    public Set<String> getSheetNames() {
        return sheetNames;
    }
}
