package chat.servlets.SheetComponentsServlets.InitSheetServlet;

import dto.components.DtoSheetInfoLine;
import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class GetSheetNamesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");


        String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);

        try{
            Engine engine = ServletUtils.getEngineManager(getServletContext());
            Set<DtoSheetInfoLine> sheetInfos = engine.getSheetInfosManager().getSheetInfos(userName);
            String sheetInfosAsJson = Constants.GSON_INSTANCE.toJson(sheetInfos);

            if(sheetInfos == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            PrintWriter out = response.getWriter();
            out.print(sheetInfosAsJson);
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
