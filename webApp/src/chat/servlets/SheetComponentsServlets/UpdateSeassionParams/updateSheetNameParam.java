package chat.servlets.SheetComponentsServlets.UpdateSeassionParams;

import chat.utilities.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class updateSheetNameParam extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set response type
        response.setContentType("text/plain;charset=UTF-8");
        try {
            String sheetName = request.getParameter("sheetName");
            request.getSession(true).setAttribute(Constants.SHEET_NAME, sheetName);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT); // Set status to 409 explicitly
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("Error: " + e.getMessage()); // Use getWriter() instead of getOutputStream() for text
        }
    }
}
