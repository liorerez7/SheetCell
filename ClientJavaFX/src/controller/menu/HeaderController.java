package controller.menu;

import controller.main.MainController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class HeaderController {

    MainController mainController;

    @FXML private GridPane gridPaneMenu;
    @FXML private ComboBox<Label> columnComboBox;
    @FXML private Button widthMinusButton;
    @FXML private Button widthPlusButton;
    @FXML private ComboBox<Label> rowComboBox;
    @FXML private Button lengthMinusButton;
    @FXML private Button lengthPlusButton;
    @FXML private ComboBox<Label> alignmentComboBox;
    @FXML private ColorPicker textColorPicker;
    @FXML private Button defaultTextButton;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private Button defaultBackgroundTextButton;
    @FXML private ComboBox<Label> ThemeColorComboBox;
    @FXML private Button backDashBoard;
    @FXML private Label welcomeLabel;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setupBindings();
    }

    private void setupBindings() {
        defaultTextButton.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
        defaultBackgroundTextButton.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
        textColorPicker.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
        backgroundColorPicker.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
        rowComboBox.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        columnComboBox.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        widthPlusButton.disableProperty().bind(mainController.getIsColumnSelectedProperty().not());
        widthMinusButton.disableProperty().bind(mainController.getIsColumnSelectedProperty().not());
        lengthPlusButton.disableProperty().bind(mainController.getIsRowSelectedProperty().not());
        lengthMinusButton.disableProperty().bind(mainController.getIsRowSelectedProperty().not());
        alignmentComboBox.disableProperty().bind(mainController.getIsColumnSelectedProperty().not());
        bindColorPickersReset(mainController.getColorProperty());
    }

    @FXML
    public void initialize() {
        initializeAlignmentComboBox();
        initializeComboBoxes();
        setupColumnComboBox();
        setupRowComboBox();
    }

    private void initializeComboBoxes() {
        initializeComboBox(columnComboBox, "Columns");
        initializeComboBox(rowComboBox, "Rows");
        initializeComboBox(alignmentComboBox, "Alignment");

        setComboBoxHeaderTextColor(columnComboBox, Color.WHITE);
        setComboBoxHeaderTextColor(rowComboBox, Color.WHITE);
        setComboBoxHeaderTextColor(alignmentComboBox, Color.WHITE);
    }

    private void initializeComboBox(ComboBox<Label> comboBox, String defaultText) {
        comboBox.setCellFactory(cb -> new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getText());
                setTextFill(Color.BLACK);  // Set dropdown item text color
            }
        });

        comboBox.setButtonCell(new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? defaultText : item.getText());
                // Avoid setting white text if the background is light
                setTextFill(Color.BLACK);  // Ensures visibility of default text
            }
        });

        comboBox.setPromptText(defaultText);  // Set the prompt text explicitly
    }

    private void setComboBoxHeaderTextColor(ComboBox<Label> comboBox, Color color) {
        TextField textField = (TextField) comboBox.lookup(".text-field");
        if (textField != null) {
            textField.setStyle("-fx-text-fill: " + toRgbString(color) + ";");
        }
    }

    private void initializeAlignmentComboBox() {
        alignmentComboBox.getItems().addAll(
                new Label("CENTER"),
                new Label("LEFT"),
                new Label("RIGHT")
        );


        // Ensure the prompt text is set correctly
        alignmentComboBox.setPromptText("Alignment");
        setComboBoxCellFactory(alignmentComboBox, "Alignment");

        // Listen for value changes in the ComboBox
        alignmentComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                handleTextAlignment(newValue.getText().toLowerCase());
            }
        });
    }

    @FXML
    void handleSizeChangeClicked(ActionEvent event) {
        String selectedCol = getSelectedComboBoxText(columnComboBox);
        String selectedRow = getSelectedComboBoxText(rowComboBox);
        Object source = event.getSource();

        if (source == lengthMinusButton) {
            mainController.adjustCellSize(-1, selectedRow);
        } else if (source == lengthPlusButton) {
            mainController.adjustCellSize(1, selectedRow);
        } else if (source == widthMinusButton) {
            mainController.adjustCellSize(-1, selectedCol);
        } else if (source == widthPlusButton) {
            mainController.adjustCellSize(1, selectedCol);
        }
    }

    private String getSelectedComboBoxText(ComboBox<Label> comboBox) {
        Label selectedLabel = comboBox.getSelectionModel().getSelectedItem();
        return selectedLabel != null ? selectedLabel.getText() : null;
    }

    @FXML
    void textColorClicked(ActionEvent event) {
        mainController.changeTextColor(textColorPicker.getValue(), mainController.getCellLocationProperty().getValue());
    }

    @FXML
    void backgroundTextColorClicked(ActionEvent event) {
        mainController.changeBackgroundColor(backgroundColorPicker.getValue(), mainController.getCellLocationProperty().getValue());
    }

    @FXML
    void defaultTextClicked(ActionEvent event) {
        mainController.changeTextColor(Color.BLACK, mainController.getCellLocationProperty().getValue());
    }

    @FXML
    void defaultBackgroundTextClicked(ActionEvent event) {
        mainController.changeBackgroundColor(Color.WHITE, mainController.getCellLocationProperty().getValue());
    }

    private void setupColumnComboBox() {
        setComboBoxCellFactory(columnComboBox, "Columns");
        columnComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                handleColumnSelection();
            }
        });
    }

    private void setupRowComboBox() {
        setComboBoxCellFactory(rowComboBox, "Rows");
        rowComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                handleRowSelection();
            }
        });
    }

    private void handleColumnSelection() {
        mainController.ColumnSelected();
    }

    private void handleRowSelection() {
        mainController.RowSelected();
    }

    private void setComboBoxCellFactory(ComboBox<Label> comboBox, String defaultText) {
        comboBox.setCellFactory(cb -> new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getText());
            }
        });
        comboBox.setButtonCell(new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? defaultText : item.getText());
            }
        });
        comboBox.setPromptText(defaultText);
    }

    private String toRgbString(Color color) {
        return String.format("rgb(%d,%d,%d)", (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void loadAllColData(int numberOfCols) {
        columnComboBox.getItems().clear();  // Clear previous items
        for (int i = 0; i < numberOfCols; i++) {
            String columnLetter = String.valueOf((char) ('A' + i));  // Convert to letter A, B, C, etc.
            Label columnLabel = new Label(columnLetter);  // Create a new Label for each column
            columnComboBox.getItems().add(columnLabel);  // Add each label to ComboBox
        }
        columnComboBox.getSelectionModel().clearSelection();  // Reset selection
        columnComboBox.setPromptText("Columns");  // Set default text after loading
    }

    public void loadAllRowData(int numberOfRows) {
        rowComboBox.getItems().clear();  // Clear previous items
        for (int i = 1; i <= numberOfRows; i++) {
            Label rowLabel = new Label(String.valueOf(i));  // Create a new Label for each row number
            rowComboBox.getItems().add(rowLabel);  // Add each label to ComboBox
        }
        rowComboBox.getSelectionModel().clearSelection();  // Reset selection
        rowComboBox.setPromptText("Rows");  // Set default text after loading
    }

    public void resetComboBox() {

        columnComboBox.getSelectionModel().clearSelection(); // Clear the current selection
        columnComboBox.setPromptText("Columns"); // Set default text after clearing selection
        rowComboBox.getSelectionModel().clearSelection(); // Clear the current selection
        rowComboBox.setPromptText("Rows"); // Set default text after clearing selection
        alignmentComboBox.getSelectionModel().clearSelection(); // Clear the current selection
        alignmentComboBox.setPromptText("Alignment"); // Set default prompt text
    }

    @FXML
    private void handleTextAlignment(String alignment) {
        Label selectedColumnLabel = columnComboBox.getSelectionModel().getSelectedItem();
        mainController.changeTextAlignment(alignment, selectedColumnLabel);
    }

    @FXML
    void backDashBoardClicked(ActionEvent event) {
        String username = mainController.getUserName();
        mainController.showDashBoardScreen(username);
    }

    private void bindColorPickersReset(BooleanProperty resetColorPickersProperty) {
        // Listen to the resetColorPickersProperty and reset the ColorPickers when it's true
        resetColorPickersProperty.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                resetColorPickersToDefault();
                // Reset the property back to false after resetting
                resetColorPickersProperty.set(false);
            }
        });
    }

    private void resetColorPickersToDefault() {
        textColorPicker.setValue(Color.WHITE);
        backgroundColorPicker.setValue(Color.WHITE);
    }

    public void setupName(String name) {
        welcomeLabel.setText("Welcome " + name + "!");
    }
}
