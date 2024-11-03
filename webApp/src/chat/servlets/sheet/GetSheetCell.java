package chat.servlets.sheet;

import engine.core_parts.api.SheetManager;
import dto.components.DtoSheetCell;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@MultipartConfig
public class GetSheetCell extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String versionNumber = request.getParameter("versionNumber");

        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = ServletUtils.getSheetManager(getServletContext(), sheetName);

        if(versionNumber == null){
            DtoSheetCell dtoSheetCell = sheetManager.getSheetCell();
            PrintWriter out = response.getWriter();
            String jsonResponse = Constants.GSON_INSTANCE.toJson(dtoSheetCell);
            out.print(jsonResponse);
            out.flush();
        }
        else {
            int version = Integer.parseInt(versionNumber);
            DtoSheetCell dtoSheetCell = sheetManager.getSheetCell(version);
            PrintWriter out = response.getWriter();
            String jsonResponse = Constants.GSON_INSTANCE.toJson(dtoSheetCell);
            out.print(jsonResponse);
            out.flush();
        }
    }
}