package Controller.Grid;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;

public class CustomCellLabel {

    private Label label;
    private Color textColor;
    private Color backgroundColor;
    private Color[] backgroundLayers;
    private Pos alignment;
    private TextAlignment textAlignment;

    private final Color AFFECTED_LAYER_COLOR = Color.LIGHTBLUE; // Special second layer color
    private final Color AFFECTS_TARGET_COLOR = Color.LIGHTGREEN; // Affects target color



    public CustomCellLabel(Label label) {
        this.label = label;
        this.textColor = (Color) label.getTextFill();  // Initialize from the label
        this.backgroundColor = null;                   // You can set default or get from style
        this.backgroundLayers = new Color[0];          // Initialize empty layers
        this.alignment = label.getAlignment();
        this.textAlignment = label.getTextAlignment();
    }

    // Getters and Setters for Text Color
    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        label.setTextFill(textColor);
    }

    // Getters and Setters for Background Color
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    // Add a special second layer (Light Blue)
    public void addAffectedlyLayer() {
        if (backgroundLayers.length == 1) {
            Color[] newLayers = { backgroundLayers[0], AFFECTED_LAYER_COLOR};
            setBackgroundLayers(newLayers);
        } else if (backgroundLayers.length == 0) {
            Color[] newLayers = { backgroundColor, AFFECTED_LAYER_COLOR};
            setBackgroundLayers(newLayers);
        }
    }

    public void addAffectsTargetLayer() {
        if (backgroundLayers.length == 1) {
            Color[] newLayers = { backgroundLayers[0], AFFECTS_TARGET_COLOR};
            setBackgroundLayers(newLayers);
        } else if (backgroundLayers.length == 0) {
            Color[] newLayers = { backgroundColor, AFFECTS_TARGET_COLOR};
            setBackgroundLayers(newLayers);
        }
    }


    // Remove the special layer (Light Blue) if it exists
    public void removeSpecialLayer() {
        if (backgroundLayers.length == 2 && (backgroundLayers[1].equals(AFFECTED_LAYER_COLOR) || backgroundLayers[1].equals(AFFECTS_TARGET_COLOR))) {
            setBackgroundLayers(new Color[] { backgroundLayers[0] }); // Remove second layer
        }
    }


    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.backgroundLayers = new Color[0]; // Reset layers
        updateBackground();
    }

    // Getters and Setters for Background Layers
    public Color[] getBackgroundLayers() {
        return backgroundLayers;
    }

    public void setBackgroundLayers(Color[] backgroundLayers) {
        this.backgroundLayers = backgroundLayers;
        updateBackground();
    }

    // Helper method to update background color with layers
//    private void updateBackground() {
//
//        if (backgroundLayers.length > 0) {
//            StringBuilder style = new StringBuilder("-fx-background-color: ");
//            for (Color layer : backgroundLayers) {
//                style.append(toRgbString(layer)).append(", ");
//            }
//            style.append(toRgbString(backgroundColor)).append(";");
//            label.setStyle(style.toString());
//        } else if (backgroundColor != null) {
//            label.setStyle("-fx-background-color: " + toRgbString(backgroundColor) + ";"
//            +"-fx-border-color: black; " +
//                            "-fx-border-width: 1px; " +
//                            "-fx-padding: 5px; " +
//                            "-fx-wrap-text: false; "
//                    );
//        }
//    }

    // Helper method to update background color with layers
    private void updateBackground() {
        if (backgroundLayers.length > 0) {
            StringBuilder style = new StringBuilder("-fx-background-color: ");
            for (Color layer : backgroundLayers) {
                style.append(toRgbString(layer)).append(", ");
            }
            label.setStyle(style.substring(0, style.length() - 2) + ";" +"-fx-border-color: black; " +
                    "-fx-border-width: 1px; " +
                    "-fx-padding: 5px; " +
                    "-fx-wrap-text: false; ");

        }
        else {
            label.setStyle("-fx-background-color: " + toRgbString(backgroundColor) + ";" +"-fx-border-color: black; " +
                           "-fx-border-width: 1px; " +
                            "-fx-padding: 5px; " +
                           "-fx-wrap-text: false; "
            );
        }
    }


    // Convert Color to CSS RGB string
    private String toRgbString(Color color) {
        return "rgba(" +
                (int) (color.getRed() * 255) + "," +
                (int) (color.getGreen() * 255) + "," +
                (int) (color.getBlue() * 255) + "," +
                color.getOpacity() + ")";
    }

    // Getters and Setters for Alignment
    public Pos getAlignment() {
        return alignment;
    }

    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
        label.setAlignment(alignment);
    }

    // Getters and Setters for Text Alignment
    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        label.setTextAlignment(textAlignment);
    }

    // Get the label itself
    public Label getLabel() {
        return label;
    }

    // Apply default styles similar to the CSS .cell-label
    public void applyDefaultStyles() {
        backgroundColor = Color.WHITE;
        label.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 1px; " +
                        "-fx-padding: 5px; " +
                        "-fx-wrap-text: false; "
        );
    }
}





