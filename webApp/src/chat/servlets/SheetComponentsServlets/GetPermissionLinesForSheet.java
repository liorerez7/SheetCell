package chat.servlets.SheetComponentsServlets;

import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.permissions.PermissionLine;
import engine.login.users.PermissionManager;

import java.util.List;

public class GetPermissionLinesForSheet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());
        PermissionManager permissionManager = engine.getPermissionManager();

        try {

            String sheetName = request.getParameter("sheetName");

            List<PermissionLine> permissions = permissionManager.getPermissionStatusOfSheet(sheetName);

            String permissionAsJson = Constants.GSON_INSTANCE.toJson(permissions);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(permissionAsJson);
            response.getWriter().flush();

        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }


    }
}
