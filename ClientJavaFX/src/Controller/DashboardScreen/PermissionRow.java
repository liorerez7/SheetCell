package Controller.DashboardScreen;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PermissionRow {
    private final StringProperty username;
    private final StringProperty permissionStatus;
    private final StringProperty approvedByOwner;

    public PermissionRow(String username, String permissionStatus, String approvedByOwner) {
        this.username = new SimpleStringProperty(username);
        this.permissionStatus = new SimpleStringProperty(permissionStatus);
        this.approvedByOwner = new SimpleStringProperty(approvedByOwner);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty permissionStatusProperty() {
        return permissionStatus;
    }

    public StringProperty approvedByOwnerProperty() {
        return approvedByOwner;
    }
}

