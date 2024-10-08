package Controller.HttpUtility.jsonDeSerialzableClasses;

import DtoComponents.DtoSheetCell;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import smallParts.CellLocation;
import smallParts.EffectiveValue;


import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DtoSheetCellDeserializer implements JsonDeserializer<DtoSheetCell> {
    @Override
    public DtoSheetCell deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize nested maps
        Type mapType = new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType();
        Map<CellLocation, EffectiveValue> sheetCellMap = context.deserialize(jsonObject.get("sheetCell"), mapType);

        Type rangeMapType = new TypeToken<Map<String, List<CellLocation>>>() {}.getType();
        Map<String, List<CellLocation>> rangesMap = context.deserialize(jsonObject.get("ranges"), rangeMapType);

        String name = jsonObject.get("name").getAsString();
        int versionNumber = jsonObject.get("versionNumber").getAsInt();
        int currentNumberOfRows = jsonObject.get("currentNumberOfRows").getAsInt();
        int currentNumberOfCols = jsonObject.get("currentNumberOfCols").getAsInt();
        int currentCellLength = jsonObject.get("currentCellLength").getAsInt();
        int currentCellWidth = jsonObject.get("currentCellWidth").getAsInt();


        return new DtoSheetCell(sheetCellMap, rangesMap, name, versionNumber, currentNumberOfRows, currentNumberOfCols, currentCellLength, currentCellWidth);
    }
}