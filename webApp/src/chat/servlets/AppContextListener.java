package chat.servlets;

import engine.Engine;
import chat.utilities.ServletUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Engine engine = new Engine();  // Initialize the engine
        context.setAttribute(ServletUtils.ENGINE_MANAGER_ATTRIBUTE_NAME, engine);  // Store in ServletContext
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
