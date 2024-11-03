package chat.servlets.sheet.range;

import engine.core_parts.api.SheetManager;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.small_parts.CellLocation;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class GetRangeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = ServletUtils.getSheetManager(getServletContext(), sheetName);

        String rangeName = request.getParameter("name");
        List<CellLocation> requestedRange = null;

        if(rangeName == null){
            try{
                Set<String> allRanges = sheetManager.getAllRangeNames();
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
                requestedRange = sheetManager.getRequestedRange(rangeName);
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
