package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.HeaderController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Cell;
import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.api.EffectiveValue;
import expression.impl.stringFunction.Str;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class MainController {
    Engine engine;

    @FXML
    private HeaderController headerController;
    @FXML private MenuBar menuBar;
    @FXML private VBox header;

    @FXML private ActionLineController actionLineController;
    @FXML private GridPane actionLine;
    @FXML
    private GridController gridController;
    @FXML
    private CustomizeController customizeController;
    Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();
    @FXML
    public void initialize() {
        customizeController.setMainController(this);
        headerController.setMainController(this);
        actionLineController.setMainController(this);
        gridController.setMainController(this);
    }

    public void InitlizeGridBasedOnXML(String absolutePath) {

        try{
            engine.readSheetCellFromXML(absolutePath);

        }catch (Exception e){
            e.printStackTrace();
        }
        gridController.initializeGrid(engine.getSheetCell());
    }

    public void UpdateCell(String text, String newValue) {
        boolean isCellPresent = engine.getSheetCell().isCellPresent(CellLocationFactory.fromCellId(text));
        try {
            engine.updateCell(newValue, text.charAt(0), text.substring(1));
            if (!isCellPresent) {
                Label label = cellLocationToLabel.get(CellLocationFactory.fromCellId(text));
                EffectiveValue effectiveValue = engine.getSheetCell().getViewSheetCell().get(CellLocationFactory.fromCellId(text));
                StringBinding string = effectiveValue.getValueProperty().asString();
                label.textProperty().bind(string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEngine(EngineImpl engine) {
        this.engine = engine;
    }

    public void setCellsLablesMap(Map<CellLocation,Label> cellLocationToLabel) {
        this.cellLocationToLabel = cellLocationToLabel;
    }
}

