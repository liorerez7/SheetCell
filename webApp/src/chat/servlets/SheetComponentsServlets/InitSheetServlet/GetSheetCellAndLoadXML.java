package chat.servlets.SheetComponentsServlets.InitSheetServlet;

import dto.permissions.RequestStatus;
import engine.core_parts.api.SheetManager;
import dto.components.DtoSheetCell;
import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import chat.utilities.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import engine.login.users.PermissionManager;
import dto.permissions.PermissionStatus;
import engine.login.users.SheetInfosManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@MultipartConfig
public class GetSheetCellAndLoadXML extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());
        PermissionManager permissionManager = engine.getPermissionManager();
        SheetInfosManager sheetInfosManager = engine.getSheetInfosManager();

//        PermissionManager permissionManager = ServletUtils.getPermissionManager(getServletContext());
        String userName = SessionUtils.getUsername(request);

        Part filePart = null;
        try {
            filePart = request.getPart("file1");
        } catch (ServletException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMessage = ServletUtils.extractErrorMessage(e);
            String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
            response.getWriter().write(errorMessageAsJson);
            response.getWriter().flush();
            return;
        }

        try (InputStream fileContent = filePart.getInputStream()) {
            SheetManager sheetManager = engine.getSheetCell(fileContent, userName);
            String sheetName = sheetManager.getSheetCell().getSheetName();

            synchronized (permissionManager) {
                permissionManager.addPermission(sheetName, userName, PermissionStatus.OWNER, RequestStatus.APPROVED);
                sheetInfosManager.newSheetWasCreated(sheetName, userName, sheetManager.getSheetCell().getSheetSize(), PermissionStatus.OWNER.toString());
            }

            String sheetNameAsJson = Constants.GSON_INSTANCE.toJson(sheetName);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(sheetNameAsJson);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            String errorMessage = ServletUtils.extractErrorMessage(e);
            String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
            response.getWriter().write(errorMessageAsJson);
            response.getWriter().flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String versionNumber = request.getParameter("versionNumber");

        //SheetManager sheetManager = ServletUtils.getEngine(getServletContext());
        Engine engine = ServletUtils.getEngineManager(getServletContext());

        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = engine.getSheetCell(sheetName);

        if(versionNumber == null){
            DtoSheetCell dtoSheetCell = sheetManager.getSheetCell();
            PrintWriter out = response.getWriter();
            String jsonResponse = Constants.GSON_INSTANCE.toJson(dtoSheetCell);
            out.print(jsonResponse);
            out.flush();
        }
        else {
            int version = Integer.parseInt(versionNumber);
            DtoSheetCell dtoSheetCell = sheetManager.getSheetCell(version);
            PrintWriter out = response.getWriter();
            String jsonResponse = Constants.GSON_INSTANCE.toJson(dtoSheetCell);
            out.print(jsonResponse);
            out.flush();
        }
    }
}


//        String userName1 = (String) request.getSession(false).getAttribute(Constants.USERNAME);
