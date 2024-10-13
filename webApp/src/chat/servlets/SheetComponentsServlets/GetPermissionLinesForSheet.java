package chat.servlets.SheetComponentsServlets;

import EngineManager.Engine;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loginPage.users.PermissionLine;
import loginPage.users.PermissionManager;
import loginPage.users.PermissionStatus;

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
