package chat.utilities;


import engine.core_parts.api.SheetManager;
import engine.Engine;
import engine.dashboard.chat.ChatManager;
import jakarta.servlet.ServletContext;
import engine.login.users.PermissionManager;
import engine.login.users.UserManager;
import jakarta.servlet.http.HttpServletRequest;

import static chat.utilities.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	public static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	public static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
	public static final String ENGINE_MANAGER_ATTRIBUTE_NAME = "engineManager";
	public static final String PERMISSION_MANAGER_ATTRIBUTE_NAME = "permissionManager";

	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object chatManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {
		Engine e = getEngineManager(servletContext);
		return e.getUserManager();
	}

	public static Engine getEngineManager(ServletContext servletContext) {
		return (Engine) servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME);
	}

	public static PermissionManager getPermissionManager(ServletContext servletContext) {
		Engine e = getEngineManager(servletContext);
		return e.getPermissionManager();
	}

	// Helper method to extract the error message
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
		synchronized (chatManagerLock) {
			if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
			}
		}
		return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
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
