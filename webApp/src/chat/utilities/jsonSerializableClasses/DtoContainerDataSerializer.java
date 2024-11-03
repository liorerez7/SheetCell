package chat.utilities.jsonSerializableClasses;


import dto.components.DtoContainerData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


import java.lang.reflect.Type;

import dto.small_parts.CellLocation;

import java.util.Map;

public class DtoContainerDataSerializer implements JsonSerializer<DtoContainerData> {
    @Override
    public JsonElement serialize(DtoContainerData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Serialize DtoSheetCell using the context
        jsonObject.add("dtoSheetCell", context.serialize(src.getDtoSheetCell()));

        // Serialize the map (afterSortCellLocationToOldCellLocation) using a helper method
        jsonObject.add("afterSortCellLocationToOldCellLocation", serializeCellLocationMap(src.getOldCellLocationToAfterSortCellLocation(), context));

        // Serialize the primitive fields
        jsonObject.addProperty("leftColumnRange", String.valueOf(src.getLeftColumnRange()));
        jsonObject.addProperty("rightColumnRange", String.valueOf(src.getRightColumnRange()));
        jsonObject.addProperty("upperRowRange", src.getUpperRowRange());
        jsonObject.addProperty("lowerRowRange", src.getLowerRowRange());
        jsonObject.addProperty("numberOfRows", src.getNumberOfRows());
        jsonObject.addProperty("numberOfColumns", src.getNumberOfColumns());

        return jsonObject;
    }

    private JsonElement serializeCellLocationMap(Map<CellLocation, CellLocation> map, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<CellLocation, CellLocation> entry : map.entrySet()) {
            // Convert CellLocation to a simple string key (e.g., "B5")
            String key = entry.getKey().toString();
            JsonElement value = context.serialize(entry.getValue(), CellLocation.class);
            jsonObject.add(key, value);
        }

        return jsonObject;
    }
}