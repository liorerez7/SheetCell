package utilities.http.deserialize;

import com.google.gson.*;
import dto.small_parts.UpdateCellInfo;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class VersionToCellInfoMapDeserializer implements JsonDeserializer<Map<Integer, UpdateCellInfo>> {

    @Override
    public Map<Integer, UpdateCellInfo> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<Integer, UpdateCellInfo> versionToCellInfoMap = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Integer versionNumber = Integer.valueOf(entry.getKey());
            UpdateCellInfo updateCellInfo = context.deserialize(entry.getValue(), UpdateCellInfo.class);
            versionToCellInfoMap.put(versionNumber, updateCellInfo);
        }
        return versionToCellInfoMap;
    }
}