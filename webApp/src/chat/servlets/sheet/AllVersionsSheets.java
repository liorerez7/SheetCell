package chat.servlets.sheet;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import dto.components.DtoSheetCell;
import engine.core_parts.api.SheetManager;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class AllVersionsSheets extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = ServletUtils.getSheetManager(getServletContext(), sheetName);
        Map<Integer,DtoSheetCell> versionToSheetCell = new HashMap<>();

        synchronized (sheetManager) {

            int latestVersion = sheetManager.getSheetCell().getLatestVersion();

            for(int i = 1; i<=latestVersion; i++){

                DtoSheetCell dtoSheetCell = sheetManager.getSheetCell(i);
                versionToSheetCell.put(i,dtoSheetCell);
            }

            PrintWriter out = response.getWriter();
            String jsonResponse = Constants.GSON_INSTANCE.toJson(versionToSheetCell);
            out.print(jsonResponse);
            out.flush();
        }
    }

}