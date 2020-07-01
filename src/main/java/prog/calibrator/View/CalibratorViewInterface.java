package prog.calibrator.View;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public interface CalibratorViewInterface {
    VBox getMainContainer();
    HBox getCalibrationInformationContainer();
    LineChart<Number, Number> getPolynomialChart();
    NumberAxis getXAxisPolynomialChart();
    NumberAxis getYAxisPolynomialChart();
    LineChart.Series<Number, Number> getPolynomialGraphSeries();
    LineChart.Series<Number, Number> getPolynomialPointsSeries();
    HBox getCalibrationPointsInteractionContainer();
    VBox getCalibrationPointsContainer();
    TableView<LineChart.Data<Number, Number>> getCalibrationTable();
    TableColumn<LineChart.Data<Number, Number>, Number> getRawDataColumnCalibrationTable();
    TableColumn<LineChart.Data<Number, Number>, Number> getRealDataColumnCalibrationTable();
    HBox getCalibrationSettersContainer();
    TextField getRawDataCalibrationSetter();
    TextField getRealDataCalibrationSetter();
    CheckBox getEnablePolynomial();
    VBox getCalibrationButtonsContainer();
    Button getAddCalibrationButton();
    Button getDeleteCalibrationButton();
    Button getChangeCalibrationButton();
    ToggleButton getCreateConnectionButton();
    Button getGenerateRandomPointsButton();
    LineChart<Number, Number> getSignalChart();
    XYChart.Series<Number, Number> getSignalGraphSeries();
    NumberAxis getXAxisSignalChart();
    NumberAxis getYAxisSignalChart();
    HBox getSignalParametersContainer();
    TextField getPolynomialFormulaIndicator();
    TextField getSignalAmplitudeIndicator();
    TextField getSignalMaximumIndicator();
    TextField getSignalMinimumIndicator();
    TextField getSignalAverageIndicator();
    TextField getSignalRMSIndicator();

    ListView<String> getChannelsList();
}
