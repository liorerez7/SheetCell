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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpdateValuesInSheetServlet extends HttpServlet {

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

                SheetManager sheetManager = engine.getSheetCell(sheetName);


                DtoSheetCell beforeUpdateDtoSheetCell = sheetManager.getSheetCell();
                List<DtoCell> beforeUpdateCells = new ArrayList<>();
                for(CellLocation cellLocation : newValueLocations){
                    DtoCell dtoCell = beforeUpdateDtoSheetCell.getRequestedCell(cellLocation.getCellId());
                    beforeUpdateCells.add(dtoCell);
                }

                sheetManager.updateReplacedCells(newValue, newValueLocations);

                DtoSheetCell afterUpdateDtoSheetCell = sheetManager.getSheetCell();
                List<DtoCell> afterUpdateCells = new ArrayList<>();
                for(CellLocation cellLocation : newValueLocations){
                    DtoCell dtoCell = afterUpdateDtoSheetCell.getRequestedCell(cellLocation.getCellId());
                    afterUpdateCells.add(dtoCell);
                }


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

        engine.getVersionToCellInfo().put(versionNumber, new UpdateCellInfo(oldActualValue,
                newActualValue, oldOriginalValue,
                newOriginalValue, versionNumber, username, newValueLocation, true));



        //        List<UpdateCellInfo> updateCellInfosList = new ArrayList<>();
//
//        for (int i = 0; i < afterUpdateCells.size(); i++) {
//            DtoCell afterUpdateCell = afterUpdateCells.get(i);
//            DtoCell beforeUpdateCell = (beforeUpdateCells != null && i < beforeUpdateCells.size()) ? beforeUpdateCells.get(i) : null;
//
//            String newActualValue = afterUpdateCell.getEffectiveValue().getValue().toString();
//            String newOriginalValue = afterUpdateCell.getOriginalValue();
//            int versionNumber = afterUpdateCell.getLatestVersion();
//            CellLocation location = afterUpdateCell.getLocation();
//
//            String oldActualValue = "";
//            String oldOriginalValue = "";
//
//            if (beforeUpdateCell != null) {
//                oldActualValue = beforeUpdateCell.getEffectiveValue().getValue().toString();
//                oldOriginalValue = beforeUpdateCell.getOriginalValue();
//            }
//
//            updateCellInfosList.add(new UpdateCellInfo(
//                    oldActualValue,
//                    newActualValue,
//                    oldOriginalValue,
//                    newOriginalValue,
//                    versionNumber,
//                    username,
//                    location
//            ));
//
//            engine.getVersionToCellInfo().put(version, updateCellInfosList);
//
//        }

    }

}
