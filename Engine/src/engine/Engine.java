package engine;

import engine.core_parts.api.SheetManager;
import engine.core_parts.impl.SheetManagerImpl;
import engine.login.users.PermissionManager;
import engine.login.users.SheetInfosManager;
import engine.login.users.UserManager;

import java.io.InputStream;
import java.util.*;

public class Engine {

    private final Map<String, SheetManagerImpl> sheetCells = new HashMap<>();
    private final Set<String> sheetNames = new HashSet<>();
    private final UserManager userManager = new UserManager();
    private final PermissionManager permissionManager = new PermissionManager(userManager);
    private final SheetInfosManager sheetInfosManager = new SheetInfosManager(permissionManager);

    public synchronized SheetManager getSheetCell(InputStream sheetInputStream) {

        SheetManagerImpl sheetManager = new SheetManagerImpl();
        try {
            sheetManager.readSheetCellFromXML(sheetInputStream);
            String currentSheetName = sheetManager.getSheetCell().getSheetName();
            if (sheetNames.contains(currentSheetName)) {
                return sheetCells.get(currentSheetName);
            } else {
                sheetNames.add(currentSheetName);
                String sheetSize = sheetManager.getSheetCell().getSheetSize();
                sheetInfosManager.AddSheet(currentSheetName, sheetSize);
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

    public SheetInfosManager getSheetInfosManager() {
        return sheetInfosManager;
    }
}
