package CoreParts.api.controller;


public interface Controller {
    void handleRequest(int commandId);
    void updateView();
}
