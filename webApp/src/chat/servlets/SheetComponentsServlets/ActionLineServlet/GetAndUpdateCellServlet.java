package chat.servlets.SheetComponentsServlets.ActionLineServlet;

import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
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

        Engine engine = ServletUtils.getEngine(getServletContext());

        try{
            engine.updateCell(cellValue, columnOfCell, row);
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getOutputStream().print("Error: " + e.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        Engine engine = ServletUtils.getEngine(getServletContext());
        String cellLocation = request.getParameter("cellLocation");

        DtoCell dtoCell = engine.getRequestedCell(cellLocation);
        String dtoCellAsJson = Constants.GSON_INSTANCE.toJson(dtoCell);
        PrintWriter out = response.getWriter();
        out.print(dtoCellAsJson);
        out.flush();
    }
}
