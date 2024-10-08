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
