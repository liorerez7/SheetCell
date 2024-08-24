package CoreParts.smallParts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CellLocationFactory implements Serializable {
    private static Map<String,CellLocation> cachedCoordinates = new HashMap<>();
    public static CellLocation fromCellId(String key) {
        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }
        CellLocation coordinate = new CellLocation(key.charAt(0), key.charAt(1));
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }
    public static boolean isContained(String key) {
        return cachedCoordinates.containsKey(key);
    }
    public static void removeKey(String key) {
        cachedCoordinates.remove(key);
    }

    public static CellLocation fromCellId(char col, String row) {

        String cellId =col + row;

        return CellLocationFactory.fromCellId(cellId);
    }
}
