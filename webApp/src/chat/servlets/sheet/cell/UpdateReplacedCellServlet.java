package chat.servlets.sheet.cell;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import engine.Engine;
import engine.core_parts.api.SheetManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class UpdateReplacedCellServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String newValue = request.getParameter("newValue");
        String newValueLocationsJson = request.getParameter("newValueLocations");

        Set<CellLocation> newValueLocations = Constants.GSON_INSTANCE.fromJson(newValueLocationsJson,
                new TypeToken<Set<CellLocation>>(){}.getType());


        Engine engine = ServletUtils.getEngineManager(getServletContext());


        synchronized (engine) {

            try{

                String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
                String username = (String) request.getSession(false).getAttribute(Constants.USERNAME);

                SheetManager sheetCell = engine.getTemporarySheetManager(username);

                sheetCell.saveCurrentSheetCellState();

                sheetCell.updateReplacedCells(newValue, newValueLocations);

                DtoSheetCell dtoSheetCell = sheetCell.getSheetCell();

                PrintWriter out = response.getWriter();

                String jsonResponse = Constants.GSON_INSTANCE.toJson(dtoSheetCell);

                out.print(jsonResponse);

                out.flush();

                sheetCell.restoreSheetCellState();

                //response.setStatus(HttpServletResponse.SC_OK);

            }catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String errorMessage = ServletUtils.extractErrorMessage(e);
                String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(errorMessageAsJson);
                response.getWriter().flush();
            }
        }

    }

}
