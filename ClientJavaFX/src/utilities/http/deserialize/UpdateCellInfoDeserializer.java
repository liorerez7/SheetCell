//package utilities.http.deserialize;
//
//import com.google.gson.*;
//import dto.small_parts.CellLocation;
//import dto.small_parts.UpdateCellInfo;
//
//import java.lang.reflect.Type;
//
//public class UpdateCellInfoDeserializer implements JsonDeserializer<UpdateCellInfo> {
//
//    @Override
//    public UpdateCellInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        JsonObject jsonObject = json.getAsJsonObject();
//
//        Object previousValue = context.deserialize(jsonObject.get("previousValue"), Object.class);
//        Object newValue = context.deserialize(jsonObject.get("newValue"), Object.class);
//        String previousOriginalValue = jsonObject.get("previousOriginalValue").getAsString();
//        String newOriginalValue = jsonObject.get("newOriginalValue").getAsString();
//        int versionNumber = jsonObject.get("versionNumberThatItWasChanged").getAsInt();
//        String newUserName = jsonObject.get("newUserName").getAsString();
//        CellLocation location = context.deserialize(jsonObject.get("location"), CellLocation.class);
//
//        return new UpdateCellInfo(previousValue, newValue, previousOriginalValue, newOriginalValue, versionNumber, newUserName, location);
//    }
//}