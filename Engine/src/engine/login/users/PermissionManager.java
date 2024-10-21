package engine.login.users;

import dto.permissions.*;
import dto.permissions.RequestStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionManager {

    private final Map<String, List<PermissionLine>> currentSheetNameToPermissionLines = new HashMap<>();
    private final Map<String, List<PermissionLine>> allHistorySheetNameToPermissionLines = new HashMap<>();
    private final UserManager userManager;
    private final Map<String, List<RequestPermission>> userNameToHisRequestList = new HashMap<>();
    private final Map<String, List<ResponsePermission>> ownerNameToHisResponseList = new HashMap<>();


    public PermissionManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public synchronized void addPermission(String sheetName, String userName,
                                           PermissionStatus status, RequestStatus requestStatus) {

        PermissionLine permissionLine = new PermissionLine(userName, status, requestStatus);
        PermissionLine permissionLineForAllHistory = new PermissionLine(userName, status, requestStatus);

        // Initialize lists if they don't exist
        allHistorySheetNameToPermissionLines.computeIfAbsent(sheetName, k -> new ArrayList<>());
        currentSheetNameToPermissionLines.computeIfAbsent(sheetName, k -> new ArrayList<>());

        // Add to the history, always appending
        allHistorySheetNameToPermissionLines.get(sheetName).add(permissionLineForAllHistory);

        // Update the current permission, removing any previous permission for the same user
        List<PermissionLine> currentPermissions = currentSheetNameToPermissionLines.get(sheetName);

        if(permissionLine.isApprovedByOwner()){
            currentPermissions.removeIf(perm -> perm.getUserName().equals(userName)); // Remove old permission
            currentPermissions.add(permissionLine); // Add the new permission
        }
    }

    public synchronized void removePermission(String sheetName, String userName) {
        List<PermissionLine> permissionLines = currentSheetNameToPermissionLines.get(sheetName);
        List<PermissionLine> allHistoryPermissionLines = allHistorySheetNameToPermissionLines.get(sheetName);
        if (permissionLines != null) {
            permissionLines.removeIf(permissionLine -> permissionLine.getUserName().equals(userName));
            allHistoryPermissionLines.removeIf(permissionLine -> permissionLine.getUserName().equals(userName));
        }
    }

    public List<PermissionLine> getPermissionStatusOfSheet(String sheetName) {
        return allHistorySheetNameToPermissionLines.get(sheetName);
    }

    public Map<String, List<PermissionLine>> getCurrentSheetNameToPermissionLines() {
        return currentSheetNameToPermissionLines;
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

        PermissionLine newPermissionLine = new PermissionLine(userName, permissionStatus, RequestStatus.PENDING);
        allHistorySheetNameToPermissionLines.get(sheetName).add(newPermissionLine);
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
                                              PermissionStatus permissionStatus, RequestStatus requestStatus) {

        // Mark the response as answered
        List<ResponsePermission> myResponses = ownerNameToHisResponseList.get(ownerName);
        myResponses.forEach(responsePermission -> {
            if (responsePermission.getSheetNameForRequest().equals(sheetName) &&
                    responsePermission.getUserNameForRequest().equals(userName) && responsePermission.getPermissionStatusForRequest().equals(permissionStatus)) {

                responsePermission.setWasAnswered(true);
            }
        });

        // Mark the request as answered
        List<RequestPermission> myRequests = userNameToHisRequestList.get(userName);
        myRequests.forEach(requestPermission -> {
            if (requestPermission.getSheetNameForRequest().equals(sheetName) &&
                    requestPermission.getUserNameForRequest().equals(ownerName) && requestPermission.getPermissionStatusForRequest().equals(permissionStatus)) {

                requestPermission.setWasAnswered(true);
            }
        });


        // Update the current permission status, this holds the current and relevant permissions.
        // here there are no pending requests, only current permission status (READER, WRITER, OWNER, NONE)
        List<PermissionLine> permissionLines = currentSheetNameToPermissionLines.get(sheetName);
        if (permissionLines != null) {
            boolean found = false;

            for (PermissionLine permissionLine : permissionLines) {
                if (permissionLine.getUserName().equals(userName)) {
                    if(requestStatus == RequestStatus.APPROVED){
                        permissionLine.setPermissionStatus(permissionStatus);
                        permissionLine.setRequestStatus(requestStatus);
                        found = true;
                        break;
                    }
                }
            }

            PermissionLine newPermissionLine = new PermissionLine(userName, permissionStatus, requestStatus);
            PermissionLine newPermissionLineForAllHistory = new PermissionLine(userName, permissionStatus, requestStatus);

            if (!found && (requestStatus == RequestStatus.APPROVED)) {
                permissionLines.add(newPermissionLine);
            }



            // Update the all history permission status, this holds all the permissions that were ever given to a user.
            // swap the pending request status to the new status
            boolean foundInAllHistory = false;

            List<PermissionLine> allHistoryPermissionLines = allHistorySheetNameToPermissionLines.get(sheetName);
            for(PermissionLine permissionLine : allHistoryPermissionLines){
                if(permissionLine.getUserName().equals(userName) && permissionLine.getRequestStatus().equals(RequestStatus.PENDING)
                && permissionLine.getPermissionStatus().equals(permissionStatus)) {

                    permissionLine.setRequestStatus(requestStatus);
                    foundInAllHistory = true;
                }
            }

            if(!foundInAllHistory){
                allHistorySheetNameToPermissionLines.get(sheetName).add(newPermissionLineForAllHistory);
            }
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