package chat.servlets.sheet.state;

import dto.small_parts.CellLocation;
import engine.Engine;
import engine.core_parts.api.SheetManager;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class IsSheetUpdatedServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain;charset=UTF-8");
        Engine engine = ServletUtils.getEngineManager(getServletContext());

        try {
            synchronized (engine) {

                String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
                String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);

                SheetManager sheetManager = engine.getSheetCell(sheetName);

                String sheetVersion = request.getParameter("sheetVersion");
                int versionNumber = Integer.parseInt(sheetVersion);


                int latestVersionInSystem = sheetManager.getSheetCell().getLatestVersion();

                if(!userName.equals("lior")){
                    int x = 5;
                }

                CellLocation locationOfLastUpdatedCell = engine.getVersionToCellInfo(sheetName).get(latestVersionInSystem).getLocations().iterator().next();
                String lastUpdatedUserName = engine.getCellLocationToUserName(sheetName).get(locationOfLastUpdatedCell);

                if (sheetManager.getSheetCell().getLatestVersion() > versionNumber) {

                    if(!lastUpdatedUserName.equals(userName)){
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("true");
                        response.getWriter().flush();
                    }

//
//                    response.setStatus(HttpServletResponse.SC_OK);
//                    response.getWriter().write("true");
//                    response.getWriter().flush();
                }
            }
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
