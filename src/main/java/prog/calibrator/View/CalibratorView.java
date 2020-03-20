package prog.calibrator.View;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import prog.calibrator.SystemInfo;

import java.util.LinkedList;
import java.util.List;


/**
 * JavaFX App
 */
public class CalibratorView extends Application {

    private ObservableList<LineChart.Data> calibrationPoints;
    private XYChart.Series<Number, Number> signalDataSeries;
    private NumberAxis xAxis;
    private Timeline animation;

    Scene scene;

    BorderPane borderPaneContainer;
    /* */ VBox mainContainer;
    /* *//* */ HBox calibrationInformationContainer;
    /* *//* *//* */ AnchorPane DimasPane;
    /* *//* *//* */ LineChart<Number, Number> polynomialChart;
    /* *//* *//* *//* */ NumberAxis xAxisPolynomialChart;
    /* *//* *//* *//* */ NumberAxis yAxisPolynomialChart;
    /* *//* *//* */ HBox calibrationPointsInteractionContainer;
    /* *//* *//* *//* */ VBox calibrationPointsContainer;
    /* *//* *//* *//* *//* */ TableView calibrationTable;
    /* *//* *//* *//* *//* *//* */ TableColumn rawDataColumnCalibrationTable;
    /* *//* *//* *//* *//* *//* */ TableColumn realDataColumnCalibrationTable;
    /* *//* *//* *//* *//* */ HBox calibrationSettersContainer;
    /* *//* *//* *//* *//* *//* */ TextField rawDataCalibrationSetter;
    /* *//* *//* *//* *//* *//* */ TextField realDataCalibrationSetter;
    /* *//* *//* *//* *//* */ CheckBox enablePolynomial;
    /* *//* *//* *//* */ VBox calibrationButtonsContainer;
    /* *//* *//* *//* *//* */ Button addCalibrationButton;
    /* *//* *//* *//* *//* */ Button deleteCalibrationButton;
    /* *//* *//* *//* *//* */ Button changeCalibrationButton;
    /* *//* *//* *//* *//* */ Button createConnectionButton;
    /* *//* *//* */ LineChart<Number, Number> signalChart;
    /* *//* *//* *//* */ NumberAxis xAxisSignalChart;
    /* *//* *//* *//* */ NumberAxis yAxisSignalChart;
    /* *//* */ HBox signalParametersContainer;
    /* *//* *//* */ TextField polynomialFormulaIndicator;
    /* *//* *//* */ TextField signalAmplitudeIndicator;
    /* *//* *//* */ TextField signalMaximumIndicator;
    /* *//* *//* */ TextField signalMinimumIndicator;
    /* *//* *//* */ TextField signalAverageIndicator;
    /* *//* *//* */ TextField signalRMSIndicator;
    StackPane systemInfoPane;

    private Scene createScene() {

        borderPaneContainer = new BorderPane();
        borderPaneContainer.setCenter(addMainContainer());
        borderPaneContainer.setBottom(addSystemInfoPane());
        return new Scene(borderPaneContainer);
    }

    private VBox addMainContainer() {

        mainContainer = new VBox();
        calibrationInformationContainer = new HBox();
        calibrationInformationContainer.getChildren().add(createDimasContainer());
        calibrationInformationContainer.getChildren().add(createPolynomialChart());
        calibrationInformationContainer.getChildren().add(createCalibrationPointsInteractionContainer());
        mainContainer.getChildren().addAll(calibrationInformationContainer);
        mainContainer.getChildren().addAll(createSignalParametersContainer());
        mainContainer.getChildren().addAll(createSignalChart());
        return mainContainer;
    }

    private LineChart<Number, Number> createPolynomialChart() {

        xAxisPolynomialChart = new NumberAxis();
        yAxisPolynomialChart = new NumberAxis();
        xAxisPolynomialChart.setLabel(ViewConstants.X_AXIS_POLYNOMIAL_CHART_TEXT);
        yAxisPolynomialChart.setLabel(ViewConstants.Y_AXIS_POLYNOMIAL_CHART_TEXT);
        polynomialChart = new LineChart<>(xAxisPolynomialChart, yAxisPolynomialChart);
        polynomialChart.getStyleClass().add("polynomial-Chart");
        polynomialChart.setTitle(ViewConstants.POLYNOMIAL_CHART_TEXT);
        HBox.setHgrow(polynomialChart, Priority.ALWAYS);
        return polynomialChart;
    }

    private HBox createCalibrationPointsInteractionContainer() {

        calibrationPointsInteractionContainer = new HBox();
        calibrationPointsInteractionContainer.getChildren().add(createCalibrationPointsContainer());
        calibrationPointsInteractionContainer.getChildren().add(createCalibrationButtonsContainer());
        return calibrationPointsInteractionContainer;
    }

    private VBox createCalibrationPointsContainer() {

        calibrationPointsContainer = new VBox();
        calibrationTable = new TableView();
        calibrationTable.getStyleClass().add("calibration-Table");
        rawDataColumnCalibrationTable = new TableColumn(ViewConstants.X_AXIS_POLYNOMIAL_CHART_TEXT);
        realDataColumnCalibrationTable = new TableColumn(ViewConstants.Y_AXIS_POLYNOMIAL_CHART_TEXT);
        calibrationTable.setEditable(false);
        calibrationTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        calibrationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        calibrationTable.getColumns().addAll(rawDataColumnCalibrationTable, realDataColumnCalibrationTable);
        calibrationTable.setPlaceholder(new Text("No data"));
        calibrationTable.setItems(calibrationPoints);
        enablePolynomial = new CheckBox(ViewConstants.ENABLE_POLYNOMIAL_TEXT);
        enablePolynomial.getStyleClass().add("enable-Polynomial");
        calibrationPointsContainer.getChildren().addAll(calibrationTable, createCalibrationSettersContainer(), enablePolynomial);
        return calibrationPointsContainer;
    }

    private HBox createCalibrationSettersContainer() {

        calibrationSettersContainer = new HBox();
        rawDataCalibrationSetter = new TextField();
        Label label = new Label(ViewConstants.RAW_DATA_CALIBRATION_SETTER_LABEL, rawDataCalibrationSetter);
        label.setContentDisplay(ContentDisplay.BOTTOM);
        realDataCalibrationSetter = new TextField();
        rawDataCalibrationSetter = new TextField();
        Label label1 = new Label(ViewConstants.REAL_DATA_CALIBRATION_SETTER_LABEL, realDataCalibrationSetter);
        label1.setContentDisplay(ContentDisplay.BOTTOM);
        label.getStyleClass().add("data-Calibration-Setter");
        label1.getStyleClass().add("data-Calibration-Setter");
        calibrationSettersContainer.getChildren().addAll(label, label1);
        return calibrationSettersContainer;
    }

    private VBox createCalibrationButtonsContainer() {

        calibrationButtonsContainer = new VBox();
        calibrationButtonsContainer.getStyleClass().add("calibration-Buttons-Container");
        addCalibrationButton = new Button(ViewConstants.ADD_CALIBRATION_BUTTON_TEXT);
        deleteCalibrationButton = new Button(ViewConstants.DELETE_CALIBRATION_BUTTON_TEXT);
        changeCalibrationButton = new Button(ViewConstants.CHANGE_CALIBRATION_BUTTON_TEXT);
        createConnectionButton = new Button(ViewConstants.CREATE_CONNECTION_BUTTON_TEXT);
        calibrationButtonsContainer.getChildren().addAll(addCalibrationButton, deleteCalibrationButton,
                changeCalibrationButton, createConnectionButton);
        return calibrationButtonsContainer;
    }

    private HBox createSignalParametersContainer() {
        signalParametersContainer = new HBox();
        polynomialFormulaIndicator = new TextField();
        signalAmplitudeIndicator = new TextField();
        signalMaximumIndicator = new TextField();
        signalMinimumIndicator = new TextField();
        signalAverageIndicator = new TextField();
        signalRMSIndicator = new TextField();
        List<Label> elementsList = new LinkedList<>();

        elementsList.add(new Label(ViewConstants.POLYNOMIAL_FORMULA_INDICATOR_LABEL, polynomialFormulaIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_AMPLITUDE_INDICATOR_LABEL, signalAmplitudeIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_MAXIMUM_INDICATOR_LABEL, signalMaximumIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_MINIMUM_INDICATOR_LABEL, signalMinimumIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_AVERAGE_INDICATOR_LABEL, signalAverageIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_RMS_INDICATOR_LABEL, signalRMSIndicator));
        for(Label element : elementsList) {
            element.setContentDisplay(ContentDisplay.BOTTOM);
        }
        signalParametersContainer.getChildren().addAll(elementsList);
        return signalParametersContainer;
    }

    private LineChart<Number, Number> createSignalChart() {

        xAxisSignalChart = new NumberAxis();
        yAxisSignalChart = new NumberAxis();
        xAxisSignalChart.setLabel(ViewConstants.X_AXIS_SIGNAL_CHART_TEXT);
        yAxisSignalChart.setLabel(ViewConstants.Y_AXIS_SIGNAL_CHART_TEXT);
        signalChart = new LineChart<>(xAxisSignalChart, yAxisSignalChart);
        signalChart.getStyleClass().add("signal-Chart");
        signalChart.setTitle(ViewConstants.POLYNOMIAL_CHART_TEXT);
        signalChart.setCreateSymbols(false);
        signalChart.setAnimated(false);
        signalChart.setLegendVisible(false);
        signalChart.setTitle(ViewConstants.SIGNAL_CHART_TITLE);
        xAxisSignalChart.setForceZeroInRange(false);
        signalDataSeries = new XYChart.Series<>();
        HBox.setHgrow(signalChart, Priority.ALWAYS);
        VBox.setVgrow(signalChart, Priority.ALWAYS);
        return signalChart;
    }

    @Override
    public void start(Stage stage) {
        scene = createScene();
        scene.getStylesheets().add("CalibratorView/Styles.css");
        stage.setHeight(ViewConstants.WINDOW_HEIGHT);
        stage.setWidth(ViewConstants.WINDOW_WIDTH);
        stage.setMinWidth(ViewConstants.WINDOW_WIDTH);
        stage.setMinHeight(ViewConstants.WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    private Pane addSystemInfoPane() {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        return systemInfoPane = new StackPane(label);
    }

    private AnchorPane createDimasContainer() {
        AnchorPane DimasContainer = new AnchorPane();
        DimasContainer.setPrefSize(450, 250);
        return DimasContainer;
    }

    public static void main(String[] args) {
        launch();
    }

}

