package chat.servlets.SheetComponentsServlets.RangeServlets.UtilityServlets;

import CoreParts.api.Engine;
import Utility.DtoContainerData;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class GetUniqueStringsInColumnsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rangeName = request.getParameter("range");
        String column = request.getParameter("filterColumn");

        Engine engine = ServletUtils.getEngine(getServletContext());

        try{
            Map<Character, Set<String>> characterToSetMap = engine.getUniqueStringsInColumn(column, rangeName);
            String characterToSetMapAsJson = Constants.GSON_INSTANCE.toJson(characterToSetMap);
            response.getWriter().print(characterToSetMapAsJson);
            response.getWriter().flush();
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getOutputStream().print("Error: " + e.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
