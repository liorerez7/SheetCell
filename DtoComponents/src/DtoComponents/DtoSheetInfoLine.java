package DtoComponents;

public class DtoSheetInfoLine {
    private final String ownerName;
    private final String sheetName;
    private final String size;
    private final String permissionStatus;

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
}
