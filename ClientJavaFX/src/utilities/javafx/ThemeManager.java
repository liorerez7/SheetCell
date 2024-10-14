package utilities.javafx;

import controller.customize.CustomizeController;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ThemeManager {
    private ThemeColor currentThemeColor;
    private Pane mainPane;
    private Pane leftCommands;

    public ThemeManager(Pane mainPane, Pane leftCommands) {
        this.mainPane = mainPane;
        this.leftCommands = leftCommands;
        this.currentThemeColor = ThemeColor.CLASSIC; // Default theme
    }

    public void classicDisplayClicked(Runnable... controllers) {
        changeTheme(ThemeColor.CLASSIC, "Background", controllers);
    }

    public void sunBurstDisplayClicked(Runnable... controllers) {
        changeTheme(ThemeColor.SUNBURST, "SunBurstBackground", controllers);
    }

    public void midNightDisplayClicked(Runnable... controllers) {
        changeTheme(ThemeColor.MIDNIGHT, "DarkModeBackground", controllers);
    }

    private void changeTheme(ThemeColor newTheme, String newStyleClass, Runnable... controllers) {

        if (currentThemeColor == newTheme) {
            return;
        }

        removeCurrentThemeStyles();

        mainPane.getStyleClass().add(newStyleClass);
        leftCommands.getStyleClass().add(newStyleClass);

        for (Runnable controller : controllers) {
            controller.run();
        }

        currentThemeColor = newTheme;
    }

    private void removeCurrentThemeStyles() {
        switch (currentThemeColor) {
            case CLASSIC:
                mainPane.getStyleClass().remove("Background");
                leftCommands.getStyleClass().remove("Background");
                break;
            case MIDNIGHT:
                mainPane.getStyleClass().remove("DarkModeBackground");
                leftCommands.getStyleClass().remove("DarkModeBackground");
                break;
            case SUNBURST:
                mainPane.getStyleClass().remove("SunBurstBackground");
                leftCommands.getStyleClass().remove("SunBurstBackground");
                break;
        }
    }

    public void setMainPaneStyleClass(Pane mainPane) {
        this.mainPane = mainPane;
    }

    public void setLeftCommandsStyleClass(Pane leftCommands) {
        this.leftCommands = leftCommands;
    }

    public void keepCurrentTheme(Pane mainPane, Pane leftCommands, CustomizeController customizeController) {

        this.mainPane = mainPane;
        this.leftCommands = leftCommands;

        switch (currentThemeColor) {
            case CLASSIC:
                customizeController.adjustTextButtonColor(Color.WHITE);
                break;
            case MIDNIGHT:
                customizeController.adjustTextButtonColor(Color.WHITE);
                break;
            case SUNBURST:
                customizeController.adjustTextButtonColor(Color.BLACK);
                break;
        }
    }
}