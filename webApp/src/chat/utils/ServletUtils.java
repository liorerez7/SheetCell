package chat.utils;


import CoreParts.api.SheetManager;
import EngineManager.Engine;
import jakarta.servlet.ServletContext;
import loginPage.users.PermissionManager;
import loginPage.users.UserManager;

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
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static SheetManager getEngine(ServletContext servletContext) {
		return (SheetManager) servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME);
	}

	public static Engine getEngineManager(ServletContext servletContext) {
		return (Engine) servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME);
	}

	public static PermissionManager getPermissionManager(ServletContext servletContext) {
		return (PermissionManager) servletContext.getAttribute("permissionManager");
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
