package chat.servlets.dashboard;


import chat.utilities.Constants;
import chat.utilities.ServletUtils;
import chat.utilities.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import engine.dashboard.chat.ChatManager;
import engine.dashboard.chat.SingleChatEntry;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ChatServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        /*
        verify chat version given from the user is a valid number. if not it is considered an error and nothing is returned back
        Obviously the UI should be ready for such a case and handle it properly
         */
        int chatVersion = ServletUtils.getIntParameter(request, Constants.CHAT_VERSION_PARAMETER);
        if (chatVersion == Constants.INT_PARAMETER_ERROR) {
            return;
        }

        /*
        Synchronizing as minimum as I can to fetch only the relevant information from the chat manager and then only processing and sending this information onward
        Note that the synchronization here is on the ServletContext, and the one that also synchronized on it is the chat servlet when adding new chat lines.
         */
        int chatManagerVersion = 0;
        List<SingleChatEntry> chatEntries;
        synchronized (getServletContext()) {
            chatManagerVersion = chatManager.getVersion();
            chatEntries = chatManager.getChatEntries(chatVersion);
        }

        // log and create the response json string
        ChatAndVersion cav = new ChatAndVersion(chatEntries, chatManagerVersion);
        String jsonResponse = Constants.GSON_INSTANCE.toJson(cav);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

    }

    private static class ChatAndVersion {

        final private List<SingleChatEntry> entries;
        final private int version;

        public ChatAndVersion(List<SingleChatEntry> entries, int version) {
            this.entries = entries;
            this.version = version;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String userChatMsg = request.getParameter(Constants.CHAT_PARAMETER);
        if (userChatMsg != null && !userChatMsg.isEmpty()) {
            synchronized (getServletContext()) {
                chatManager.addChatString(userChatMsg, username);
            }
        }
    }
}