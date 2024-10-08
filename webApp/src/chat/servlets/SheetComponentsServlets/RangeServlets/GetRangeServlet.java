package chat.servlets.SheetComponentsServlets.RangeServlets;

import CoreParts.api.Engine;

import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import smallParts.CellLocation;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class GetRangeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        Engine engine = ServletUtils.getEngine(getServletContext());
        String rangeName = request.getParameter("name");
        List<CellLocation> requestedRange = null;

        if(rangeName == null){
            try{
                Set<String> allRanges = engine.getAllRangeNames();
                String allRangesJson = Constants.GSON_INSTANCE.toJson(allRanges);
                response.getWriter().print(allRangesJson);
                response.getWriter().flush();
            }catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getOutputStream().print("Error: " + e.getMessage());
            }
        }
        else{
            try{
                requestedRange = engine.getRequestedRange(rangeName);
                String requestedRangeAsJson = Constants.GSON_INSTANCE.toJson(requestedRange);
                response.getWriter().print(requestedRangeAsJson);
                response.getWriter().flush();
            }
            catch (Exception e){
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getOutputStream().print("Error: " + e.getMessage());
            }
        }
    }
}
