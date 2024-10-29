package chat.utilities.jsonSerializableClasses;

import com.google.gson.*;
import dto.small_parts.UpdateCellInfo;

import java.lang.reflect.Type;
import java.util.Map;

public class VersionToCellInfoMapSerializer implements JsonSerializer<Map<Integer, UpdateCellInfo>> {

    @Override
    public JsonElement serialize(Map<Integer, UpdateCellInfo> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<Integer, UpdateCellInfo> entry : src.entrySet()) {
            Integer versionNumber = entry.getKey();
            UpdateCellInfo updateCellInfo = entry.getValue();
            jsonObject.add(versionNumber.toString(), context.serialize(updateCellInfo, UpdateCellInfo.class));
        }

        return jsonObject;
    }
}