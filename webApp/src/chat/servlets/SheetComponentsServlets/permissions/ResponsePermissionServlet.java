package chat.servlets.SheetComponentsServlets.permissions;

import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.login.users.PermissionManager;
import engine.login.users.ResponsePermission;
import engine.login.users.SheetInfosManager;

import java.io.IOException;
import java.util.List;

public class ResponsePermissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());
        PermissionManager permissionManager = engine.getPermissionManager();
        SheetInfosManager sheetInfosManager = engine.getSheetInfosManager();

        String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);

        try {
            synchronized (permissionManager) {
                List<ResponsePermission> requests = permissionManager.getUserResponsePermission(userName);
                String responseAsJson = Constants.GSON_INSTANCE.toJson(requests);
                response.getWriter().write(responseAsJson);
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