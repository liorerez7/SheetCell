package chat.servlets.sheet.loadXML;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import chat.utilities.SessionUtils;
import dto.permissions.PermissionStatus;
import dto.permissions.RequestStatus;
import engine.Engine;
import engine.core_parts.api.SheetManager;
import engine.login.users.PermissionManager;
import engine.login.users.SheetInfosManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;

public class CreateNewSheetServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());

        String userName = SessionUtils.getUsername(request);

        String sheetName = request.getParameter("sheetName");
        int cellWidth = Integer.parseInt(request.getParameter("cellWidth"));
        int cellLength = Integer.parseInt(request.getParameter("cellLength"));
        int numColumns = Integer.parseInt(request.getParameter("numColumns"));
        int numRows = Integer.parseInt(request.getParameter("numRows"));

        try {
            synchronized (engine) {

                engine.createNewSheet(sheetName, cellWidth, cellLength, numColumns, numRows, userName);
                SheetManager sheetManager = engine.getSheetCell(sheetName);

                PermissionManager permissionManager = engine.getPermissionManager();
                SheetInfosManager sheetInfosManager = engine.getSheetInfosManager();

                permissionManager.addPermission(sheetName, userName, PermissionStatus.OWNER, RequestStatus.APPROVED);
                sheetInfosManager.newSheetWasCreated(sheetName, userName, sheetManager.getSheetCell().getSheetSize(), PermissionStatus.OWNER.toString());

                String sheetNameAsJson = Constants.GSON_INSTANCE.toJson(sheetName);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(sheetNameAsJson);
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            String errorMessage = ServletUtils.extractErrorMessage(e);
            String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
            response.getWriter().write(errorMessageAsJson);
            response.getWriter().flush();
        }
    }
}
