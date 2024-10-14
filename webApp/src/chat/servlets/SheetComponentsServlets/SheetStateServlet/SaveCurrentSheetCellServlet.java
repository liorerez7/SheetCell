package chat.servlets.SheetComponentsServlets.SheetStateServlet;

import engine.core_parts.api.SheetManager;
import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SaveCurrentSheetCellServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //SheetManager sheetManager = ServletUtils.getEngine(getServletContext());

        Engine engine = ServletUtils.getEngineManager(getServletContext());
        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = engine.getSheetCell(sheetName);

        try{
            sheetManager.saveCurrentSheetCellState();
            response.setStatus(HttpServletResponse.SC_OK);
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getOutputStream().print("Error: " + e.getMessage());
        }
    }
}
