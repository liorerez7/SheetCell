package Controller.Customize;

import Controller.Main.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.awt.*;


public class CustomizeController {

    MainController mainController;

    @FXML
    private VBox customize;

    @FXML
    private ComboBox<Label> columnComboBox;  // Using Label for columns
    @FXML
    private ComboBox<Label> rowComboBox;     // Using Label for rows

    @FXML
    private ColorPicker textColorPicker;

    @FXML
    private Button defaultBackgroundTextButton;

    @FXML
    private Button defaultTextButton;

    @FXML
    private ColorPicker backgroundColorPicker;

    @FXML
    private Button widthMinusButton;

    @FXML
    private Button widthPlusButton;

    @FXML
    private Button lengthMinusButton;

    @FXML
    private Button lengthPlusButton;

    @FXML
    private ComboBox<Label> alignmentComboBox; // New ComboBox for text alignment

    @FXML
    private Label cellIdLabel;

    public void setMainController(MainController mainController) {

        this.mainController = mainController;

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
        cellIdLabel.textProperty().bind(mainController.getCellLocationProperty());
    }

    @FXML
    public void initialize() {

        initializeAlignmentComboBox();


        // Set up Column ComboBox (handles labels like A, B, C, etc.)
        columnComboBox.setCellFactory(comboBox -> new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getText());
                }
            }
        });

        columnComboBox.setButtonCell(new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Columns");  // Default text when nothing is selected
                } else {
                    setText(item.getText());
                }
            }
        });

        // Set up Row ComboBox (handles labels for row numbers like 1, 2, 3, etc.)
        rowComboBox.setCellFactory(comboBox -> new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getText());
                }
            }
        });

        rowComboBox.setButtonCell(new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Rows");  // Default text when nothing is selected
                } else {
                    setText(item.getText());
                }
            }
        });


        columnComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                handleColumnSelection();
            }
        });

        rowComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                handleRowSelection();
            }
        });
    }

    private void initializeAlignmentComboBox() {
        // Add alignment options
        alignmentComboBox.getItems().addAll(
                new Label("CENTER"),
                new Label("LEFT"),
                new Label("RIGHT")
        );

        // Set custom cell factory for alignment options in the dropdown
        alignmentComboBox.setCellFactory(comboBox -> new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getText());
                }
            }
        });

        // Set custom button cell to display prompt text or selected option
        alignmentComboBox.setButtonCell(new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Alignment Text");  // Default text when nothing is selected
                } else {
                    setText(item.getText());
                }
            }
        });

        // Set default prompt text
        alignmentComboBox.setPromptText("Alignment Text");

        // Add a listener to handle alignment selection
        alignmentComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                handleTextAlignment(newValue.getText().toLowerCase());
            }
        });
    }

    private void handleColumnSelection() {
        mainController.ColumnSelected();
    }

    private void handleRowSelection() {
        mainController.RowSelected();
    }

    // Method to load all column data dynamically (A, B, C,...)
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

    // Method to load all row data dynamically (1, 2, 3,...)
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
        alignmentComboBox.setPromptText("AlignmentText"); // Set default prompt text
    }

    @FXML
    void handleSizeChangeClicked(ActionEvent event) {

        Label selectedColumnLabel = columnComboBox.getSelectionModel().getSelectedItem();
        Label selectedRowLabel = rowComboBox.getSelectionModel().getSelectedItem();


        String selectedCol = null;
        String selectedRow = null;

        if(selectedColumnLabel != null)
        {
            selectedCol = selectedColumnLabel.getText();

        }
        if(selectedRowLabel != null)
        {
            selectedRow = selectedRowLabel.getText();
        }

        // Identify the source of the event (which button was clicked)
        Object source = event.getSource();

        // Call the respective function in MainController, passing the row and column along with the size adjustment
        if (source == lengthMinusButton) {
            mainController.adjustCellSize(-1,selectedRow);  // Decrease length
        } else if (source == lengthPlusButton) {
            mainController.adjustCellSize(1, selectedRow);   // Increase length
        } else if (source == widthMinusButton) {
            mainController.adjustCellSize(-1, selectedCol);   // Decrease width
        } else if (source == widthPlusButton) {
            mainController.adjustCellSize(1, selectedCol);    // Increase width
        }
    }

    @FXML
    private void handleTextAlignment(String alignment) {
        Label selectedColumnLabel = columnComboBox.getSelectionModel().getSelectedItem();
        mainController.changeTextAlignment(alignment, selectedColumnLabel);
    }

    @FXML
    void defaultBackgroundTextClicked(ActionEvent event) {
        //mainController.setDefaultBackground(Color.WHITE);
        mainController.changeBackgroundColor(Color.WHITE, cellIdLabel.getText());
    }

    @FXML
    void defaultTextClicked(ActionEvent event) {
        mainController.changeTextColor(Color.BLACK, cellIdLabel.getText());
    }

    @FXML
    void backgroundTextColorClicked(ActionEvent event) {

        mainController.changeBackgroundColor(backgroundColorPicker.getValue(), cellIdLabel.getText());
    }

    @FXML
    void textColorClicked(ActionEvent event) {
        mainController.changeTextColor(textColorPicker.getValue(), cellIdLabel.getText());
    }

}


