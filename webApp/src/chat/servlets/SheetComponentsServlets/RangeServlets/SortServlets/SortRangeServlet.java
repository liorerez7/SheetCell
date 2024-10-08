package chat.servlets.SheetComponentsServlets.RangeServlets.SortServlets;

import CoreParts.api.Engine;
import Utility.DtoContainerData;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SortRangeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rangeName = request.getParameter("range");
        String column = request.getParameter("columns");


        Engine engine = ServletUtils.getEngine(getServletContext());

        try{
            DtoContainerData dtoContainerData = engine.sortSheetCell(rangeName, column);
            String dtoContainerDataAsJson = Constants.GSON_INSTANCE.toJson(dtoContainerData);
            response.getWriter().print(dtoContainerDataAsJson);
            response.getWriter().flush();
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getOutputStream().print("Error: " + e.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
