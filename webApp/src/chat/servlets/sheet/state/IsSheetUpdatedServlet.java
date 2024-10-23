package chat.servlets.sheet.state;

import engine.core_parts.api.SheetManager;
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
            String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
            SheetManager sheetManager = ServletUtils.getSheetManager(getServletContext(), sheetName);

            String sheetVersion = request.getParameter("sheetVersion");
            int versionNumber = Integer.parseInt(sheetVersion);

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
