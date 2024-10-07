package util;

import Controller.HttpUtility.jsonDeSerialzableClasses.CellLocationMapDeserializer;
import Controller.HttpUtility.jsonDeSerialzableClasses.CellLocationMapSerializer;
import Controller.HttpUtility.jsonDeSerialzableClasses.DtoSheetCellDeserializer;
import Controller.HttpUtility.jsonDeSerialzableClasses.DtoSheetCellSerializer;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import expression.ReturnedValueType;
import expression.impl.variantImpl.EffectiveValue;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Constants {

    // GSON instance
    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<CellLocation, EffectiveValue>>() {
            }.getType(), new CellLocationMapSerializer())
            .registerTypeAdapter(new TypeToken<Map<CellLocation, EffectiveValue>>() {
            }.getType(), new CellLocationMapDeserializer())
            .registerTypeAdapter(DtoSheetCell.class, new DtoSheetCellSerializer())
            .registerTypeAdapter(DtoSheetCell.class, new DtoSheetCellDeserializer())
            .setPrettyPrinting()
            .create();


    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/main/chat-app-main.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/login/login.fxml";
    public final static String CHAT_ROOM_FXML_RESOURCE_LOCATION = "/chat/client/component/chatroom/chat-room-main.fxml";

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


}
