package CoreParts.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public interface InputHandler {
    int getMenuOptionInput() throws Exception;
    String getCellInput(int userTryCount) throws Exception;
     String getCommandInput(int userTryCount) throws Exception;
     int getVersionInput() throws Exception;
     boolean getExitInput() throws Exception;
     boolean getSaveInput() throws Exception;
     String getFilePathInput() throws Exception;
}
