package engine;

import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;
import dto.small_parts.UpdateCellInfo;
import engine.core_parts.api.SheetManager;
import engine.core_parts.impl.SheetManagerImpl;
import engine.dashboard.chat.ChatManager;
import engine.login.users.PermissionManager;
import engine.login.users.SheetInfosManager;
import engine.login.users.UserManager;

import java.io.InputStream;
import java.util.*;

public class Engine {

    private final Map<String, SheetManagerImpl> sheetCells = new HashMap<>();
    private final Set<String> sheetNames = new HashSet<>();
    private final UserManager userManager = new UserManager();
    private final ChatManager chatManager = new ChatManager();
    private final PermissionManager permissionManager = new PermissionManager();
    private Map<String, SheetManagerImpl> usernameToTemporarySheetManagerRunTime = new HashMap<>();
    private Map<String, SheetManagerImpl> usernameToTemporarySheetManagerPredict = new HashMap<>();
    private Map<String, SheetManagerImpl> usernameToTemporarySheetManagerFindAndReplace = new HashMap<>();


    private Map<String,Map<Integer, UpdateCellInfo>> versionToCellInfo = new HashMap<>();

    private final SheetInfosManager sheetInfosManager = new SheetInfosManager(permissionManager, userManager);

    private Map<String, Map<CellLocation, String>> sheetNameToCellLocationToUserName = new HashMap<>();

    public Map<Integer, UpdateCellInfo> getVersionToCellInfo(String sheetName) {
        return versionToCellInfo.get(sheetName);
    }

    public synchronized SheetManager getSheetCell(InputStream sheetInputStream, String userName) {

        SheetManagerImpl sheetManager = new SheetManagerImpl();
        try {
            sheetManager.readSheetCellFromXML(sheetInputStream);
            String currentSheetName = sheetManager.getSheetCell().getSheetName();
            if (sheetNames.contains(currentSheetName)) {
                return null;
            } else {
                sheetNames.add(currentSheetName);
                String sheetSize = sheetManager.getSheetCell().getSheetSize();
                DtoSheetCell dtoSheetCell = sheetManager.getSheetCell();
                initializeCellLocationToUserName(dtoSheetCell, userName);
                sheetInfosManager.AddSheet(currentSheetName, sheetSize);
                sheetCells.put(currentSheetName, sheetManager);
                versionToCellInfo.computeIfAbsent(currentSheetName, k -> new HashMap<>());
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

                versionToCellInfo.computeIfAbsent(SheetName, k -> new HashMap<>());

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

    public synchronized ChatManager getChatManager() {
        return chatManager;
    }

    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(sheetNames);
    }

    public SheetInfosManager getSheetInfosManager() {
        return sheetInfosManager;
    }

    public Map<CellLocation, String> getCellLocationToUserName(String sheetName) {
        return sheetNameToCellLocationToUserName.get(sheetName);
    }

    private void initializeCellLocationToUserName(DtoSheetCell dtoSheetCell, String userName) {
        dtoSheetCell.getViewSheetCell().forEach((key, value) -> {
            if(!value.getValue().toString().equals("")){
                sheetNameToCellLocationToUserName.putIfAbsent(dtoSheetCell.getSheetName(), new HashMap<>());
                sheetNameToCellLocationToUserName.get(dtoSheetCell.getSheetName()).put(key, userName);
            }
        });
    }

    public void updateUsersInCells(DtoSheetCell beforeUpdateDtoSheetCell, DtoSheetCell afterUpdateDtoSheetCell, String userName) {
        // Retrieve the previous and new maps of CellLocation to EffectiveValue
        Map<CellLocation, EffectiveValue> prevMap = beforeUpdateDtoSheetCell.getViewSheetCell();
        Map<CellLocation, EffectiveValue> newMap = afterUpdateDtoSheetCell.getViewSheetCell();

        // Get the sheet name
        String sheetName = beforeUpdateDtoSheetCell.getSheetName();

        // Ensure the map for this sheet exists, if not, create a new map
        Map<CellLocation, String> cellLocationToUserName = sheetNameToCellLocationToUserName.computeIfAbsent(sheetName, k -> new HashMap<>());

        // Check for changes in the cells that exist in both prevMap and newMap
        for (Map.Entry<CellLocation, EffectiveValue> entry : prevMap.entrySet()) {
            CellLocation cellLocation = entry.getKey();
            EffectiveValue prevValue = entry.getValue();
            EffectiveValue newValue = newMap.get(cellLocation);

            // If the value has changed, update the map with the username
            if (newValue != null && !prevValue.getValue().equals(newValue.getValue())) {
                cellLocationToUserName.put(cellLocation, userName);
            }
        }

        // Now handle the case where there are new cells in newMap that do not exist in prevMap
        for (Map.Entry<CellLocation, EffectiveValue> entry : newMap.entrySet()) {
            CellLocation cellLocation = entry.getKey();

            // If the cell is new (not in prevMap), add it to the result map
            if (!prevMap.containsKey(cellLocation)) {
                cellLocationToUserName.put(cellLocation, userName);
            }
        }
    }

    public synchronized void createNewSheet(String sheetName, int cellWidth, int cellLength, int numColumns, int numRows, String userName) {
        SheetManagerImpl sheetManager = new SheetManagerImpl();
        sheetManager.createEmptyNewSheet(sheetName, cellWidth, cellLength, numColumns, numRows);
        DtoSheetCell dtoSheetCell = sheetManager.getSheetCell();
        initializeCellLocationToUserName(dtoSheetCell, userName);
        sheetNames.add(sheetName);
        sheetCells.put(sheetName, sheetManager);
        sheetInfosManager.AddSheet(sheetName, dtoSheetCell.getSheetSize());
        versionToCellInfo.computeIfAbsent(sheetName, k -> new HashMap<>());
    }

    public void addTemporarySheetToRunTime(String username, int versionNumber, String sheetName) {
        usernameToTemporarySheetManagerRunTime.put(username, sheetCells.get(sheetName).createSheetCellOnlyForRunTime(versionNumber));
    }

    public SheetManager getTemporarySheetManagerRunTime(String username) {
        return usernameToTemporarySheetManagerRunTime.get(username);
    }

    public void removeTemporarySheetRunTime(String userName) {
        usernameToTemporarySheetManagerRunTime.remove(userName);
    }

//    public void addTemporarySheetManagerPredict(String username, int versionNumber, String sheetName) {
//        usernameToTemporarySheetManagerRunTime.put(username, sheetCells.get(sheetName).createSheetCellOnlyForRunTime(versionNumber));
//    }
//
//    public SheetManager getTemporarySheetManagerPredict(String username) {
//        return usernameToTemporarySheetManagerRunTime.get(username);
//    }
//
//    public void removeTemporarySheetManagerPredict(String userName) {
//        usernameToTemporarySheetManagerRunTime.remove(userName);
//    }
}
