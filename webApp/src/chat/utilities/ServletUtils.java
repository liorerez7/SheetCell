package chat.utilities;

import engine.Engine;
import engine.core_parts.api.SheetManager;
import engine.dashboard.chat.ChatManager;
import engine.login.users.SheetInfosManager;
import jakarta.servlet.ServletContext;
import engine.login.users.PermissionManager;
import engine.login.users.UserManager;
import jakarta.servlet.http.HttpServletRequest;

import static chat.utilities.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	public static final String ENGINE_MANAGER_ATTRIBUTE_NAME = "engineManager";

	public static UserManager getUserManager(ServletContext servletContext) {
			Engine e = getEngineManager(servletContext);
			return e.getUserManager();
	}

	public static Engine getEngineManager(ServletContext servletContext) {
		return (Engine) servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME);
	}

	public static SheetManager getSheetManager(ServletContext servletContext, String sheetName) {
		Engine e = (Engine) servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME);
		return e.getSheetCell(sheetName);
	}

	public static SheetInfosManager getSheetInfosManager(ServletContext servletContext) {
		Engine e = getEngineManager(servletContext);
		return e.getSheetInfosManager();
	}

	public static PermissionManager getPermissionManager(ServletContext servletContext) {
		Engine e = getEngineManager(servletContext);
		return e.getPermissionManager();
	}

	public static String extractErrorMessage(Exception e) {
		String message = e.getMessage();
		if (message == null) {
			return "An error occurred.";
		}
		// Remove the prefix if it contains "java.lang.Exception: "
		if (message.contains(":")) {
			// Split on the first occurrence of ":" and return the trimmed part after it
			return message.substring(message.indexOf(":") + 1).trim();
		}
		return message;
	}

	public static ChatManager getChatManager(ServletContext servletContext) {
		Engine e = getEngineManager(servletContext);
		return e.getChatManager();
	}

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}
}
