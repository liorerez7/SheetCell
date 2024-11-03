package chat.servlets.sheet.cell;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import engine.Engine;
import engine.core_parts.api.SheetManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class PostTempSheetServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        int versionNumber = Integer.parseInt(request.getParameter("versionNumber"));
        String toDelete = request.getParameter("delete");
        Engine engine = ServletUtils.getEngineManager(getServletContext());
        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);


            synchronized (engine) {

                if(toDelete.equals("false")) {
                    try {
                        engine.addTemporarySheetToRunTime(userName, versionNumber, sheetName);
                    } catch (Exception e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorMessage = ServletUtils.extractErrorMessage(e);
                        String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(errorMessageAsJson);
                    }
//                }
////                else{
//                    try {
//                        engine.removeTemporarySheet(userName);
//                    }catch (Exception e){
//                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                        String errorMessage = ServletUtils.extractErrorMessage(e);
//                        String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
//                        response.setContentType("application/json");
//                        response.setCharacterEncoding("UTF-8");
//                        response.getWriter().write(errorMessageAsJson);
//                    }
                }else{
                    try {
                        engine.removeTemporarySheetRunTime(userName);
                    }catch (Exception e){
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorMessage = ServletUtils.extractErrorMessage(e);
                        String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(errorMessageAsJson);
                    }
                }
            }


        //}

    }
}
