package chat.servlets.SheetComponentsServlets.RangeServlets.SortServlets;

import CoreParts.api.SheetManager;
import DtoComponents.DtoContainerData;
import EngineManager.Engine;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SortRangeServlet extends HttpServlet {

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String rangeName = request.getParameter("range");
//        String column = request.getParameter("columns");
//
//        Engine engine = ServletUtils.getEngineManager(getServletContext());
//        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
//        SheetManager sheetManager = engine.getSheetCell(sheetName);
//        //SheetManager sheetManager = ServletUtils.getEngine(getServletContext());
//
//        try{
//            DtoContainerData dtoContainerData = sheetManager.sortSheetCell(rangeName, column);
//            String dtoContainerDataAsJson = Constants.GSON_INSTANCE.toJson(dtoContainerData);
//            response.getWriter().print(dtoContainerDataAsJson);
//            response.getWriter().flush();
//        }
//        catch (Exception e){
//            response.setStatus(HttpServletResponse.SC_CONFLICT);
//            response.getOutputStream().print("Error: " + e.getMessage());
//        }
//
//        response.setStatus(HttpServletResponse.SC_OK);
//    }
}
