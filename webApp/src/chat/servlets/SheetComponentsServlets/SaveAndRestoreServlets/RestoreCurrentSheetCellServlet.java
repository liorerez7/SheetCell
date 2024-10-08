package chat.servlets.SheetComponentsServlets.SaveAndRestoreServlets;

import CoreParts.api.Engine;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RestoreCurrentSheetCellServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Engine engine = ServletUtils.getEngine(getServletContext());
        try{
            engine.restoreSheetCellState();
            response.setStatus(HttpServletResponse.SC_OK);
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getOutputStream().print("Error: " + e.getMessage());
        }
    }
}
