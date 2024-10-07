package Controller.HttpUtility.jsonDeSerialzableClasses;

import CoreParts.smallParts.CellLocation;
import com.google.gson.*;
import expression.impl.variantImpl.EffectiveValue;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

//public class CellLocationMapDeserializer implements JsonDeserializer<Map<CellLocation, EffectiveValue>> {
//    @Override
//    public Map<CellLocation, EffectiveValue> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
//        Map<CellLocation, EffectiveValue> map = new HashMap<>();
//        JsonObject jsonObject = json.getAsJsonObject();
//
//        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
//            // Convert the key back to a CellLocation (assumes keys are in "B5" format)
//            String key = entry.getKey();
//            char visualColumn = key.charAt(0);
//            String visualRow = key.substring(1);
//            CellLocation cellLocation = new CellLocation(visualColumn, visualRow);
//
//            // Deserialize the value as EffectiveValue
//            EffectiveValue value = context.deserialize(entry.getValue(), EffectiveValue.class);
//            map.put(cellLocation, value);
//        }
//
//        return map;
//    }
//}

public class CellLocationMapDeserializer implements JsonDeserializer<Map<CellLocation, EffectiveValue>> {
    @Override
    public Map<CellLocation, EffectiveValue> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<CellLocation, EffectiveValue> map = new HashMap<>();

        // Ensure the json element is a JsonObject
        if (!json.isJsonObject()) {
            throw new JsonParseException("Expected JsonObject for Map<CellLocation, EffectiveValue>");
        }

        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();

            // Convert the string key (e.g., "E2") into a CellLocation object
            char visualColumn = key.charAt(0);  // Get the column (e.g., 'E')
            String visualRow = key.substring(1); // Get the row (e.g., "2")
            CellLocation cellLocation = new CellLocation(visualColumn, visualRow);

            // Deserialize the value as EffectiveValue using the context
            JsonElement valueElement = entry.getValue();
            EffectiveValue value;
            try {
                // The value should be an object; this checks and parses it correctly
                value = context.deserialize(valueElement, EffectiveValue.class);
            } catch (JsonParseException e) {
                throw new JsonParseException("Error deserializing value for key: " + key, e);
            }

            // Put the deserialized key-value pair in the map
            map.put(cellLocation, value);
        }

        return map;
    }
}
