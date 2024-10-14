package chat.servlets.SheetComponentsServlets;

import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.login.users.PermissionManager;
import dto.permissions.PermissionStatus;

import java.util.HashMap;
import java.util.Map;

public class GetMyPermissionForSheet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Set response type
        response.setContentType("application/json;charset=UTF-8");
        try {
            String sheetName = request.getParameter("sheetName");

            Engine engine = ServletUtils.getEngineManager(getServletContext());
            PermissionManager permissionManager = engine.getPermissionManager();
            String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);

            PermissionStatus permission = permissionManager.getPermission(sheetName, userName);
            String permissionString = permission.toString();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(permissionString);
            response.getWriter().flush();
        }
        catch (Exception e) {
            // Set status to 409 explicitly
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }
}
