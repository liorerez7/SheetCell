package CoreParts.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public interface InputHandler {
    int getMenuOptionInput() throws Exception;
    String getCellInput() throws Exception;
     String getCommandInput() throws Exception;
     int getVersionInput() throws Exception;
}
