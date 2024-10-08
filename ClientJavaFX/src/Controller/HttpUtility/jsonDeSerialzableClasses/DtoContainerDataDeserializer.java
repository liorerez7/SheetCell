package Controller.HttpUtility.jsonDeSerialzableClasses;

import DtoComponents.DtoContainerData;
import DtoComponents.DtoSheetCell;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;



import com.google.gson.*;
import smallParts.CellLocation;

import java.lang.reflect.Type;
import java.util.Map;

public class DtoContainerDataDeserializer implements JsonDeserializer<DtoContainerData> {
    @Override
    public DtoContainerData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize DtoSheetCell using the context
        DtoSheetCell dtoSheetCell = context.deserialize(jsonObject.get("dtoSheetCell"), DtoSheetCell.class);

        // Deserialize the map (afterSortCellLocationToOldCellLocation) using a helper method
        Map<CellLocation, CellLocation> afterSortCellLocationToOldCellLocation = deserializeCellLocationMap(jsonObject.get("afterSortCellLocationToOldCellLocation"), context);

        // Create a new instance of DtoContainerData
        return new DtoContainerData(dtoSheetCell, afterSortCellLocationToOldCellLocation);
    }

    private Map<CellLocation, CellLocation> deserializeCellLocationMap(JsonElement json, JsonDeserializationContext context) {
        Map<CellLocation, CellLocation> map = new java.util.HashMap<>();

        // Ensure the json element is a JsonObject
        if (!json.isJsonObject()) {
            throw new JsonParseException("Expected JsonObject for Map<CellLocation, CellLocation>");
        }

        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();

            // Convert the string key (e.g., "E2") into a CellLocation object
            CellLocation newCellLocation = deserializeCellLocation(key);

            // Deserialize the value as CellLocation using context
            JsonElement valueElement = entry.getValue();
            CellLocation oldCellLocation;
            try {
                oldCellLocation = context.deserialize(valueElement, CellLocation.class);
            } catch (JsonParseException e) {
                throw new JsonParseException("Error deserializing value for key: " + key, e);
            }

            // Put the deserialized key-value pair in the map
            map.put(newCellLocation, oldCellLocation);
        }

        return map;
    }

    private CellLocation deserializeCellLocation(String key) {
        // Assume the string format is like "A1"
        char visualColumn = key.charAt(0);
        String visualRow = key.substring(1);
        return new CellLocation(visualColumn, visualRow);
    }
}