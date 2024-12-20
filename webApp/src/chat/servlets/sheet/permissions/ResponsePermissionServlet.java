package chat.servlets.sheet.permissions;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.login.users.PermissionManager;
import dto.permissions.ResponsePermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResponsePermissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        PermissionManager permissionManager = ServletUtils.getPermissionManager(getServletContext());
        String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);

        try {
            synchronized (permissionManager) {
                List<ResponsePermission> requests = permissionManager.getUserResponsePermission(userName);
                String responseAsJson;

                if(requests == null) {
                    List list = new ArrayList();
                    responseAsJson = Constants.GSON_INSTANCE.toJson(list);
                }
                else {
                    responseAsJson = Constants.GSON_INSTANCE.toJson(requests);
                }

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
