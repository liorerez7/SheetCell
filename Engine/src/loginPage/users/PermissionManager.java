package loginPage.users;

import DtoComponents.PermissionLine;
import DtoComponents.PermissionStatus;
import DtoComponents.RequestPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionManager {

    private Map<String, List<PermissionLine>> currentSheetNameToPermissionLines = new HashMap<>();
    private Map<String, List<PermissionLine>> allHistorySheetNameToPermissionLines = new HashMap<>();
    private UserManager userManager;
    private Map<String, List<RequestPermission>> userNameToHisRequestList = new HashMap<>();
    private Map<String, List<ResponsePermission>> ownerNameToHisResponseList = new HashMap<>();


    public PermissionManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public synchronized void addPermission(String sheetName, String userName,
                                           PermissionStatus status, boolean approvedByOwner) {

        PermissionLine permissionLine = new PermissionLine(userName, status, approvedByOwner);
        PermissionLine permissionLineForAllHistory = new PermissionLine(userName, status, approvedByOwner);

        // Initialize lists if they don't exist
        allHistorySheetNameToPermissionLines.computeIfAbsent(sheetName, k -> new ArrayList<>());
        currentSheetNameToPermissionLines.computeIfAbsent(sheetName, k -> new ArrayList<>());

        // Add to the history, always appending
        allHistorySheetNameToPermissionLines.get(sheetName).add(permissionLineForAllHistory);

        // Update the current permission, removing any previous permission for the same user
        List<PermissionLine> currentPermissions = currentSheetNameToPermissionLines.get(sheetName);
        currentPermissions.removeIf(perm -> perm.getUserName().equals(userName)); // Remove old permission
        currentPermissions.add(permissionLine); // Add the new permission
    }

    public synchronized void removePermission(String sheetName, String userName) {
        List<PermissionLine> permissionLines = currentSheetNameToPermissionLines.get(sheetName);
        List<PermissionLine> allHistoryPermissionLines = allHistorySheetNameToPermissionLines.get(sheetName);
        if (permissionLines != null) {
            permissionLines.removeIf(permissionLine -> permissionLine.getUserName().equals(userName));
            allHistoryPermissionLines.removeIf(permissionLine -> permissionLine.getUserName().equals(userName));
        }
    }


    public synchronized void addPermission(String sheetName, PermissionLine permissionLine) {
        currentSheetNameToPermissionLines.get(sheetName).add(permissionLine);
    }

    public List<PermissionLine> getPermissionStatusOfSheet(String sheetName) {
        //return currentSheetNameToPermissionLines.get(sheetName);
        return allHistorySheetNameToPermissionLines.get(sheetName);
    }

    public synchronized void newUserNameLoggedIn(String userName) {
        for (String sheetName : currentSheetNameToPermissionLines.keySet()) {
            addPermission(sheetName, userName, PermissionStatus.NONE, false);
        }
    }

    public Map<String, List<PermissionLine>> getCurrentSheetNameToPermissionLines() {
        return currentSheetNameToPermissionLines;
    }

    public synchronized void newSheetCreated(String sheetName) {

        if (!currentSheetNameToPermissionLines.containsKey(sheetName)) {
            currentSheetNameToPermissionLines.put(sheetName, new ArrayList<>());
        }
        List<PermissionLine> permissionLines = currentSheetNameToPermissionLines.get(sheetName);

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
        for (String sheetName : currentSheetNameToPermissionLines.keySet()) {
            removePermission(sheetName, userName);
        }
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public synchronized void addRequestPermission(String sheetName, String userName, PermissionStatus permissionStatus) {
        // Find the owner of the sheet
        String ownerName = findOwner(sheetName);

        if (ownerName == null) {
            throw new IllegalStateException("Owner not found for sheet: " + sheetName);
        }

        // Create the request permission and response permission objects
        RequestPermission requestPermission = new RequestPermission(sheetName, ownerName, permissionStatus);
        ResponsePermission responsePermission = new ResponsePermission(sheetName, userName, permissionStatus);

        // Ensure that userName has a list in userNameToRequestPermission
        userNameToHisRequestList.computeIfAbsent(userName, k -> new ArrayList<>()).add(requestPermission);

        // Ensure that ownerName has a list in userNameToResponsePermission
        ownerNameToHisResponseList.computeIfAbsent(ownerName, k -> new ArrayList<>()).add(responsePermission);
    }

    private String findOwner(String sheetName) {
        for (PermissionLine permissionLine : currentSheetNameToPermissionLines.get(sheetName)) {
            if (permissionLine.getPermissionStatus() == PermissionStatus.OWNER) {
                return permissionLine.getUserName();
            }
        }
        return null;
    }

    public List<RequestPermission> getUserRequestPermission(String userName) {
        return userNameToHisRequestList.get(userName);
    }

    public List<ResponsePermission> getUserResponsePermission(String userName) {
        return ownerNameToHisResponseList.get(userName);
    }

    public void updateOwnerResponseForRequest(String ownerName, String sheetName, String userName,
                                              PermissionStatus permissionStatus, boolean isApproved) {
        //first we need to delete from both lists the request and response

        List<ResponsePermission> myResponses = ownerNameToHisResponseList.get(ownerName);
        myResponses.forEach(responsePermission -> {
            if (responsePermission.getSheetNameForRequest().equals(sheetName) &&
                    responsePermission.getUserNameForRequest().equals(userName)) {

                responsePermission.setWasAnswered(true);
            }
        });

        List<RequestPermission> myRequests = userNameToHisRequestList.get(userName);
        myRequests.forEach(requestPermission -> {
            if (requestPermission.getSheetNameForRequest().equals(sheetName) &&
                    requestPermission.getUserNameForRequest().equals(ownerName)) {

                requestPermission.setWasAnswered(true);
            }
        });

        //second we need to change the user permission in the sheet in first table

        List<PermissionLine> permissionLines = currentSheetNameToPermissionLines.get(sheetName);
        if (permissionLines != null) {
            boolean found = false;

            // Use a traditional for loop to check and update the permission
            for (PermissionLine permissionLine : permissionLines) {
                if (permissionLine.getUserName().equals(userName)) {
                    permissionLine.setPermissionStatus(permissionStatus);
                    permissionLine.setApprovedByOwner(isApproved);
                    found = true;
                    break; // No need to keep looping once we found the user
                }
            }

            PermissionLine newPermissionLine = new PermissionLine(userName, permissionStatus, isApproved);
            PermissionLine newPermissionLineForAllHistory = new PermissionLine(userName, permissionStatus, isApproved);


            if (!found) {
                permissionLines.add(newPermissionLine);
            }

            allHistorySheetNameToPermissionLines.get(sheetName).add(newPermissionLineForAllHistory);
        }
    }

    public PermissionStatus getPermission(String sheetName, String userName) {

        List<PermissionLine> permissionLines = currentSheetNameToPermissionLines.get(sheetName);
        if (permissionLines != null) {
            for (PermissionLine permissionLine : permissionLines) {
                if (permissionLine.getUserName().equals(userName)) {
                    return permissionLine.getPermissionStatus();
                }
            }
        }
        return PermissionStatus.NONE;
    }
}
