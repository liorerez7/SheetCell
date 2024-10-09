package chat.servlets;

import CoreParts.api.SheetManager;
import CoreParts.impl.InnerSystemComponents.SheetManagerImpl;
import EngineManager.Engine;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        SheetManager userSheetManager = new SheetManagerImpl();  // Initialize the engine
        context.setAttribute("engine", userSheetManager);  // Store in ServletContext
        Engine engine = new Engine();  // Initialize the engine
        context.setAttribute("engineManager", engine);  // Store in ServletContext
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Clean up resources here if needed
    }
}
