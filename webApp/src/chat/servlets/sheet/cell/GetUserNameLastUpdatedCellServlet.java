package chat.servlets.sheet.cell;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import dto.small_parts.CellLocation;
import engine.Engine;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public class GetUserNameLastUpdatedCellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());

        String sheetName = request.getParameter("sheetName");

        try {
            synchronized (engine) {
                Map<CellLocation,String> cellLocationToUserName = engine.getCellLocationToUserName(sheetName);
                String mapAsJson = Constants.GSON_INSTANCE.toJson(cellLocationToUserName);
                response.getWriter().write(mapAsJson);
                response.getWriter().flush();
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
