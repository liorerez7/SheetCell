package chat.servlets.SheetComponentsServlets.SheetStateServlet;

import CoreParts.api.SheetManager;
import EngineManager.Engine;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class IsSheetUpdatedServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain;charset=UTF-8");

        Engine engine = ServletUtils.getEngineManager(getServletContext());
        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = engine.getSheetCell(sheetName);

        // i want to return 200.
        //write here a code that just return the response of 200.
        response.setStatus(HttpServletResponse.SC_OK);

    }
}
