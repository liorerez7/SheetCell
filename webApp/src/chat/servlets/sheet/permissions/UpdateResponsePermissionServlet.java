package chat.servlets.sheet.permissions;

import dto.permissions.RequestStatus;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import engine.Engine;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.login.users.PermissionManager;
import dto.permissions.PermissionStatus;
import engine.login.users.SheetInfosManager;

import java.io.IOException;

public class UpdateResponsePermissionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        Engine engine = ServletUtils.getEngineManager(getServletContext());
        PermissionManager permissionManager = engine.getPermissionManager();
        SheetInfosManager sheetInfosManager = engine.getSheetInfosManager();

        String ownerName = (String) request.getSession(false).getAttribute(Constants.USERNAME);

        try {
            String sheetName = request.getParameter("sheetName");

            String permission = request.getParameter("permission");
            permission = permission.toUpperCase();
            PermissionStatus permissionStatus = PermissionStatus.valueOf(permission);

            String userName = request.getParameter("userName");

            String requestStatus = request.getParameter("requestStatus");
            requestStatus = requestStatus.toUpperCase();
            RequestStatus requestStatusEnum = RequestStatus.valueOf(requestStatus);

            synchronized (engine) {  // Synchronize on the engine instead of individual managers
                permissionManager.updateOwnerResponseForRequest(ownerName, sheetName, userName, permissionStatus, requestStatusEnum);
                if(requestStatus.equals(RequestStatus.APPROVED.toString())){
                    sheetInfosManager.updateSheetPermissions(ownerName, sheetName, userName);
                }
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
