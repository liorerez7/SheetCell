package chat.servlets.SheetComponentsServlets.ActionLineServlet;

import CoreParts.api.SheetManager;

import DtoComponents.DtoCell;
import EngineManager.Engine;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class GetAndUpdateCellServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String cellValue = request.getParameter("newValue");
        char columnOfCell = request.getParameter("colLocation").charAt(0);
        String row = request.getParameter("rowLocation");

        Engine engine = ServletUtils.getEngineManager(getServletContext());
        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = engine.getSheetCell(sheetName);
        //SheetManager sheetManager = ServletUtils.getEngine(getServletContext());

        try{
            sheetManager.updateCell(cellValue, columnOfCell, row);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMessage = ServletUtils.extractErrorMessage(e);
            String errorMessageAsJson = Constants.GSON_INSTANCE.toJson(errorMessage);
            response.getWriter().write(errorMessageAsJson);
            response.getWriter().flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        Engine engine = ServletUtils.getEngineManager(getServletContext());
        String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);
        SheetManager sheetManager = engine.getSheetCell(sheetName);
        //SheetManager sheetManager = ServletUtils.getEngine(getServletContext());
        String cellLocation = request.getParameter("cellLocation");

        DtoCell dtoCell = sheetManager.getRequestedCell(cellLocation);
        String dtoCellAsJson = Constants.GSON_INSTANCE.toJson(dtoCell);
        PrintWriter out = response.getWriter();
        out.print(dtoCellAsJson);
        out.flush();
    }
}
