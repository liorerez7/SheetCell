package chat.utilities;


import chat.utilities.jsonSerializableClasses.*;
import dto.components.DtoContainerData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;
import dto.components.DtoSheetCell;
import dto.small_parts.UpdateCellInfo;


import java.util.Map;

public class Constants {

    public static final String USERNAME = "username";
    public static final String USER_NAME_ERROR = "username_error";
    public static final String SHEET_NAME = "sheetName";
    public static final String CHAT_PARAMETER = "userstring";
    public static final String CHAT_VERSION_PARAMETER = "chatversion";
    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;

    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType(), new CellLocationMapSerializer())
            .registerTypeAdapter(new TypeToken<Map<CellLocation, String>>() {}.getType(), new CellLocationToStringMapSerializer())
            .registerTypeAdapter(DtoSheetCell.class, new DtoSheetCellSerializer())
            .registerTypeAdapter(DtoContainerData.class, new DtoContainerDataSerializer())
//            .registerTypeAdapter(UpdateCellInfo.class, new UpdateCellInfoSerializer())
//            .registerTypeAdapter(new TypeToken<Map<Integer, UpdateCellInfo>>() {}.getType(), new VersionToCellInfoMapSerializer()) // Serializer for Map<Integer, UpdateCellInfo>
            .serializeSpecialFloatingPointValues()
            .setPrettyPrinting()
            .create();
}