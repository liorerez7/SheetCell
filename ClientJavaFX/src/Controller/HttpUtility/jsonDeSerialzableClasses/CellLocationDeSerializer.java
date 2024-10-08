//package Controller.HttpUtility.jsonDeSerialzableClasses;
//
//import CoreParts.smallParts.CellLocation;
//import com.google.gson.JsonDeserializationContext;
//import com.google.gson.JsonDeserializer;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//import java.lang.reflect.Type;
//
//public class CellLocationDeSerializer implements JsonDeserializer<CellLocation> {
//    @Override
//    public CellLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
//        JsonObject jsonObject = json.getAsJsonObject();
//        char visualColumn = jsonObject.get("visualColumn").getAsString().charAt(0);
//        String visualRow = jsonObject.get("visualRow").getAsString();
//        return new CellLocation(visualColumn, visualRow);
//    }
//}
