package Controller.HttpUtility;

import Controller.HttpUtility.jsonDeSerialzableClasses.*;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import expression.impl.variantImpl.EffectiveValue;

import java.util.Map;

public class Constants {

    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    // fxml locations
    public final static String MAIN_APP_PAGE_FXML_RESOURCE_LOCATION = "/Controller/Main/SheetCell.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/Controller/login/login.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/webApp_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";
    public final static String LOGOUT = FULL_SERVER_PATH + "/chat/logout";
    public final static String SEND_CHAT_LINE = FULL_SERVER_PATH + "/pages/chatroom/sendChat";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";
    public final static String INIT_SHEET_CELL_ENDPOINT = FULL_SERVER_PATH + "/xmlAddress";
    public static final String GET_SHEET_CELL_ENDPOINT = FULL_SERVER_PATH + "/sheetCell";
    public static final String UPDATE_CELL_ENDPOINT = FULL_SERVER_PATH + "/updateCell";
    public static final String GET_REQUESTED_CELL_ENDPOINT = FULL_SERVER_PATH + "/requestedCell";



    // GSON instance
    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<CellLocation, EffectiveValue>>() {}.getType(), new CellLocationMapDeserializer())
            .registerTypeAdapter(DtoSheetCell.class, new DtoSheetCellDeserializer())
            .setPrettyPrinting()
            .create();

    //public final static Gson GSON_INSTANCE = new Gson();
}
