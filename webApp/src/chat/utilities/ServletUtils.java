package chat.utilities;


import engine.core_parts.api.SheetManager;
import engine.Engine;
import jakarta.servlet.ServletContext;
import engine.login.users.PermissionManager;
import engine.login.users.UserManager;

public class ServletUtils {

	public static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	public static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
	public static final String ENGINE_ATTRIBUTE_NAME = "engine";
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
//		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static Engine getEngineManager(ServletContext servletContext) {
		return (Engine) servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME);
	}

	public static PermissionManager getPermissionManager(ServletContext servletContext) {
		Engine e = getEngineManager(servletContext);
		return e.getPermissionManager();
//		return (PermissionManager) servletContext.getAttribute("permissionManager");
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

}
