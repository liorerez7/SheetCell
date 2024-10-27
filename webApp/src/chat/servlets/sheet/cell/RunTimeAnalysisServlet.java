package chat.servlets.sheet.cell;

import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import chat.utilities.SessionUtils;
import dto.components.DtoSheetCell;
import engine.Engine;
import engine.core_parts.api.SheetManager;
import engine.core_parts.impl.SheetCellImp;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class RunTimeAnalysisServlet extends HttpServlet {

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

                String cellValue = request.getParameter("newValue");
                char columnOfCell = request.getParameter("colLocation").charAt(0);
                String row = request.getParameter("rowLocation");
                int versionNumber = Integer.parseInt(request.getParameter("versionNumber"));

                Engine engine = ServletUtils.getEngineManager(getServletContext());
                String sheetName = (String) request.getSession(false).getAttribute(Constants.SHEET_NAME);

                synchronized (engine) {

                        try{
                                SheetCellImp runTimeSheetCell = engine.createSheetCellOnlyForRunTime(sheetName, versionNumber);
                                SheetManager sheetManager = engine.getSheetCell(sheetName);

                                sheetManager.saveCurrentSheetCellState();

                                sheetManager.updateCell(cellValue, columnOfCell, row);

                                DtoSheetCell dtoSheetCell = sheetManager.getSheetCell();
                                PrintWriter out = response.getWriter();
                                String jsonResponse = Constants.GSON_INSTANCE.toJson(dtoSheetCell);
                                out.print(jsonResponse);
                                out.flush();

                                sheetManager.restoreSheetCellState();

                                response.setStatus(HttpServletResponse.SC_OK);

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
