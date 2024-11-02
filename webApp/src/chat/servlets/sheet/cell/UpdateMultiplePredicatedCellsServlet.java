package chat.servlets.sheet.cell;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import com.google.gson.reflect.TypeToken;
import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import dto.small_parts.UpdateCellInfo;
import engine.Engine;
import engine.core_parts.api.SheetManager;
import engine.utilities.exception.CycleDetectedException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateMultiplePredicatedCellsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String newCellsToBeUpdateAsJson = request.getParameter("newValues");

        Map<String,String> newCellsToBeUpdate = Constants.GSON_INSTANCE.fromJson(newCellsToBeUpdateAsJson,
                new TypeToken<Map<String,String>>(){}.getType());


        Engine engine = ServletUtils.getEngineManager(getServletContext());


        synchronized (engine) {

            try{

                String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
                String username = (String) request.getSession(false).getAttribute(Constants.USERNAME);

                SheetManager sheetManager = engine.getSheetCell(sheetName);


                DtoSheetCell beforeUpdateDtoSheetCell = sheetManager.getSheetCell();
                List<DtoCell> beforeUpdateCells = new ArrayList<>();

                Set<String> set = newCellsToBeUpdate.keySet();
                set.forEach(string -> {
                    DtoCell dtoCell = beforeUpdateDtoSheetCell.getRequestedCell(string);
                    if(dtoCell != null){
                        beforeUpdateCells.add(dtoCell);
                    }
                });

                sheetManager.updateReplacedCells(newCellsToBeUpdate);

                DtoSheetCell afterUpdateDtoSheetCell = sheetManager.getSheetCell();
                List<DtoCell> afterUpdateCells = new ArrayList<>();

                Set<String> newSet = newCellsToBeUpdate.keySet();
                set.forEach(string -> {
                    DtoCell dtoCell = afterUpdateDtoSheetCell.getRequestedCell(string);
                    if(dtoCell != null){
                        afterUpdateCells.add(dtoCell);
                    }
                });


                Set<CellLocation> newValueLocations = afterUpdateCells.stream()
                        .map(DtoCell::getLocation).collect(Collectors.toSet());

                engine.updateUsersInCells(beforeUpdateDtoSheetCell, afterUpdateDtoSheetCell, username);
                updateVersionToCellInfo(beforeUpdateCells, afterUpdateCells, newValueLocations, username, engine);

                response.setStatus(HttpServletResponse.SC_OK);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String errorMessage = ServletUtils.extractErrorMessage(e);
                if(e instanceof CycleDetectedException){
                    errorMessage = "Cycle detected: " + errorMessage;
                }
                String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(errorMessageAsJson);
                response.getWriter().flush();
            }
        }

    }

    private void updateVersionToCellInfo(List<DtoCell> beforeUpdateCells, List<DtoCell> afterUpdateCells,
                                         Set<CellLocation> newValueLocation, String username, Engine engine) {

        String newActualValue = afterUpdateCells.getFirst().getEffectiveValue().getValue().toString();
        String newOriginalValue = afterUpdateCells.getFirst().getOriginalValue();
        int versionNumber = afterUpdateCells.getFirst().getLatestVersion();
        CellLocation location = afterUpdateCells.getFirst().getLocation();

        String oldActualValue = "";
        String oldOriginalValue = "";

        if(beforeUpdateCells.getFirst() != null){
            oldActualValue = beforeUpdateCells.getFirst().getEffectiveValue().getValue().toString();
            oldOriginalValue = beforeUpdateCells.getFirst().getOriginalValue();
        }

        engine.getVersionToCellInfo().put(versionNumber, new UpdateCellInfo("old sequence",
                "new sequence", "old sequence",
                "new sequence", versionNumber, username, newValueLocation, true));

    }

}

