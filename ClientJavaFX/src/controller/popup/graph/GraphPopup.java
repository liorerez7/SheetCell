package controller.popup.graph;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphPopup {

    private Stage popupStage;
    private List<String> xAxisListAsString;
    private List<Double> yAxisList;
    private String xTitle;
    private String yTitle;
    private boolean isBarChart;

    public GraphPopup(char xAxis, String xTitle, String yTitle, Map<Character, List<String>> columnsForXYaxis, boolean isBarChart) {
        this.xTitle = xTitle;
        this.yTitle = yTitle;
        this.isBarChart = isBarChart;
        initializeData(xAxis, columnsForXYaxis);
        initializeStage();
    }

    public void show() {
        popupStage.show();
    }

    private void initializeData(char xAxis, Map<Character, List<String>> columnsForXYaxis) {
        this.xAxisListAsString = columnsForXYaxis.get(xAxis);
        this.yAxisList = columnsForXYaxis.entrySet().stream()
                .filter(entry -> entry.getKey() != xAxis)
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Y axis data not found"))
                .stream().map(Double::parseDouble)
                .collect(Collectors.toList());

        if (xAxisListAsString.size() != yAxisList.size()) {
            throw new IllegalArgumentException("X and Y axis data sets must be of equal size.");
        }
    }

    private void initializeStage() {
        popupStage = new Stage();
        popupStage.setTitle(isBarChart ? "Column Chart Popup" : "Line Chart Popup");
        Chart chart = isBarChart ? createBarChart() : createLineChart();
        popupStage.setScene(new Scene(new StackPane(chart), 800, 600));
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xTitle);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yTitle);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Column Chart");
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
        lineChart.setTitle("Line Chart");
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
}
