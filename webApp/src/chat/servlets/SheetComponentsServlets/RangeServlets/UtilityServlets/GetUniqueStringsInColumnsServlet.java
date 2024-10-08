package chat.servlets.SheetComponentsServlets.RangeServlets.UtilityServlets;

import CoreParts.api.Engine;

import chat.constants.Constants;
import chat.utils.ServletUtils;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetUniqueStringsInColumnsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Retrieve parameters
        String columnsJson = request.getParameter("columns");
        String isChartGraphStr = request.getParameter("isChartGraph");
        String rangeName = request.getParameter("range");
        String column = request.getParameter("filterColumn");

        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            if (columnsJson != null && isChartGraphStr != null) {
                // If columns and isChartGraph parameters are provided, use the new implementation
                boolean isChartGraph = Boolean.parseBoolean(isChartGraphStr);
                List<Character> columnsForXYaxis = Constants.GSON_INSTANCE.fromJson(columnsJson, new TypeToken<List<Character>>(){}.getType());
                Map<Character, Set<String>> characterToSetMap = engine.getUniqueStringsInColumn(columnsForXYaxis, isChartGraph);

                String characterToSetMapAsJson = Constants.GSON_INSTANCE.toJson(characterToSetMap);
                response.getWriter().print(characterToSetMapAsJson);
                response.getWriter().flush();
                response.setStatus(HttpServletResponse.SC_OK);
            } else if (rangeName != null && column != null) {
                // If range and filterColumn are provided, use the existing implementation
                Map<Character, Set<String>> characterToSetMap = engine.getUniqueStringsInColumn(column, rangeName);

                String characterToSetMapAsJson = Constants.GSON_INSTANCE.toJson(characterToSetMap);
                response.getWriter().print(characterToSetMapAsJson);
                response.getWriter().flush();
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                // If neither set of parameters is provided, return a bad request status
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("Error: Missing required parameters.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().print("Error: " + e.getMessage());
        }
    }
}

