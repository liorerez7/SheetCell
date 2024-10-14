package chat.servlets.SheetComponentsServlets.SheetStateServlet;

import engine.core_parts.api.SheetManager;
import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class IsSheetUpdatedServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain;charset=UTF-8");

        try {


            Engine engine = ServletUtils.getEngineManager(getServletContext());
            String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
            SheetManager sheetManager = engine.getSheetCell(sheetName);

            String sheetVersion = request.getParameter("sheetVersion");
            int versionNumber = Integer.parseInt(sheetVersion);

            System.out.println("Sheet version: " + sheetVersion);
            System.out.println("Sheet latest version: " + sheetManager.getSheetCell().getLatestVersion());
            if (sheetManager.getSheetCell().getLatestVersion() > versionNumber) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("true");
                response.getWriter().flush();
            }
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
