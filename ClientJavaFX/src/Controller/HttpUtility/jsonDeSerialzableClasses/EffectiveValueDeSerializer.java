//package Controller.HttpUtility.jsonDeSerialzableClasses;
//
//import com.google.gson.*;
//import expression.ReturnedValueType;
//import expression.impl.variantImpl.EffectiveValue;
//
//import java.lang.reflect.Type;
//
//public class EffectiveValueDeSerializer implements JsonDeserializer<EffectiveValue> {
//    @Override
//    public EffectiveValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
//        JsonObject jsonObject = json.getAsJsonObject();
//
//        JsonElement cellTypeElement = jsonObject.get("cellType");
//        if (cellTypeElement == null || cellTypeElement.isJsonNull()) {
//            // Handle the case where "cellType" is missing or null
//            throw new JsonParseException("Missing 'cellType' in JSON");
//        }
//
//        ReturnedValueType cellType = ReturnedValueType.valueOf(cellTypeElement.getAsString());
//
//        JsonElement valueElement = jsonObject.get("value");
//        Object value = null;
//
//        if (valueElement != null && !valueElement.isJsonNull()) {
//            switch (cellType) {
//                case NUMERIC:
//                    value = valueElement.getAsNumber().doubleValue();
//                    break;
//                case BOOLEAN:
//                    value = valueElement.getAsBoolean();
//                    break;
//                case STRING:
//                case UNKNOWN:
//                case EMPTY:
//                case UNDEFINED:
//                default:
//                    value = valueElement.getAsString();
//                    break;
//            }
//        }
//
//        return new EffectiveValue(cellType, value);
//    }
//}
