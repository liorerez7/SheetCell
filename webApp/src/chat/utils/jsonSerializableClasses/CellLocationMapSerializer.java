package chat.utils.jsonSerializableClasses;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import smallParts.CellLocation;
import smallParts.EffectiveValue;


import java.lang.reflect.Type;
import java.util.Map;

public class CellLocationMapSerializer implements JsonSerializer<Map<CellLocation, EffectiveValue>> {
    @Override
    public JsonElement serialize(Map<CellLocation, EffectiveValue> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<CellLocation, EffectiveValue> entry : src.entrySet()) {
            // Convert CellLocation to a simple string key (e.g., "B5")
            String key = entry.getKey().toString();
            JsonElement value = context.serialize(entry.getValue(), EffectiveValue.class);
            jsonObject.add(key, value);
        }

        return jsonObject;
    }
}
