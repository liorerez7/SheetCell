package chat.servlets.SheetComponentsServlets.InitSheetServlet;

import CoreParts.api.SheetManager;
import EngineManager.Engine;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class GetSheetNamesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try{
            Engine engine = ServletUtils.getEngineManager(getServletContext());
            Set<String> sheetNames = engine.getSheetNames();
            String sheetNamesAsJson = Constants.GSON_INSTANCE.toJson(sheetNames);
            PrintWriter out = response.getWriter();
            out.print(sheetNamesAsJson);
            out.flush();
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMessage = ServletUtils.extractErrorMessage(e);
            String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
            response.getWriter().write(errorMessageAsJson);
            response.getWriter().flush();
        }



    }

}
