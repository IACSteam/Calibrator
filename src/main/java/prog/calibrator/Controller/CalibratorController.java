package prog.calibrator.Controller;

import javafx.scene.chart.LineChart;
import javafx.scene.control.cell.PropertyValueFactory;
import prog.calibrator.Model.CalibratorModelInterface;
import prog.calibrator.View.CalibratorViewInterface;

public class CalibratorController {

    private CalibratorViewInterface calibratorView;
    private CalibratorModelInterface calibrationModel;

    public CalibratorController(CalibratorViewInterface calibratorViewInterface,
                                CalibratorModelInterface calibratorModelInterface) {
        this.calibratorView = calibratorViewInterface;
        this.calibrationModel = calibratorModelInterface;

        initializeEvents();
        bindViewAndModel();
    }

    private void initializeEvents() {
        calibratorView.getCreateConnectionButton().setOnMouseReleased((value) -> {
            calibrationModel.generateRandomData();
        });

//        calibratorView.getRawDataCalibrationSetter().setOnKeyReleased((keyEvent) -> { checkPolynomialPointsInputFields(); });
//        calibratorView.getRealDataCalibrationSetter().setOnKeyReleased((keyEvent) -> { checkPolynomialPointsInputFields(); });
        calibratorView.getRawDataCalibrationSetter().textProperty().addListener(
                (observable, oldValue, newValue) -> {checkPolynomialPointsInputFields();});
        calibratorView.getRealDataCalibrationSetter().textProperty().addListener(
                (observable, oldValue, newValue) -> {checkPolynomialPointsInputFields();});
        calibratorView.getAddCalibrationButton().setOnMouseReleased((mouseEvent) -> { addCalibrationDataItem(); });
    }

    private void bindViewAndModel() {
        calibratorView.getRawDataColumnCalibrationTable().
                setCellValueFactory(new PropertyValueFactory<LineChart.Data<Number, Number>, Number>("XValue"));
        calibratorView.getRealDataColumnCalibrationTable().
                setCellValueFactory(new PropertyValueFactory<LineChart.Data<Number, Number>, Number>("YValue"));
        calibratorView.getCalibrationTable().setItems(calibrationModel.getObservableListForPoints());
        calibratorView.getPolynomialGraphSeries().setData(calibrationModel.getObservableListForPolynomial());
        calibratorView.getPolynomialPointsSeries().setData(calibrationModel.getObservableListForPoints());
        calibratorView.getPolynomialChart().getData().add(calibratorView.getPolynomialGraphSeries());
        calibratorView.getPolynomialChart().getData().add(calibratorView.getPolynomialPointsSeries());
        calibratorView.getPolynomialFormulaIndicator().textProperty().bind(calibrationModel.getPolynomialProperty());
        calibratorView.getSignalGraphSeries().getData().addAll(calibrationModel.getSignalChartData());
        calibratorView.getChannelsList().getItems().addAll(calibrationModel.getListOfChannels());
    }

    private void addCalibrationDataItem() {
        try {
            Double rawElement = checkElement( calibratorView.getRawDataCalibrationSetter().getText() );
            Double realElement = checkElement( calibratorView.getRealDataCalibrationSetter().getText() );
            calibrationModel.addDataItem(rawElement, realElement);
        } catch (IllegalCalibrationPointException e) {
            calibratorView.getAddCalibrationButton().setDisable(true);
        }
    }

    private void checkPolynomialPointsInputFields() {
        try {
            Double rawElement = checkElement( calibratorView.getRawDataCalibrationSetter().getText() );
            Double realElement = checkElement( calibratorView.getRealDataCalibrationSetter().getText() );
            calibratorView.getAddCalibrationButton().setDisable(false);
        } catch (IllegalCalibrationPointException e) {
            calibratorView.getAddCalibrationButton().setDisable(true);
        }
    }

    private Double checkElement(String text) throws IllegalCalibrationPointException {

        try {
            Double value = Double.valueOf(text);
//            view.makeButtonAddEnabled();
//            xCoordinate = true;
//            changeAddButtonState();
            return value;
        } catch (NumberFormatException e) {
//            view.makeButtonAddDisabled();
//            xCoordinate = false;
//            changeAddButtonState();
            throw new IllegalCalibrationPointException("Impossible to convert \"" + text + "\" to double");
        }
    }
}
