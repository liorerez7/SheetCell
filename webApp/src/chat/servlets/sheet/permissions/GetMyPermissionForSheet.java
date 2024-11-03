package chat.servlets.sheet.permissions;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.login.users.PermissionManager;
import dto.permissions.PermissionStatus;

public class GetMyPermissionForSheet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json;charset=UTF-8");
        String sheetName = request.getParameter("sheetName");
        PermissionManager permissionManager = ServletUtils.getPermissionManager(getServletContext());

        try {

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
