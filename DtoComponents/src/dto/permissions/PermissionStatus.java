package dto.permissions;

public enum PermissionStatus {
    OWNER,
    WRITER,
    READER,
    NONE;

    public static PermissionStatus fromString(String status) {
        return switch (status) {
            case "OWNER" -> OWNER;
            case "WRITER" -> WRITER;
            case "READER" -> READER;
            default -> NONE;
        };
    }
}
