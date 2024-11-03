package chat.servlets.sheet.cell;

import chat.utilities.SessionUtils;
import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import dto.small_parts.CellLocationFactory;
import dto.small_parts.UpdateCellInfo;
import engine.core_parts.api.SheetManager;

import engine.Engine;
import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import engine.utilities.exception.CycleDetectedException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UpdateCellServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String cellValue = request.getParameter("newValue");
        char columnOfCell = request.getParameter("colLocation").charAt(0);
        String row = request.getParameter("rowLocation");

        Engine engine = ServletUtils.getEngineManager(getServletContext());


        // Synchronize on the engine or the sheet manager
        synchronized (engine) {

            String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
            String userName = SessionUtils.getUsername(request);
            SheetManager sheetManager = engine.getSheetCell(sheetName);

            try {
                DtoSheetCell beforeUpdateDtoSheetCell = sheetManager.getSheetCell();
                DtoCell beforeUpdateCell = beforeUpdateDtoSheetCell.getRequestedCell(columnOfCell + row);

                sheetManager.updateCell(cellValue, columnOfCell, row);

                DtoSheetCell afterUpdateDtoSheetCell = sheetManager.getSheetCell();
                DtoCell afterUpdateCell = afterUpdateDtoSheetCell.getRequestedCell(columnOfCell + row);

                engine.updateUsersInCells(beforeUpdateDtoSheetCell, afterUpdateDtoSheetCell, userName);

                updateVersionToCellInfo(beforeUpdateCell, afterUpdateCell, sheetName,userName, engine);


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

    private void updateVersionToCellInfo(DtoCell beforeUpdateCell, DtoCell afterUpdateCell,String sheetName, String userName, Engine engine) {

        String newActualValue = afterUpdateCell.getEffectiveValue().getValue().toString();
        String newOriginalValue = afterUpdateCell.getOriginalValue();
        int versionNumber = afterUpdateCell.getLatestVersion();
        CellLocation location = afterUpdateCell.getLocation();

        Set<CellLocation> newValueLocation = new HashSet<>();
        newValueLocation.add(location);

        String oldActualValue = "";
        String oldOriginalValue = "";

        if(beforeUpdateCell != null){
            oldActualValue = beforeUpdateCell.getEffectiveValue().getValue().toString();
            oldOriginalValue = beforeUpdateCell.getOriginalValue();
        }

        engine.getVersionToCellInfo(sheetName).put(versionNumber, new UpdateCellInfo(oldActualValue,
                newActualValue, oldOriginalValue,
                newOriginalValue, versionNumber, userName, newValueLocation));

    }
}
