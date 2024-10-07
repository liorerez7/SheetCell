package Controller.HttpUtility.jsonDeSerialzableClasses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import expression.impl.variantImpl.EffectiveValue;

import java.lang.reflect.Type;

public class EffectiveValueSerializer implements JsonSerializer<EffectiveValue> {
    @Override
    public JsonElement serialize(EffectiveValue src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cellType", src.getCellType().name());

        Object value = src.getValue();
        if (value != null) {
            if (value instanceof Number) {
                jsonObject.addProperty("value", (Number) value);
            } else if (value instanceof Boolean) {
                jsonObject.addProperty("value", (Boolean) value);
            } else {
                jsonObject.addProperty("value", value.toString());
            }
        }

        return jsonObject;
    }
}
