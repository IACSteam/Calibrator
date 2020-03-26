package prog.calibrator.View;

import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;


/**
 * JavaFX App
 */
public class CalibratorView /*extends Application*/ implements CalibratorViewInterface {

    private NumberAxis xAxis;
    private Timeline animation;

    Scene scene;

    BorderPane borderPaneContainer;
    /* */ VBox mainContainer;
    /* *//* */ HBox calibrationInformationContainer;
    /* *//* *//* */ HBox DimasContainer;
    /* *//* *//* *//* */ ListView<String> channelsList;
    /* *//* *//* */ LineChart<Number, Number> polynomialChart;
    /* *//* *//* *//* */ NumberAxis xAxisPolynomialChart;
    /* *//* *//* *//* */ NumberAxis yAxisPolynomialChart;
    /* *//* *//* *//* */ LineChart.Series<Number, Number> polynomialGraphSeries;
    /* *//* *//* *//* */ LineChart.Series<Number, Number> polynomialPointsSeries;
    /* *//* *//* */ HBox calibrationPointsInteractionContainer;
    /* *//* *//* *//* */ VBox calibrationPointsContainer;
    /* *//* *//* *//* *//* */ TableView<LineChart.Data<Number, Number>> calibrationTable;
    /* *//* *//* *//* *//* *//* */ TableColumn <LineChart.Data<Number, Number>, Number> rawDataColumnCalibrationTable;
    /* *//* *//* *//* *//* *//* */ TableColumn <LineChart.Data<Number, Number>, Number> realDataColumnCalibrationTable;
    /* *//* *//* *//* *//* */ HBox calibrationSettersContainer;
    /* *//* *//* *//* *//* *//* */ TextField rawDataCalibrationSetter;
    /* *//* *//* *//* *//* *//* */ TextField realDataCalibrationSetter;
    /* *//* *//* *//* *//* */ CheckBox enablePolynomial;
    /* *//* *//* *//* */ VBox calibrationButtonsContainer;
    /* *//* *//* *//* *//* */ Button addCalibrationButton;
    /* *//* *//* *//* *//* */ Button deleteCalibrationButton;
    /* *//* *//* *//* *//* */ Button changeCalibrationButton;
    /* *//* *//* *//* *//* */ Button createConnectionButton;
    /* *//* */ LineChart<Number, Number> signalChart;
    /* *//* *//* */ LineChart.Series<Number, Number> signalGraphSeries;
    /* *//* *//* */ NumberAxis xAxisSignalChart;
    /* *//* *//* */ NumberAxis yAxisSignalChart;
    /* *//* */ HBox signalIndicatorsContainer;
    /* *//* *//* */ TextField polynomialFormulaIndicator;
    /* *//* */ /* */HBox signalParametersContainer;
    /* *//* *//* *//* */ TextField signalAmplitudeIndicator;
    /* *//* *//* *//* */ TextField signalMaximumIndicator;
    /* *//* *//* *//* */ TextField signalMinimumIndicator;
    /* *//* *//* *//* */ TextField signalAverageIndicator;
    /* *//* *//* *//* */ TextField signalRMSIndicator;
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
        mainContainer.getChildren().addAll(createSignalIndicatorsContainer()/*createSignalParametersContainer()*/);
        mainContainer.getChildren().addAll(createSignalChart());
        return mainContainer;
    }

    private LineChart<Number, Number> createPolynomialChart() {

        xAxisPolynomialChart = new NumberAxis();
        yAxisPolynomialChart = new NumberAxis();
        xAxisPolynomialChart.setLabel(ViewConstants.X_AXIS_POLYNOMIAL_CHART_TEXT);
        yAxisPolynomialChart.setLabel(ViewConstants.Y_AXIS_POLYNOMIAL_CHART_TEXT);
        xAxisPolynomialChart.setForceZeroInRange(false);
        yAxisPolynomialChart.setForceZeroInRange(false);
        polynomialChart = new LineChart<>(xAxisPolynomialChart, yAxisPolynomialChart);
        polynomialChart.getStyleClass().add("polynomial-Chart");
        polynomialChart.setTitle(ViewConstants.POLYNOMIAL_CHART_TEXT);
        polynomialChart.setAnimated(false);
        polynomialChart.setLegendVisible(false);
        polynomialGraphSeries = new XYChart.Series<>();
        polynomialPointsSeries = new XYChart.Series<>();
        //polynomialChart.getData().add(polynomialGraphSeries);
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
        rawDataColumnCalibrationTable = new TableColumn<>(ViewConstants.X_AXIS_POLYNOMIAL_CHART_TEXT);
        realDataColumnCalibrationTable = new TableColumn<>(ViewConstants.Y_AXIS_POLYNOMIAL_CHART_TEXT);
        calibrationTable.setEditable(false);
        calibrationTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        calibrationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        calibrationTable.getColumns().addAll(rawDataColumnCalibrationTable, realDataColumnCalibrationTable);
        calibrationTable.setPlaceholder(new Text("No data"));
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

    private HBox createSignalIndicatorsContainer() {
        HBox signalIndicatorsContainer = new HBox();
        polynomialFormulaIndicator = new TextField();
        Label polynomialFormula = new Label(ViewConstants.POLYNOMIAL_FORMULA_INDICATOR_LABEL, polynomialFormulaIndicator);
        polynomialFormula.setContentDisplay(ContentDisplay.BOTTOM);
        polynomialFormulaIndicator.getStyleClass().add("polynomial-Formula-Indicator");
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        signalIndicatorsContainer.getChildren().addAll(region, createSignalParametersContainer(), polynomialFormula);
        signalIndicatorsContainer.getStyleClass().add("signal-Indicators-Container");
        return signalIndicatorsContainer;
    }
    private HBox createSignalParametersContainer() {
        signalParametersContainer = new HBox();

        signalAmplitudeIndicator = new TextField();
        signalMaximumIndicator = new TextField();
        signalMinimumIndicator = new TextField();
        signalAverageIndicator = new TextField();
        signalRMSIndicator = new TextField();
        List<Label> elementsList = new LinkedList<>();

        elementsList.add(new Label(ViewConstants.SIGNAL_AMPLITUDE_INDICATOR_LABEL, signalAmplitudeIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_MAXIMUM_INDICATOR_LABEL, signalMaximumIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_MINIMUM_INDICATOR_LABEL, signalMinimumIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_AVERAGE_INDICATOR_LABEL, signalAverageIndicator));
        elementsList.add(new Label(ViewConstants.SIGNAL_RMS_INDICATOR_LABEL, signalRMSIndicator));
        for(Label element : elementsList) {
            element.setContentDisplay(ContentDisplay.BOTTOM);
        }
        signalParametersContainer.getChildren().addAll(elementsList);
        signalParametersContainer.getStyleClass().add("signal-Parameters-Container");
        return signalParametersContainer;
    }

    private LineChart<Number, Number> createSignalChart() {

        xAxisSignalChart = new NumberAxis(0, 1000, 100);
        yAxisSignalChart = new NumberAxis(-3, 3, 1);
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
        signalGraphSeries = new XYChart.Series<>();
        signalChart.getData().add(signalGraphSeries);
        HBox.setHgrow(signalChart, Priority.ALWAYS);
        VBox.setVgrow(signalChart, Priority.ALWAYS);
        return signalChart;
    }

    //@Override
    public void start(Stage stage) {
        scene = createScene();
        scene.getStylesheets().add("CalibratorView/Styles.css");
        stage.setHeight(ViewConstants.WINDOW_HEIGHT);
        stage.setWidth(ViewConstants.WINDOW_WIDTH);
        stage.setMinWidth(ViewConstants.WINDOW_WIDTH);
        stage.setMinHeight(ViewConstants.WINDOW_HEIGHT);
        stage.setScene(scene);
        bindings();
        stage.show();
    }
    private void bindings() {
        signalParametersContainer.prefWidthProperty().bind(polynomialChart.widthProperty());
        polynomialFormulaIndicator.prefWidthProperty().bind(calibrationPointsInteractionContainer.widthProperty());
        polynomialChart.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
                signalParametersContainer.setSpacing(((double) newWidth - (75*5) - 20) / 4);
            }
        });
    };
    private Pane addSystemInfoPane() {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        return systemInfoPane = new StackPane(label);
    }

    private HBox createDimasContainer() {
        DimasContainer = new HBox();
        DimasContainer.setPrefSize(450, 250);
        channelsList = new ListView<String>();
        /*ObservableList<String> seasonList = FXCollections.<String>observableArrayList(
                "Spring", "Summer", "Fall", "Winter");
        seasons.getItems().addAll(seasonList);*/
        Label placeHolder = new Label("No seasons available for selection.");
        channelsList.setPlaceholder(placeHolder);
        DimasContainer.getChildren().add(channelsList);
        return DimasContainer;
    }

    public static void main(String[] args) {
        //launch();
    }

    public CalibratorView(Stage stage) {
        start(stage);
    }
    public VBox getMainContainer() {
        return mainContainer;
    }

    public HBox getCalibrationInformationContainer() {
        return calibrationInformationContainer;
    }

    public LineChart<Number, Number> getPolynomialChart() {
        return polynomialChart;
    }

    public NumberAxis getXAxisPolynomialChart() {
        return xAxisPolynomialChart;
    }

    public NumberAxis getYAxisPolynomialChart() {
        return yAxisPolynomialChart;
    }

    public LineChart.Series<Number, Number> getPolynomialGraphSeries() {
        return polynomialGraphSeries;
    }

    public LineChart.Series<Number, Number> getPolynomialPointsSeries() {
        return polynomialPointsSeries;
    }

    public HBox getCalibrationPointsInteractionContainer() {
        return calibrationPointsInteractionContainer;
    }

    public VBox getCalibrationPointsContainer() {
        return calibrationPointsContainer;
    }

    public TableView<LineChart.Data<Number, Number>> getCalibrationTable() {
        return calibrationTable;
    }

    public TableColumn<LineChart.Data<Number, Number>, Number> getRawDataColumnCalibrationTable() {
        return rawDataColumnCalibrationTable;
    }

    public TableColumn<LineChart.Data<Number, Number>, Number> getRealDataColumnCalibrationTable() {
        return realDataColumnCalibrationTable;
    }

    public HBox getCalibrationSettersContainer() {
        return calibrationSettersContainer;
    }

    public TextField getRawDataCalibrationSetter() {
        return rawDataCalibrationSetter;
    }

    public TextField getRealDataCalibrationSetter() {
        return realDataCalibrationSetter;
    }

    public CheckBox getEnablePolynomial() {
        return enablePolynomial;
    }

    public VBox getCalibrationButtonsContainer() {
        return calibrationButtonsContainer;
    }

    public Button getAddCalibrationButton() {
        return addCalibrationButton;
    }

    public Button getDeleteCalibrationButton() {
        return deleteCalibrationButton;
    }

    public Button getChangeCalibrationButton() {
        return changeCalibrationButton;
    }

    public Button getCreateConnectionButton() {
        return createConnectionButton;
    }

    public LineChart<Number, Number> getSignalChart() {
        return signalChart;
    }

    public XYChart.Series<Number, Number> getSignalGraphSeries() {
        return signalGraphSeries;
    }

    public NumberAxis getXAxisSignalChart() {
        return xAxisSignalChart;
    }

    public NumberAxis getYAxisSignalChart() {
        return yAxisSignalChart;
    }

    public HBox getSignalParametersContainer() {
        return signalParametersContainer;
    }

    public TextField getPolynomialFormulaIndicator() {
        return polynomialFormulaIndicator;
    }

    public TextField getSignalAmplitudeIndicator() {
        return signalAmplitudeIndicator;
    }

    public TextField getSignalMaximumIndicator() {
        return signalMaximumIndicator;
    }

    public TextField getSignalMinimumIndicator() {
        return signalMinimumIndicator;
    }

    public TextField getSignalAverageIndicator() {
        return signalAverageIndicator;
    }

    public TextField getSignalRMSIndicator() {
        return signalRMSIndicator;
    }

    public ListView<String> getChannelsList() {
        return channelsList;
    }
}

