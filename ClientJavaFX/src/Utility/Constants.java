package Utility;

import DtoComponents.DtoContainerData;
import DtoComponents.DtoSheetCell;
import Utility.HttpUtility.jsonDeSerialzableClasses.CellLocationMapDeserializer;
import Utility.HttpUtility.jsonDeSerialzableClasses.DtoContainerDataDeserializer;
import Utility.HttpUtility.jsonDeSerialzableClasses.DtoSheetCellDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import smallParts.CellLocation;
import smallParts.EffectiveValue;


import java.util.Map;

public class Constants {

    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    // fxml locations
    public final static String MAIN_APP_PAGE_FXML_RESOURCE_LOCATION = "/Controller/Main/SheetCell.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/Controller/LoginScreen/login.fxml";
    public static final String DASHBOARD_PAGE_FXML_RESOURCE_LOCATION = "/Controller/DashboardScreen/dashboard.fxml";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/webApp_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/usersList";
    public final static String LOGOUT = FULL_SERVER_PATH + "/chat/logout";
    public final static String SEND_CHAT_LINE = FULL_SERVER_PATH + "/pages/chatroom/sendChat";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";

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








    public static final String NEWER_VERSION__ERROR_MESSAGE = "there is a newer version" +
            " of this sheet\nPress the " +
            "UpdateSheet button inorder to ";

    public static final String CANT_UPDATE_CELL_NEWER_VERSION_MESSAGE =  NEWER_VERSION__ERROR_MESSAGE + "update cells";

    public static final String CANT_ADD_RANGE_NEWER_VERSION_MESSAGE = NEWER_VERSION__ERROR_MESSAGE + "add ranges";
    public static final String CANT_DELETE_RANGE_NEWER_VERSION_MESSAGE = NEWER_VERSION__ERROR_MESSAGE + "delete ranges";












    // GSON instance
    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType(), new CellLocationMapDeserializer())
            .registerTypeAdapter(DtoSheetCell.class, new DtoSheetCellDeserializer())
            .registerTypeAdapter(DtoContainerData.class, new DtoContainerDataDeserializer())
            .setPrettyPrinting()
            .create();

    //public final static Gson GSON_INSTANCE = new Gson();
}
