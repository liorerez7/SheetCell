package chat.servlets.SheetComponentsServlets.RangeServlets;

import CoreParts.api.Engine;
import chat.utils.ServletUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DeleteRangeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");

        if (name != null) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            engine.deleteRange(name);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Invalid range name");
        }
    }
}
