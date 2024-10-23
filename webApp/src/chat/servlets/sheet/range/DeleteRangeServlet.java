package chat.servlets.sheet.range;

import engine.core_parts.api.SheetManager;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DeleteRangeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");

        if (name != null) {

            String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
            SheetManager sheetManager = ServletUtils.getSheetManager(getServletContext(), sheetName);

            sheetManager.deleteRange(name);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Invalid range name");
        }
    }
}
