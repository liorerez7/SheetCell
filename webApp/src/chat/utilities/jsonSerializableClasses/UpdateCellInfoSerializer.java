package chat.utilities.jsonSerializableClasses;

import com.google.gson.*;
import dto.small_parts.UpdateCellInfo;

import java.lang.reflect.Type;

public class UpdateCellInfoSerializer implements JsonSerializer<UpdateCellInfo> {

    @Override
    public JsonElement serialize(UpdateCellInfo src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("previousValue", context.serialize(src.getPreviousValue()));
        jsonObject.add("newValue", context.serialize(src.getNewValue()));
        jsonObject.addProperty("previousOriginalValue", src.getPreviousOriginalValue());
        jsonObject.addProperty("newOriginalValue", src.getNewOriginalValue());
        jsonObject.addProperty("versionNumberThatItWasChanged", src.getVersionNumberThatItWasChanged());
        jsonObject.addProperty("newUserName", src.getNewUserName());
        jsonObject.add("location", context.serialize(src.getLocations()));

        return jsonObject;
    }
}