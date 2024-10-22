package engine.login.users;

import dto.components.DtoSheetInfoLine;

import java.util.*;

public class SheetInfosManager {

    private final Map<String, Set<DtoSheetInfoLine>> usernameToSheetInfos = new HashMap<>();
    private Map<String, String> sheetNamesToSheetSize = new HashMap<>();
    private Map<String, String> sheetNamesToSheetOwner = new HashMap<>();
    private PermissionManager permissionManager;
    private UserManager userManager;


    public SheetInfosManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
        userManager = permissionManager.getUserManager();
    }


    public synchronized void removeSheetInfo(String sheetName, DtoSheetInfoLine sheetInfo) {

    }

    public synchronized Set<DtoSheetInfoLine> getSheetInfo(String sheetName) {
        return usernameToSheetInfos.get(sheetName);
    }

    public synchronized void newSheetWasCreated(String sheetName, String userName, String sheetSize,
                                                String myPermissionStatus) {

        DtoSheetInfoLine sheetInfo = new DtoSheetInfoLine(userName, sheetName, sheetSize, myPermissionStatus);
        usernameToSheetInfos.computeIfAbsent(userName, k -> new HashSet<>());

        usernameToSheetInfos.get(userName).add(sheetInfo);

        userManager.getUsers().forEach(user -> {
            if (!user.equals(userName)) {
                usernameToSheetInfos.computeIfAbsent(user, k -> new HashSet<>());
                usernameToSheetInfos.get(user).add(new DtoSheetInfoLine(userName, sheetName, sheetSize, "NONE"));
            }
        });
    }


//    public synchronized void newUserLoggedIn(String userName) {
//        // Initialize or retrieve the existing sheet info set for the new user
//        Set<DtoSheetInfoLine> sheetInfos = usernameToSheetInfos.computeIfAbsent(userName, k -> new HashSet<>());
//
//        // Iterate over all existing users and their sheet info
//        usernameToSheetInfos.forEach((existingUser, sheetInfoSet) -> {
//            // For each sheet owned by other users, add a new entry for the logged-in user
//            sheetInfoSet.forEach(sheetInfo -> {
//                String sheetName = sheetInfo.getSheetName();
//                String sheetSize = sheetNamesToSheetSize.get(sheetName);
//
//                sheetNamesToSheetOwner.put(sheetName, sheetInfo.getOwnerName());
//
//                // Add the sheet to the new user's sheet info with "NONE" as the permission status
//                sheetInfos.add(new DtoSheetInfoLine(sheetInfo.getOwnerName(), sheetName, sheetSize, "NONE"));
//            });
//        });
//    }

    public synchronized void newUserLoggedIn(String userName) {
        // Check if the user is new by using computeIfAbsent and storing the result
        Set<DtoSheetInfoLine> sheetInfos = usernameToSheetInfos.computeIfAbsent(userName, k -> new HashSet<>());

        // If the user is new (i.e., the sheetInfos set was just created), proceed with adding sheet info
        if (usernameToSheetInfos.get(userName).isEmpty()) {
            // Iterate over all existing users and their sheet info
            usernameToSheetInfos.forEach((existingUser, sheetInfoSet) -> {
                // For each sheet owned by other users, add a new entry for the logged-in user
                sheetInfoSet.forEach(sheetInfo -> {
                    String sheetName = sheetInfo.getSheetName();
                    String sheetSize = sheetNamesToSheetSize.get(sheetName);

                    sheetNamesToSheetOwner.put(sheetName, sheetInfo.getOwnerName());

                    // Add the sheet to the new user's sheet info with "NONE" as the permission status
                    sheetInfos.add(new DtoSheetInfoLine(sheetInfo.getOwnerName(), sheetName, sheetSize, "NONE"));
                });
            });
        }
    }


    public synchronized void removeSheet(String sheetName) {
        sheetNamesToSheetSize.remove(sheetName);
    }

    public synchronized void AddSheet(String sheetName, String sheetSize) {
        sheetNamesToSheetSize.put(sheetName, sheetSize);
    }


    public Set<DtoSheetInfoLine> getSheetInfos(String userName) {

        Set<DtoSheetInfoLine> sheetInfos = usernameToSheetInfos.get(userName);
        if (sheetInfos == null) {
            return null;
        }
        else{
            return sheetInfos;
        }
    }

    public void updateSheetPermissions(String ownerName, String sheetName, String userName) {
        usernameToSheetInfos.get(userName).forEach(sheetInfo -> {
            if (sheetInfo.getSheetName().equals(sheetName)) {
                sheetInfo.setMyPermission(permissionManager.getPermission(sheetName, userName).toString());
            }
        });
    }
}
