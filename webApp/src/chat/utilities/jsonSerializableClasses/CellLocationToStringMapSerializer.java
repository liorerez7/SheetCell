package chat.utilities.jsonSerializableClasses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dto.small_parts.CellLocation;

import java.lang.reflect.Type;
import java.util.Map;

public class CellLocationToStringMapSerializer implements JsonSerializer<Map<CellLocation, String>> {
    @Override
    public JsonElement serialize(Map<CellLocation, String> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<CellLocation, String> entry : src.entrySet()) {
            // Convert CellLocation to a string (e.g., "A1", "B2")
            String key = entry.getKey().toString();
            JsonElement value = context.serialize(entry.getValue());
            jsonObject.add(key, value);
        }

        return jsonObject;
    }
}
