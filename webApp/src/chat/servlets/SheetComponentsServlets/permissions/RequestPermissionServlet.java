package chat.servlets.SheetComponentsServlets.permissions;

import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.login.users.PermissionManager;
import dto.permissions.PermissionStatus;
import dto.permissions.RequestPermission;
import engine.login.users.SheetInfosManager;

import java.io.IOException;
import java.util.List;

public class RequestPermissionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());
        PermissionManager permissionManager = engine.getPermissionManager();
        SheetInfosManager sheetInfosManager = engine.getSheetInfosManager();

        String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);



        try {
            String sheetName = request.getParameter("sheetName");
            String permission = request.getParameter("permission");
            permission = permission.toUpperCase();
            PermissionStatus permissionStatus = PermissionStatus.valueOf(permission);
            synchronized (permissionManager) {
                permissionManager.addRequestPermission(sheetName, userName, permissionStatus);
            }
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMessage = ServletUtils.extractErrorMessage(e);
            String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
            response.getWriter().write(errorMessageAsJson);
            response.getWriter().flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());
        PermissionManager permissionManager = engine.getPermissionManager();
        SheetInfosManager sheetInfosManager = engine.getSheetInfosManager();

        String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);

        try {
            synchronized (permissionManager) {
                List<RequestPermission> requests = permissionManager.getUserRequestPermission(userName);
                String requestsAsJson = Constants.GSON_INSTANCE.toJson(requests);
                response.getWriter().write(requestsAsJson);
                response.getWriter().flush();
            }
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMessage = ServletUtils.extractErrorMessage(e);
            String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
            response.getWriter().write(errorMessageAsJson);
            response.getWriter().flush();
        }
    }
}

