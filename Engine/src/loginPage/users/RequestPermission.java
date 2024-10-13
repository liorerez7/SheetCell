package loginPage.users;

public class RequestPermission {

    private String sheetNameForRequest;
    private String userNameForRequest;
    private PermissionStatus permissionStatusForRequest;
    private boolean wasAnswered;

    public RequestPermission(String sheetNameForRequest, String userNameForRequest, PermissionStatus permissionStatusForRequest) {
        this.sheetNameForRequest = sheetNameForRequest;
        this.userNameForRequest = userNameForRequest;
        this.permissionStatusForRequest = permissionStatusForRequest;
        this.wasAnswered = false;
    }

    public boolean getWasAnswered() {
        return wasAnswered;
    }

    public void setWasAnswered(boolean wasAnswered) {
        this.wasAnswered = wasAnswered;
    }

    public String getSheetNameForRequest() {
        return sheetNameForRequest;
    }

    public String getUserNameForRequest() {
        return userNameForRequest;
    }

    public PermissionStatus getPermissionStatusForRequest() {
        return permissionStatusForRequest;
    }

    public void setSheetNameForRequest(String sheetNameForRequest) {
        this.sheetNameForRequest = sheetNameForRequest;
    }

    public void setUserNameForRequest(String userNameForRequest) {
        this.userNameForRequest = userNameForRequest;
    }

    public void setPermissionStatusForRequest(PermissionStatus permissionStatusForRequest) {
        this.permissionStatusForRequest = permissionStatusForRequest;
    }
}
