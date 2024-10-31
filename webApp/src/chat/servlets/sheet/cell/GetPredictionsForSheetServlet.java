package chat.servlets.sheet.cell;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import com.google.gson.reflect.TypeToken;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import engine.Engine;
import engine.core_parts.api.SheetManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetPredictionsForSheetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String startingRangeCellLocation = request.getParameter("startingRangeCellLocation");
        String endingRangeCellLocation = request.getParameter("endingRangeCellLocation");
        String extendedRangeCellLocation = request.getParameter("extendedRangeCellLocation");
        String originalValuesByOrderAsJson = request.getParameter("originalValues");
        Map<String,String> originalValuesByOrder = Constants.GSON_INSTANCE.fromJson(originalValuesByOrderAsJson,
                new TypeToken<Map<String, String>>(){}.getType());


        Engine engine = ServletUtils.getEngineManager(getServletContext());
        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);

        SheetManager sheetManager = engine.getSheetCell(sheetName);


        synchronized (sheetManager) {

            String userName = (String) request.getSession(false).getAttribute(Constants.USERNAME);


            Map<String,String> resultStrings = sheetManager.getPredictionsForSheet(startingRangeCellLocation,
                    endingRangeCellLocation, extendedRangeCellLocation, originalValuesByOrder);


            SheetManager sheetManagerForPresentingTheInformation = engine.getTemporarySheetManager(userName);
            sheetManagerForPresentingTheInformation.updateMultipleCells(resultStrings);
            DtoSheetCell dtoSheetCellForPresentingDataOnly = sheetManagerForPresentingTheInformation.getSheetCell();


            response.getWriter().write(Constants.GSON_INSTANCE.toJson(dtoSheetCellForPresentingDataOnly));
            response.getWriter().flush();

        }

    }
}
