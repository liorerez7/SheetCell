package utilities;

import dto.components.DtoContainerData;
import dto.components.DtoSheetCell;
import utilities.http.deserialize.CellLocationMapDeserializer;
import utilities.http.deserialize.CellLocationToStringMapDeserializer;
import utilities.http.deserialize.DtoContainerDataDeserializer;
import utilities.http.deserialize.DtoSheetCellDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;


import java.util.Map;

public class Constants {

    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    // fxml locations
    public final static String MAIN_APP_PAGE_FXML_RESOURCE_LOCATION = "/controller/main/SheetCell.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/controller/login/login.fxml";
    public static final String DASHBOARD_PAGE_FXML_RESOURCE_LOCATION = "/controller/dashboard/dashboard.fxml";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/webApp_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/usersList";
    public final static String LOGOUT_PAGE = FULL_SERVER_PATH + "/logoutShortResponse";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";
    public final static String SEND_MESSAGE_URL = CHAT_LINES_LIST + "/sendMessage";
    public final static String SEND_CHAT_LINE = CHAT_LINES_LIST + "/sendChat";



    public final static String INIT_SHEET_CELL_ENDPOINT = FULL_SERVER_PATH + "/xmlAddress";
    public static final String GET_SHEET_CELL_ENDPOINT = FULL_SERVER_PATH + "/sheetCell";
    public static final String UPDATE_CELL_ENDPOINT = FULL_SERVER_PATH + "/updateCell";
    public static final String ADD_RANGE_ENDPOINT = FULL_SERVER_PATH + "/addRange";
    public static final String GET_REQUESTED_RANGE_ENDPOINT = FULL_SERVER_PATH + "/requestedRange";
    public static final String GET_ALL_RANGES_ENDPOINT = FULL_SERVER_PATH + "/allRanges";
    public static final String DELETE_RANGE_ENDPOINT = FULL_SERVER_PATH + "/deleteRange";
    public static final String SAVE_CURRENT_SHEET_CELL_STATE_ENDPOINT = FULL_SERVER_PATH + "/saveCurrentSheetCellState";
    public static final String RESTORE_CURRENT_SHEET_CELL_STATE_ENDPOINT = FULL_SERVER_PATH + "/restoreCurrentSheetCellState";
    public static final String UPDATE_SHEET_NAME_IN_SEASSION_ENDPOINT = FULL_SERVER_PATH + "/updateSheetNameInSession";
    public static final String GET_ALL_SHEET_NAMES = FULL_SERVER_PATH + "/allSheetNames";
    public static final String GET_IS_SHEET_UPDATED = FULL_SERVER_PATH + "/isSheetUpdated";
    public static final String GET_MY_SHEET_NAME = FULL_SERVER_PATH + "/mySheetName";
    public static final String GET_PERMISSIONS_LINES_FOR_SHEET = FULL_SERVER_PATH + "/permissionLinesForSheet";
    public static final String REQUEST_PERMISSION = FULL_SERVER_PATH + "/requestPermission";
    public static final String GET_MY_REQUESTS = FULL_SERVER_PATH + "/myRequests";
    public static final String MY_RESPONSE_PERMISSION = FULL_SERVER_PATH + "/myResponsePermission";
    public static final String UPDATE_RESPONSE_PERMISSION = FULL_SERVER_PATH + "/updateResponsePermission";
    public static final String GET_MY_PERMISSION_FOR_SHEET = FULL_SERVER_PATH + "/myPermissionForSheet";
    public static final String GET_USER_NAME_THAT_LAST_UPDATED_CELL_ENDPOINT = FULL_SERVER_PATH + "/userNameThatLastUpdatedCell";









    public static final String CANT_UPDATE_CELL_NO_CLICK_ON_LABEL = "first click on the cell \nyou wish to modify its value";

//    public static final String CANT_UPDATE_CELL_NEWER_VERSION_MESSAGE =  NEWER_VERSION__ERROR_MESSAGE + "update cells";
//
//    public static final String CANT_ADD_RANGE_NEWER_VERSION_MESSAGE = NEWER_VERSION__ERROR_MESSAGE + "add ranges";
//    public static final String CANT_DELETE_RANGE_NEWER_VERSION_MESSAGE = NEWER_VERSION__ERROR_MESSAGE + "delete ranges";

    // GSON instance
    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType(), new CellLocationMapDeserializer())
            .registerTypeAdapter(new TypeToken<Map<CellLocation, String>>() {}.getType(), new CellLocationToStringMapDeserializer())
            .registerTypeAdapter(DtoSheetCell.class, new DtoSheetCellDeserializer())
            .registerTypeAdapter(DtoContainerData.class, new DtoContainerDataDeserializer())
            .serializeSpecialFloatingPointValues()  // This allows serialization of NaN and Infinity
            .setPrettyPrinting()
            .create();

    //public final static Gson GSON_INSTANCE = new Gson();
}
