package chat.servlets.sheet.range;

import engine.core_parts.api.SheetManager;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AddRangeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String name = request.getParameter("name");
        String range = request.getParameter("range");

        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = ServletUtils.getSheetManager(getServletContext(), sheetName);

        try{
            sheetManager.UpdateNewRange(name, range);
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getOutputStream().print("Error: " + e.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
