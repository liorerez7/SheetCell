package utilities.http.deserialize;

import com.google.gson.*;
import dto.small_parts.CellLocation;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CellLocationToStringMapDeserializer implements JsonDeserializer<Map<CellLocation, String>> {
    @Override
    public Map<CellLocation, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<CellLocation, String> map = new HashMap<>();

        // Ensure the json element is a JsonObject
        if (!json.isJsonObject()) {
            throw new JsonParseException("Expected JsonObject for Map<CellLocation, String>");
        }

        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();

            // Convert the string key (e.g., "A1", "B2") into a CellLocation object
            char visualColumn = key.charAt(0);  // Get the column (e.g., 'A')
            String visualRow = key.substring(1); // Get the row (e.g., "1")
            CellLocation cellLocation = new CellLocation(visualColumn, visualRow);

            // Deserialize the value as a String
            JsonElement valueElement = entry.getValue();
            String value;
            try {
                value = context.deserialize(valueElement, String.class);
            } catch (JsonParseException e) {
                throw new JsonParseException("Error deserializing value for key: " + key, e);
            }

            // Put the deserialized key-value pair in the map
            map.put(cellLocation, value);
        }

        return map;
    }
}
