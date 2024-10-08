package chat.constants;

import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import Utility.DtoContainerData;
import chat.utils.jsonSerializableClasses.CellLocationMapSerializer;
import chat.utils.jsonSerializableClasses.DtoContainerDataSerializer;
import chat.utils.jsonSerializableClasses.DtoSheetCellSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import expression.impl.variantImpl.EffectiveValue;

import java.util.Map;

public class Constants {
    public static final String USERNAME = "username";
    public static final String USER_NAME_ERROR = "username_error";

    public static final String CHAT_PARAMETER = "userstring";
    public static final String CHAT_VERSION_PARAMETER = "chatversion";
    
    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;
    // GSON instance
    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType(), new CellLocationMapSerializer())
            .registerTypeAdapter(DtoSheetCell.class, new DtoSheetCellSerializer())
            .registerTypeAdapter(DtoContainerData.class, new DtoContainerDataSerializer())
            .setPrettyPrinting()
            .create();
}