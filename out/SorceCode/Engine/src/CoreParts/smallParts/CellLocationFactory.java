package CoreParts.smallParts;

import expression.impl.stringFunction.Str;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CellLocationFactory implements Serializable {
    private static Map<String,CellLocation> cachedCoordinates = new HashMap<>();

    public static CellLocation fromCellId(String key) {

        key = key.toUpperCase();

        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }

        CellLocation coordinate = new CellLocation(key.charAt(0), key.substring(1));
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

        String cellId = col + row;

        return CellLocationFactory.fromCellId(cellId);
    }

    public static void clearCache() {
        cachedCoordinates.clear();
    }

    public static Map<String, CellLocation>  getCacheCoordiante(){
        return cachedCoordinates;
    }

    public static void setCacheCoordiante(Map<String, CellLocation>  cachedCoordinates){
        CellLocationFactory.cachedCoordinates = cachedCoordinates;
    }
}
