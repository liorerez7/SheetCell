package dto.permissions;

import java.util.Objects;

public class PermissionLine {

    private String userName;
    private PermissionStatus permissionStatus;
    private boolean approvedByOwner;

    public PermissionLine(String userName, PermissionStatus permissionStatus, boolean approvedByOwner) {
        this.userName = userName;
        this.approvedByOwner = approvedByOwner;
        this.permissionStatus = permissionStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isApprovedByOwner() {
        return approvedByOwner;
    }

    public void setApprovedByOwner(boolean approvedByOwner) {
        this.approvedByOwner = approvedByOwner;
    }

    public PermissionStatus getPermissionStatus() {
        return permissionStatus;
    }

    public void setPermissionStatus(PermissionStatus permissionStatus) {
        this.permissionStatus = permissionStatus;
    }

    //implement equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionLine that = (PermissionLine) o;
        return approvedByOwner == that.approvedByOwner && Objects.equals(userName, that.userName) && permissionStatus == that.permissionStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, permissionStatus, approvedByOwner);
    }
}
