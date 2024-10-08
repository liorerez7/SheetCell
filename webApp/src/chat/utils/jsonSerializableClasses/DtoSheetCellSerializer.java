package chat.utils.jsonSerializableClasses;


import DtoComponents.DtoSheetCell;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

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
        return jsonObject;
    }
}