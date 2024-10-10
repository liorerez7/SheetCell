package chat.servlets.SheetComponentsServlets.InitSheetServlet;

import chat.constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class GetMySheetNameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get the current user's sheet name from the session
        String sheetName = (String) req.getSession(false).getAttribute(Constants.SHEET_NAME);
        // Send the sheet name as a response
        if(sheetName == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.getWriter().write(sheetName);
        resp.getWriter().flush();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
