package controller.popup.graph;

import controller.popup.PopUpWindowsManager;
import dto.components.DtoSheetCell;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class ConsolidatedGraphPopup {

    private Stage popupStage;
    private Scene initialScene;  // Store the initial scene
    private boolean isChartGraph;
    private DtoSheetCell dtoSheetCell;
    private Map<Character, List<String>> selectedValues;
    private Map<Character, Set<String>> stringsInChosenColumn;
    private char xAxis, yAxis;
    private String xTitle, yTitle;
    private List<Character> currentAvailableColumns;
    private List<String> xAxisListAsString, selectedXValues, selectedYValues;
    private List<Double> yAxisList;

    private VBox mainLayout;
    private Button submitButton, generateButton, backButton;
    private ComboBox<Character> xColumnComboBox, yColumnComboBox;
    private TextField xTitleField, yTitleField, graphTitleField;
    private ListView<String> xListView, yListView, selectedPairsListView;
    private GridPane pairSelectionPane, graphDetailsPane;
    private PopUpWindowsManager popUpWindowsManager;

    public ConsolidatedGraphPopup(boolean isChartGraph, DtoSheetCell dtoSheetCell, PopUpWindowsManager popUpWindowsManager) {
        this.isChartGraph = isChartGraph;
        this.dtoSheetCell = dtoSheetCell;
        this.popUpWindowsManager = popUpWindowsManager;
        this.selectedValues = new HashMap<>();
        this.currentAvailableColumns = new ArrayList<>();
        this.selectedXValues = new ArrayList<>();
        this.selectedYValues = new ArrayList<>();

        // Populate currentAvailableColumns based on the number of columns in dtoSheetCell
        for (int i = 0; i < dtoSheetCell.getNumberOfColumns(); i++) {
            currentAvailableColumns.add((char) ('A' + i));
        }

        initializeStage();
    }

    public void show() {
        popupStage.showAndWait();
    }

    private void initializeStage() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Consolidated Graph Setup");

        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(createColumnSelectionPanel(), createGraphDetailsPanel());

        ScrollPane scrollPane = new ScrollPane(mainLayout);  // Wrap mainLayout in ScrollPane
        scrollPane.setFitToWidth(true);  // Ensures scroll pane expands to fit width

        initialScene = new Scene(scrollPane, 600, 700);
        initialScene.getStylesheets().add(getClass().getResource("graphPopupCss.css").toExternalForm());
        popupStage.setScene(initialScene);
    }

    private GridPane createColumnSelectionPanel() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 10;");

        xColumnComboBox = new ComboBox<>();
        yColumnComboBox = new ComboBox<>();
        xColumnComboBox.getItems().addAll(currentAvailableColumns);
        yColumnComboBox.getItems().addAll(currentAvailableColumns);

        submitButton = new Button("Next");
        submitButton.setDisable(true);  // Initially disable the button
        submitButton.setOnAction(e -> handleInitialSubmission());

        // Add listeners to enable the button when both ComboBoxes have values
        xColumnComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateSubmitButtonState());
        yColumnComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateSubmitButtonState());

        gridPane.add(new Label("Select X-Axis Column:"), 0, 0);
        gridPane.add(xColumnComboBox, 1, 0);
        gridPane.add(new Label("Select Y-Axis Column:"), 0, 1);
        gridPane.add(yColumnComboBox, 1, 1);
        gridPane.add(submitButton, 1, 2);

        return gridPane;
    }

    // Method to update the submit button state based on ComboBox selections
    private void updateSubmitButtonState() {
        submitButton.setDisable(xColumnComboBox.getValue() == null || yColumnComboBox.getValue() == null);
    }

    private void handleInitialSubmission() {
        if (xColumnComboBox.getValue() != null && yColumnComboBox.getValue() != null) {
            xAxis = xColumnComboBox.getValue();
            yAxis = yColumnComboBox.getValue();
            stringsInChosenColumn = dtoSheetCell.getUniqueStringsInColumn(List.of(xAxis, yAxis), isChartGraph);

            submitButton.setDisable(true);
            xColumnComboBox.setDisable(true);
            yColumnComboBox.setDisable(true);

            pairSelectionPane = createPairSelectionPanel();
            mainLayout.getChildren().add(1, pairSelectionPane);
        } else {
            showErrorAlert("Please select both X and Y axis columns.");
        }
    }

    private GridPane createPairSelectionPanel() {
        GridPane pairPane = new GridPane();
        pairPane.setHgap(10);
        pairPane.setVgap(10);
        pairPane.setPadding(new Insets(10));
        pairPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 10;");

        xListView = new ListView<>();
        yListView = new ListView<>();
        xListView.setPrefHeight(80);
        yListView.setPrefHeight(80);
        xListView.getItems().addAll(stringsInChosenColumn.getOrDefault(xAxis, new HashSet<>()));
        yListView.getItems().addAll(stringsInChosenColumn.getOrDefault(yAxis, new HashSet<>()));

        selectedPairsListView = new ListView<>();
        selectedPairsListView.setPrefHeight(100);

        Button addPairButton = new Button("Add Pair");
        addPairButton.setOnAction(e -> addSelectedPair(xListView.getSelectionModel().getSelectedItem(),
                yListView.getSelectionModel().getSelectedItem()));

        Button removePairButton = new Button("Remove Selected Pair");
        removePairButton.setOnAction(e -> removeSelectedPair());

        // Middle-section "Back" button
        Button middleBackButton = new Button("Back");
        middleBackButton.setOnAction(e -> handleMiddleSectionBackAction());

        pairPane.add(new Label("Select pairs by choosing values:"), 0, 0, 2, 1);
        pairPane.add(xListView, 0, 1);
        pairPane.add(yListView, 1, 1);
        pairPane.add(addPairButton, 0, 2);
        pairPane.add(removePairButton, 1, 2);
        pairPane.add(middleBackButton, 0, 3, 2, 1); // Add Back button to middle section
        pairPane.add(new Label("Selected Pairs:"), 0, 4, 2, 1);
        pairPane.add(selectedPairsListView, 0, 5, 2, 1);

        return pairPane;
    }

    private void handleMiddleSectionBackAction() {
        // Clear all selected pairs
        selectedPairsListView.getItems().clear();
        selectedXValues.clear();
        selectedYValues.clear();

        // Re-enable column selection
        submitButton.setDisable(true);
        xColumnComboBox.setDisable(false);
        yColumnComboBox.setDisable(false);

        // Hide and disable the graph details pane
        graphDetailsPane.setVisible(false);
        graphDetailsPane.setDisable(true);

        // Remove the pair selection pane from the main layout
        mainLayout.getChildren().remove(pairSelectionPane);
    }


    private void addSelectedPair(String xValue, String yValue) {
        if (xValue != null && yValue != null) {
            String pair = "(" + xValue + ", " + yValue + ")";
            selectedPairsListView.getItems().add(pair);
            selectedXValues.add(xValue);
            selectedYValues.add(yValue);

            xListView.getItems().remove(xValue);
            yListView.getItems().remove(yValue);

            if (selectedPairsListView.getItems().size() > 0) {
                graphDetailsPane.setVisible(true);
                graphDetailsPane.setDisable(false);
                checkGenerateButtonState();
            }
        }
    }

    private void removeSelectedPair() {
        String selectedPair = selectedPairsListView.getSelectionModel().getSelectedItem();
        if (selectedPair != null) {
            selectedPairsListView.getItems().remove(selectedPair);

            String[] values = selectedPair.replace("(", "").replace(")", "").split(", ");
            String xValue = values[0];
            String yValue = values[1];

            xListView.getItems().add(xValue);
            yListView.getItems().add(yValue);

            selectedXValues.remove(xValue);
            selectedYValues.remove(yValue);

            if (selectedPairsListView.getItems().isEmpty()) {
                graphDetailsPane.setVisible(false);
                graphDetailsPane.setDisable(true);
                generateButton.setDisable(true);
            }
        }
    }

    private GridPane createGraphDetailsPanel() {
        graphDetailsPane = new GridPane();
        graphDetailsPane.setHgap(10);
        graphDetailsPane.setVgap(10);
        graphDetailsPane.setPadding(new Insets(10));
        graphDetailsPane.setDisable(true);
        graphDetailsPane.setVisible(false);
        graphDetailsPane.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");

        graphTitleField = new TextField();
        xTitleField = new TextField();
        yTitleField = new TextField();
        graphTitleField.setPromptText("Graph Title");
        xTitleField.setPromptText("X-Axis Label");
        yTitleField.setPromptText("Y-Axis Label");

        graphTitleField.textProperty().addListener((obs, oldVal, newVal) -> checkGenerateButtonState());
        xTitleField.textProperty().addListener((obs, oldVal, newVal) -> checkGenerateButtonState());
        yTitleField.textProperty().addListener((obs, oldVal, newVal) -> checkGenerateButtonState());

        generateButton = new Button("Generate Graph");
        generateButton.setDisable(true);
        generateButton.setOnAction(e -> showGraphView());

        graphDetailsPane.add(new Label("Graph Title:"), 0, 0);
        graphDetailsPane.add(graphTitleField, 1, 0);
        graphDetailsPane.add(new Label("X-Axis Label:"), 0, 1);
        graphDetailsPane.add(xTitleField, 1, 1);
        graphDetailsPane.add(new Label("Y-Axis Label:"), 0, 2);
        graphDetailsPane.add(yTitleField, 1, 2);
        graphDetailsPane.add(generateButton, 1, 3);

        return graphDetailsPane;
    }

    private void checkGenerateButtonState() {
        generateButton.setDisable(graphTitleField.getText().isEmpty() ||
                xTitleField.getText().isEmpty() ||
                yTitleField.getText().isEmpty());
    }

    private void showGraphView() {
        xAxisListAsString = selectedXValues;
        yAxisList = selectedYValues.stream().map(Double::parseDouble).collect(Collectors.toList());

        Chart chart = isChartGraph ? createBarChart() : createLineChart();

        backButton = new Button("Back");
        backButton.setOnAction(e -> popupStage.setScene(initialScene));
        backButton.getStyleClass().add("button");

        Label statsLabel = new Label(generateGraphStats());
        statsLabel.setId("statsLabel");

        VBox graphLayout = new VBox(10, chart, statsLabel, backButton);
        graphLayout.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(graphLayout);  // Wrap graphLayout in ScrollPane
        scrollPane.setFitToWidth(true);

        Scene graphScene = new Scene(scrollPane, 800, 600);
        graphScene.getStylesheets().add(getClass().getResource("graphPopupCss.css").toExternalForm());
        popupStage.setScene(graphScene);
    }

    private String generateGraphStats() {
        if (isChartGraph) {
            double maxY = yAxisList.stream().max(Double::compare).orElse(0.0);
            double minY = yAxisList.stream().min(Double::compare).orElse(0.0);
            double meanY = yAxisList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double sumY = yAxisList.stream().mapToDouble(Double::doubleValue).sum();
            double rangeY = maxY - minY;

            return "Bar Chart Statistics:\n" +
                    "Highest Value: " + maxY + "\n" +
                    "Lowest Value: " + minY + "\n" +
                    "Mean Value: " + String.format("%.2f", meanY) + "\n" +
                    "Total Sum: " + String.format("%.2f", sumY) + "\n" +
                    "Range (Max - Min): " + String.format("%.2f", rangeY);
        } else {
            double slope = calculateSlope();
            double intercept = calculateIntercept(slope);
            double correlationCoefficient = calculateCorrelationCoefficient();
            double meanY = yAxisList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double yVariance = calculateVariance(yAxisList, meanY);

            // Format slope and intercept
            String formattedSlope;
            if (Math.abs(slope) == 1) {
                formattedSlope = slope == -1 ? "-" : "";  // Use "-" for -1, nothing for 1
            } else {
                formattedSlope = slope % 1 == 0 ? String.format("%.0f", slope) : String.format("%.2f", slope);
            }

            String formattedIntercept = intercept < 0
                    ? String.format("%.2f", intercept)
                    : "+ " + String.format("%.2f", intercept);

            return "Line Chart Statistics:\n" +
                    "Equation: y = " + formattedSlope + "x " + formattedIntercept + "\n" +
                    "Correlation Coefficient: " + String.format("%.2f", correlationCoefficient) + "\n" +
                    "Mean of Y: " + String.format("%.2f", meanY) + "\n" +
                    "Variance of Y: " + String.format("%.2f", yVariance) + "\n" +
                    "Slope (Rate of Change): " + formattedSlope;
        }
    }

    private double calculateCorrelationCoefficient() {
        int n = xAxisListAsString.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (int i = 0; i < n; i++) {
            double x = Double.parseDouble(xAxisListAsString.get(i));
            double y = yAxisList.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
        return (denominator != 0) ? numerator / denominator : 0.0;
    }

    private double calculateVariance(List<Double> values, double mean) {
        double variance = 0;
        for (double value : values) {
            variance += Math.pow(value - mean, 2);
        }
        return variance / values.size();
    }

    private double calculateSlope() {
        int n = xAxisListAsString.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = Double.parseDouble(xAxisListAsString.get(i));
            double y = yAxisList.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    }

    private double calculateIntercept(double slope) {
        double sumX = xAxisListAsString.stream().mapToDouble(Double::parseDouble).sum();
        double sumY = yAxisList.stream().mapToDouble(Double::doubleValue).sum();
        return (sumY - slope * sumX) / xAxisListAsString.size();
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xTitle);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yTitle);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(graphTitleField.getText());
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Data Series");

        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PURPLE};

        for (int i = 0; i < xAxisListAsString.size(); i++) {
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(xAxisListAsString.get(i), yAxisList.get(i));
            final int index = i;
            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + toHexString(colors[index % colors.length]) + ";");
                }
            });
            series.getData().add(dataPoint);
        }
        barChart.getData().add(series);
        return barChart;
    }

    private LineChart<Number, Number> createLineChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(xTitle);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yTitle);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(graphTitleField.getText());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Series");

        for (int i = 0; i < yAxisList.size(); i++) {
            series.getData().add(new XYChart.Data<>(Double.parseDouble(xAxisListAsString.get(i)), yAxisList.get(i)));
        }
        lineChart.getData().add(series);
        return lineChart;
    }

    private String toHexString(Color color) {
        return String.format("#%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
