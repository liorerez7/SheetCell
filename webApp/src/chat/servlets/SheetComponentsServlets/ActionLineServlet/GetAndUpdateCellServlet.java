package chat.servlets.SheetComponentsServlets.ActionLineServlet;

import engine.core_parts.api.SheetManager;

import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class GetAndUpdateCellServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String cellValue = request.getParameter("newValue");
        char columnOfCell = request.getParameter("colLocation").charAt(0);
        String row = request.getParameter("rowLocation");

        Engine engine = ServletUtils.getEngineManager(getServletContext());
        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);

        // Synchronize on the engine or the sheet manager
        synchronized (engine) {  // Alternatively, you can synchronize on sheetManager if it's a better fit
            SheetManager sheetManager = engine.getSheetCell(sheetName);

            try {
                sheetManager.updateCell(cellValue, columnOfCell, row);
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String errorMessage = ServletUtils.extractErrorMessage(e);
                String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(errorMessageAsJson);
                response.getWriter().flush();
            }
        }
    }
}
