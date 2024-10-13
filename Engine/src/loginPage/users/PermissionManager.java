package loginPage.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionManager {

    private Map<String, List<PermissionLine>> sheetNameToPermissionLines = new HashMap<>();
    private UserManager userManager;


    public PermissionManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public synchronized void addPermission(String sheetName, String userName,
                                           PermissionStatus Status, boolean approvedByOwner) {

        PermissionLine permissionLine = new PermissionLine(userName, Status, approvedByOwner);
        if(sheetNameToPermissionLines.get(sheetName) == null) {
            //create new List:
            sheetNameToPermissionLines.put(sheetName, new ArrayList<>());
        }
        sheetNameToPermissionLines.get(sheetName).add(permissionLine);
    }

    public synchronized void removePermission(String sheetName, String userName) {
        List<PermissionLine> permissionLines = sheetNameToPermissionLines.get(sheetName);
        if (permissionLines != null) {
            permissionLines.removeIf(permissionLine -> permissionLine.getUserName().equals(userName));
        }
    }


    public synchronized void addPermission(String sheetName, PermissionLine permissionLine) {
        sheetNameToPermissionLines.get(sheetName).add(permissionLine);
    }

    public List<PermissionLine> getPermissionStatusOfSheet(String sheetName) {
        return sheetNameToPermissionLines.get(sheetName);
    }

    public synchronized void newUserNameLoggedIn(String userName) {
        for (String sheetName : sheetNameToPermissionLines.keySet()) {
            addPermission(sheetName, userName, PermissionStatus.NONE, false);
        }
    }

    public synchronized void newSheetCreated(String sheetName) {
        if (!sheetNameToPermissionLines.containsKey(sheetName)) {
            sheetNameToPermissionLines.put(sheetName, new ArrayList<>());
        }
        List<PermissionLine> permissionLines = sheetNameToPermissionLines.get(sheetName);

        // Collect new permissions in a separate list
        List<PermissionLine> newPermissions = new ArrayList<>();

        synchronized (permissionLines) {
            for (String userName : userManager.getUsers()) {
                boolean userExists = permissionLines.stream().anyMatch(p -> p.getUserName().equals(userName));
                if (!userExists) {
                    newPermissions.add(new PermissionLine(userName, PermissionStatus.NONE, false));
                }
            }
            // Now add all collected new permissions
            permissionLines.addAll(newPermissions);
        }
    }

    public synchronized void removeUser(String userName) {
        for (String sheetName : sheetNameToPermissionLines.keySet()) {
            removePermission(sheetName, userName);
        }
    }
}
