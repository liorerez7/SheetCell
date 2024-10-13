package EngineManager;

import CoreParts.api.SheetManager;
import CoreParts.impl.InnerSystemComponents.SheetManagerImpl;
import DtoComponents.DtoCell;
import DtoComponents.DtoSheetCell;
import DtoComponents.DtoSheetInfoLine;
import loginPage.users.PermissionManager;
import loginPage.users.UserManager;
import smallParts.CellLocation;

import java.io.InputStream;
import java.util.*;

public class Engine {

    private final Map<String, SheetManagerImpl> sheetCells = new HashMap<>();
    private final Set<String> sheetNames = new HashSet<>();
    private final UserManager userManager = new UserManager();
    private final PermissionManager permissionManager = new PermissionManager(userManager);


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

    public synchronized UserManager getUserManager() {
        return userManager;
    }

    public synchronized PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(sheetNames);
    }

    public Set<String> getSheetNames() {
        return sheetNames;
    }

    public Set<DtoSheetInfoLine> getSheetInfos(String userName) {
        Set<DtoSheetInfoLine> sheetInfos  = new HashSet<>();
//        for (String sheetName : sheetNames) {
//            SheetManagerImpl sheetManager = sheetCells.get(sheetName);
//            DtoSheetCell sheetCell = sheetManager.getSheetCell();
//            String ownerName = sheetCell.getOwnerName();
//            String size = sheetCell.getSheetSize();
//            String permissionStatus = PermissionManager.getPermissionStatus();
//            DtoSheetInfoLine sheetInfo = new DtoSheetInfoLine(ownerName, sheetName, size, permissionStatus);
//            sheetInfos.add(sheetInfo);
//        }
        return sheetInfos;
    }
}
