package utilities;

import dto.components.DtoContainerData;
import dto.components.DtoSheetCell;
import dto.small_parts.UpdateCellInfo;
import utilities.http.deserialize.*;
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


    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
//    private final static String CONTEXT_PATH = "/webApp_Web_exploded";
private final static String CONTEXT_PATH = "/WebAppWar";


    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    // Chat servlet constants
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOGOUT_ENDPOINT = FULL_SERVER_PATH + "/logout";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";
    public final static String POST_CHAT_MESSAGE_ENDPOINT = CHAT_LINES_LIST + "/sendMessage";
    public final static String GET_CHAT_ENDPOINT = CHAT_LINES_LIST + "/getChat";



    public final static String INIT_SHEET_CELL_ENDPOINT = FULL_SERVER_PATH + "/xmlAddress";
    public static final String GET_SHEET_CELL_ENDPOINT = FULL_SERVER_PATH + "/sheetCell";
    public static final String UPDATE_CELL_ENDPOINT = FULL_SERVER_PATH + "/updateCell";
    public static final String ADD_RANGE_ENDPOINT = FULL_SERVER_PATH + "/addRange";
    public static final String GET_REQUESTED_RANGE_ENDPOINT = FULL_SERVER_PATH + "/requestedRange";
    public static final String DELETE_RANGE_ENDPOINT = FULL_SERVER_PATH + "/deleteRange";
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
    public static final String RUNTIME_ANALYSIS = FULL_SERVER_PATH + "/runtimeAnalysis";
    public static final String GET_ALL_SHEET_CELL_VERSIONS_ENDPOINT = FULL_SERVER_PATH + "/allSheetCellVersions";
    public static final String CREATE_NEW_SHEET_ENDPOINT = FULL_SERVER_PATH + "/createNewSheet";
    public static final String POST_TEMP_SHEET_IN_SERVLET = FULL_SERVER_PATH + "/postTempSheet";
    public static final String GET_VERSION_TO_CELL_INFO_MAP = FULL_SERVER_PATH + "/versionToCellInfo";
    public static final String UPDATE_REPLACED_VALUES_URL = FULL_SERVER_PATH + "/updateReplacedValues";
    public static final String UPDATE_SHEET_VALUES_URL = FULL_SERVER_PATH + "/updateSheetValues";





    public static final String CANT_UPDATE_CELL_NO_CLICK_ON_LABEL = "first click on the cell \nyou wish to modify its value";



    // GSON instance
    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType(), new CellLocationMapDeserializer())
            .registerTypeAdapter(new TypeToken<Map<CellLocation, String>>() {}.getType(), new CellLocationToStringMapDeserializer())
            .registerTypeAdapter(DtoSheetCell.class, new DtoSheetCellDeserializer())
            .registerTypeAdapter(DtoContainerData.class, new DtoContainerDataDeserializer())
            .serializeSpecialFloatingPointValues()  // This allows serialization of NaN and Infinity
            .setPrettyPrinting()
            .create();
}
