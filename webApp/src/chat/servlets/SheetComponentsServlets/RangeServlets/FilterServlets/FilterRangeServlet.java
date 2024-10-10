package chat.servlets.SheetComponentsServlets.RangeServlets.FilterServlets;

import CoreParts.api.SheetManager;
import DtoComponents.DtoContainerData;
import EngineManager.Engine;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;


public class FilterRangeServlet extends HttpServlet {
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String rangeName = request.getParameter("range");
//        String filterJson = request.getParameter("filter");
//
//        Engine engine = ServletUtils.getEngineManager(getServletContext());
//        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
//        SheetManager sheetManager = engine.getSheetCell(sheetName);
//        //SheetManager sheetManager = ServletUtils.getEngine(getServletContext());
//
//        try {
//            Type filterType = new TypeToken<Map<Character, Set<String>>>(){}.getType();
//            Map<Character, Set<String>> filterdMap = Constants.GSON_INSTANCE.fromJson(filterJson, filterType);
//
//            DtoContainerData dtoContainerData = sheetManager.filterSheetCell( rangeName, filterdMap);
//
//            String dtoContainerAsJson = Constants.GSON_INSTANCE.toJson(dtoContainerData);
//            response.getWriter().print(dtoContainerAsJson);
//            response.getWriter().flush();
//            response.setStatus(HttpServletResponse.SC_OK);
//
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_CONFLICT);
//            response.getOutputStream().print("Error: " + e.getMessage());
//        }
//
//    }
}
