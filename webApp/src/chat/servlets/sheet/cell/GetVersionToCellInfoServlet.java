package chat.servlets.sheet.cell;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import dto.small_parts.UpdateCellInfo;
import engine.Engine;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class GetVersionToCellInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());

        try {
            synchronized (engine){
                Map<Integer, UpdateCellInfo> versionToCellInfo = engine.getVersionToCellInfo();
                String versionToCellInfoAsJson = Constants.GSON_INSTANCE.toJson(versionToCellInfo);
                System.out.println("versionToCellInfoAsJson = " + versionToCellInfoAsJson);
                response.getWriter().write(versionToCellInfoAsJson);
                response.getWriter().flush();
                response.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
