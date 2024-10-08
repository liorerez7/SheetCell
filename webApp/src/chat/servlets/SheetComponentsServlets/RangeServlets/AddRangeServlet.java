package chat.servlets.SheetComponentsServlets.RangeServlets;

import CoreParts.api.Engine;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AddRangeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String name = request.getParameter("name");
        String range = request.getParameter("range");

        Engine engine = ServletUtils.getEngine(getServletContext());

        try{
            engine.UpdateNewRange(name, range);
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getOutputStream().print("Error: " + e.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json;charset=UTF-8");
//        response.setCharacterEncoding("UTF-8");
//
//        Engine engine = ServletUtils.getEngine(getServletContext());
//        String rangeName = request.getParameter("name");
//        List<CellLocation> requestedRange = null;
//
//        try{
//            requestedRange = engine.getRequestedRange(rangeName);
//        }
//        catch (Exception e){
//            response.setStatus(HttpServletResponse.SC_CONFLICT);
//            response.getOutputStream().print("Error: " + e.getMessage());
//        }
//
//        String requestedRangeAsJson = Constants.GSON_INSTANCE.toJson(requestedRange);
//        response.getWriter().print(requestedRangeAsJson);
//        response.getWriter().flush();
//    }
}
