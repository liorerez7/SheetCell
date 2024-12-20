package utilities.http.deserialize;

import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;


import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

//public class DtoSheetCellDeserializer implements JsonDeserializer<DtoSheetCell> {
//    @Override
//    public DtoSheetCell deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        JsonObject jsonObject = json.getAsJsonObject();
//
//        // Deserialize nested maps
//        Type mapType = new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType();
//        Map<CellLocation, EffectiveValue> sheetCellMap = context.deserialize(jsonObject.get("sheetCell"), mapType);
//
//        Type rangeMapType = new TypeToken<Map<String, List<CellLocation>>>() {}.getType();
//        Map<String, List<CellLocation>> rangesMap = context.deserialize(jsonObject.get("ranges"), rangeMapType);
//
//        // Deserialize cellIdToDtoCell
//        Type cellIdToDtoCellType = new TypeToken<Map<String, DtoCell>>() {}.getType();
//        Map<String, DtoCell> cellIdToDtoCellMap = context.deserialize(jsonObject.get("cellIdToDtoCell"), cellIdToDtoCellType);
//
//        String name = jsonObject.get("name").getAsString();
//        int versionNumber = jsonObject.get("versionNumber").getAsInt();
//        int currentNumberOfRows = jsonObject.get("currentNumberOfRows").getAsInt();
//        int currentNumberOfCols = jsonObject.get("currentNumberOfCols").getAsInt();
//        int currentCellLength = jsonObject.get("currentCellLength").getAsInt();
//        int currentCellWidth = jsonObject.get("currentCellWidth").getAsInt();
//
//
//        return new DtoSheetCell(sheetCellMap, rangesMap, name, versionNumber, currentNumberOfRows,
//                currentNumberOfCols, currentCellLength, currentCellWidth, cellIdToDtoCellMap);
//    }
//}

public class DtoSheetCellDeserializer implements JsonDeserializer<DtoSheetCell> {
    @Override
    public DtoSheetCell deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize nested maps
        Type mapType = new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType();
        Map<CellLocation, EffectiveValue> sheetCellMap = context.deserialize(jsonObject.get("sheetCell"), mapType);

        Type rangeMapType = new TypeToken<Map<String, List<CellLocation>>>() {}.getType();
        Map<String, List<CellLocation>> rangesMap = context.deserialize(jsonObject.get("ranges"), rangeMapType);

        // Deserialize cellIdToDtoCell
        Type cellIdToDtoCellType = new TypeToken<Map<String, DtoCell>>() {}.getType();
        Map<String, DtoCell> cellIdToDtoCellMap = context.deserialize(jsonObject.get("cellIdToDtoCell"), cellIdToDtoCellType);

        String name = jsonObject.get("name").getAsString();
        int versionNumber = jsonObject.get("versionNumber").getAsInt();
        int currentNumberOfRows = jsonObject.get("currentNumberOfRows").getAsInt();
        int currentNumberOfCols = jsonObject.get("currentNumberOfCols").getAsInt();
        int currentCellLength = jsonObject.get("currentCellLength").getAsInt();
        int currentCellWidth = jsonObject.get("currentCellWidth").getAsInt();

        // Deserialize predictedValues and isPredictedValuesWorked
        Type predictedValuesType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> predictedValues = context.deserialize(jsonObject.get("predictedValues"), predictedValuesType);
        boolean isPredictedValuesWorked = jsonObject.get("isPredictedValuesWorked").getAsBoolean();

        DtoSheetCell dtoSheetCell = new DtoSheetCell(sheetCellMap, rangesMap, name, versionNumber, currentNumberOfRows,
                currentNumberOfCols, currentCellLength, currentCellWidth, cellIdToDtoCellMap);

        // Set predictedValues and isPredictedValuesWorked in the DtoSheetCell object
        dtoSheetCell.setPredictedValues(predictedValues);
        dtoSheetCell.setPredictedValuesWorked(isPredictedValuesWorked);

        return dtoSheetCell;
    }
}