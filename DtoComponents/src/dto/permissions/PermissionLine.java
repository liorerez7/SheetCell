package dto.permissions;

import java.util.Objects;

public class PermissionLine {

    private String userName;
    private PermissionStatus permissionStatus;
    private RequestStatus requestStatus;

    public PermissionLine(String userName, PermissionStatus permissionStatus, RequestStatus requestStatus) {
        this.userName = userName;
        this.permissionStatus = permissionStatus;
        this.requestStatus = requestStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isApprovedByOwner() {
        return requestStatus.equals(RequestStatus.APPROVED);
    }

    public PermissionStatus getPermissionStatus() {
        return permissionStatus;
    }

    public void setPermissionStatus(PermissionStatus permissionStatus) {
        this.permissionStatus = permissionStatus;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionLine that = (PermissionLine) o;
        return Objects.equals(userName, that.userName)
                && permissionStatus == that.permissionStatus &&
                requestStatus == that.requestStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, permissionStatus, requestStatus);
    }
}
