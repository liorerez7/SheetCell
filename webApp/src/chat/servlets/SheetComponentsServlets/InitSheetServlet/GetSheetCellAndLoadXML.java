package chat.servlets.SheetComponentsServlets.InitSheetServlet;

import CoreParts.api.Engine;
import DtoComponents.DtoSheetCell;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class GetSheetCellAndLoadXML extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String xmlAddress = request.getParameter("xmlAddress");

        Engine engine = ServletUtils.getEngine(getServletContext());

        try{
            engine.readSheetCellFromXML(xmlAddress);
            String sheetName = engine.getSheetCell().getSheetName();
            String sheetNameAsJson = Constants.GSON_INSTANCE.toJson(sheetName);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(sheetNameAsJson);
            response.getWriter().flush();
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_CONFLICT); // Set status to 409 explicitly
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("Error: " + e.getMessage()); // Use getWriter() instead of getOutputStream() for text
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String versionNumber = request.getParameter("versionNumber");

        Engine engine = ServletUtils.getEngine(getServletContext());

        if(versionNumber == null){

            DtoSheetCell dtoSheetCell = engine.getSheetCell();
            PrintWriter out = response.getWriter();
            String jsonResponse = Constants.GSON_INSTANCE.toJson(dtoSheetCell);
            out.print(jsonResponse);
            out.flush();
        }
        else {
            int version = Integer.parseInt(versionNumber);
            DtoSheetCell dtoSheetCell = engine.getSheetCell(version);
            PrintWriter out = response.getWriter();
            String jsonResponse = Constants.GSON_INSTANCE.toJson(dtoSheetCell);
            out.print(jsonResponse);
            out.flush();
        }
    }
}
