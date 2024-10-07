package chat.servlets;

import CoreParts.api.Engine;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Engine userEngine = new EngineImpl();  // Initialize the engine
        context.setAttribute("engine", userEngine);  // Store in ServletContext
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Clean up resources here if needed
    }
}
