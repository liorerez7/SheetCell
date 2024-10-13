package DtoComponents;

import java.util.Objects;

public class DtoSheetInfoLine {
    private final String ownerName;
    private final String sheetName;
    private final String size;
    private String permissionStatus;

    public DtoSheetInfoLine(String ownerName, String sheetName, String size, String permissionStatus) {

        this.ownerName = ownerName;
        this.sheetName = sheetName;
        this.size = size;
        this.permissionStatus = permissionStatus;
    }

    public String getMyPermission() {
        return permissionStatus;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getSheetSize() {
        return size;
    }

    public void setMyPermission(String permission) {
        permissionStatus = permission;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DtoSheetInfoLine that = (DtoSheetInfoLine) o;
        return Objects.equals(ownerName, that.ownerName) &&
                Objects.equals(sheetName, that.sheetName) &&
                Objects.equals(size, that.size) &&
                Objects.equals(permissionStatus, that.permissionStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerName, sheetName, size, permissionStatus);
    }
}
