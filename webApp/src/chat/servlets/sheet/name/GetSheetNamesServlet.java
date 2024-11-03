package chat.servlets.sheet.name;

import dto.components.DtoSheetInfoLine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import engine.login.users.SheetInfosManager;
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

        try{
            String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);
            SheetInfosManager sheetInfosManager = ServletUtils.getSheetInfosManager(getServletContext());
            Set<DtoSheetInfoLine> sheetInfos = sheetInfosManager.getSheetInfos(userName);
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
