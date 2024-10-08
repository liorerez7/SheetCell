//package Controller.HttpUtility.jsonDeSerialzableClasses;
//
//
//import CoreParts.smallParts.CellLocation;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSerializationContext;
//import com.google.gson.JsonSerializer;
//
//import java.lang.reflect.Type;
//
//public class CellLocationSerializer implements JsonSerializer<CellLocation> {
//    @Override
//    public JsonElement serialize(CellLocation src, Type typeOfSrc, JsonSerializationContext context) {
//        JsonObject jsonObject = new JsonObject();
////        jsonObject.addProperty("realColumn", src.getRealColumn());
////        jsonObject.addProperty("realRow", src.getRealRow());
//        jsonObject.addProperty("visualColumn", String.valueOf(src.getVisualColumn()));
//        jsonObject.addProperty("visualRow", src.getVisualRow());
//        return jsonObject;
//    }
//}