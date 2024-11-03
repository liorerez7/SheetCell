package dto.permissions;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PermissionLine {

    private String userName;
    private PermissionStatus permissionStatus;
    private RequestStatus requestStatus;
    private String timestamp;

    public PermissionLine(String userName, PermissionStatus permissionStatus, RequestStatus requestStatus) {
        this.userName = userName;
        this.permissionStatus = permissionStatus;
        this.requestStatus = requestStatus;
        this.timestamp = getCurrentDateTime();
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

    public void getTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    private String getCurrentDateTime() {
        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Create a formatter for the date and time in the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Format the current date and time
        return currentDateTime.format(formatter);
    }
}
