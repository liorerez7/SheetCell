package chat.utilities.jsonSerializableClasses;


import dto.components.DtoSheetCell;
import dto.components.DtoCell;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class DtoSheetCellSerializer implements JsonSerializer<DtoSheetCell> {
    @Override
    public JsonElement serialize(DtoSheetCell src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("sheetCell", context.serialize(src.getViewSheetCell()));
        jsonObject.add("ranges", context.serialize(src.getRanges()));
        jsonObject.addProperty("name", src.getSheetName());
        jsonObject.addProperty("versionNumber", src.getLatestVersion());
        jsonObject.addProperty("currentNumberOfRows", src.getNumberOfRows());
        jsonObject.addProperty("currentNumberOfCols", src.getNumberOfColumns());
        jsonObject.addProperty("currentCellLength", src.getCellLength());
        jsonObject.addProperty("currentCellWidth", src.getCellWidth());

        // Serialize cellIdToDtoCell
        jsonObject.add("cellIdToDtoCell", serializeCellIdToDtoCell(src.getCellIdToDtoCell(), context));

        return jsonObject;
    }

    private JsonElement serializeCellIdToDtoCell(Map<String, DtoCell> map, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, DtoCell> entry : map.entrySet()) {
            String key = entry.getKey();
            JsonElement value = context.serialize(entry.getValue(), DtoCell.class);
            jsonObject.add(key, value);
        }
        return jsonObject;
    }
}