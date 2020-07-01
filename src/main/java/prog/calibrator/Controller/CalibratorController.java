package prog.calibrator.Controller;

import javafx.scene.chart.LineChart;
import javafx.scene.control.cell.PropertyValueFactory;
import prog.calibrator.Model.CalibratorModelInterface;
import prog.calibrator.View.CalibratorViewInterface;
import prog.calibrator.View.ViewConstants;

import java.util.List;

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
        calibratorView.getGenerateRandomPointsButton().setOnMouseReleased((value) -> {
            calibrationModel.generateRandomData();
        });

//        calibratorView.getRawDataCalibrationSetter().setOnKeyReleased((keyEvent) -> { checkPolynomialPointsInputFields(); });
//        calibratorView.getRealDataCalibrationSetter().setOnKeyReleased((keyEvent) -> { checkPolynomialPointsInputFields(); });
        calibratorView.getRawDataCalibrationSetter().textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    checkPolynomialPointsInputFields();
                });
        calibratorView.getRealDataCalibrationSetter().textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    checkPolynomialPointsInputFields();
                });
        calibratorView.getAddCalibrationButton().setOnMouseReleased((mouseEvent) -> {
            addCalibrationDataItem();
        });

        calibratorView.getChangeCalibrationButton().setOnMouseReleased((mouseEvent) -> {
            changeCalibrationDataItem();
        });

        calibratorView.getDeleteCalibrationButton().setOnMouseReleased((mouseEvent) -> {
            deleteCalibrationDataItem();
        });

        calibratorView.getCreateConnectionButton().setOnMouseReleased((mouseEvent) -> {
            manageConnection();
        });

        calibratorView.getCalibrationTable().setOnMouseClicked(mouseEvent -> tableMouseClickedEvent(mouseEvent.getClickCount()));
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
        calibratorView.getSignalGraphSeries().dataProperty().bind(calibrationModel.signalChartPointsProperty());
        calibratorView.getChannelsList().getItems().addAll(calibrationModel.getListOfChannels());

        calibratorView.getSignalAmplitudeIndicator().textProperty().bind(calibrationModel.amplitudeSignalProperty().asString("%.3f"));
        calibratorView.getSignalMaximumIndicator().textProperty().bind(calibrationModel.maxValueSignalProperty().asString("%.3f"));
        calibratorView.getSignalMinimumIndicator().textProperty().bind(calibrationModel.minValueSignalProperty().asString("%.3f"));
        calibratorView.getSignalAverageIndicator().textProperty().bind(calibrationModel.meanValueSignalProperty().asString("%.3f"));
        calibratorView.getSignalRMSIndicator().textProperty().bind(calibrationModel.rmsValueSignalProperty().asString("%.3f"));
        calibrationModel.enablePolynomialProperty().bind(calibratorView.getEnablePolynomial().selectedProperty());
    }

    private void addCalibrationDataItem() {
        try {
            Double rawElement = checkElement(calibratorView.getRawDataCalibrationSetter().getText());
            Double realElement = checkElement(calibratorView.getRealDataCalibrationSetter().getText());
            calibrationModel.addCalibrationPoint(rawElement, realElement);
            calibratorView.getRawDataCalibrationSetter().setText("");
            calibratorView.getRealDataCalibrationSetter().setText("");
        } catch (IllegalCalibrationPointException e) {
            calibratorView.getAddCalibrationButton().setDisable(true);
        }
    }

    private void checkPolynomialPointsInputFields() {
        try {
            Double rawElement = checkElement(calibratorView.getRawDataCalibrationSetter().getText());
            Double realElement = checkElement(calibratorView.getRealDataCalibrationSetter().getText());
            if (!calibrationModel.checkIfXIsUnique(rawElement)) {
                calibratorView.getAddCalibrationButton().setDisable(true);
                calibratorView.getDeleteCalibrationButton().setDisable(true);
                calibratorView.getChangeCalibrationButton().setDisable(false);
            } else {
                calibratorView.getAddCalibrationButton().setDisable(false);
                calibratorView.getDeleteCalibrationButton().setDisable(true);
                calibratorView.getChangeCalibrationButton().setDisable(true);
            }
        } catch (IllegalCalibrationPointException e) {
            calibratorView.getAddCalibrationButton().setDisable(true);
            calibratorView.getDeleteCalibrationButton().setDisable(true);
            calibratorView.getChangeCalibrationButton().setDisable(true);
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

    private boolean checkIfRawDuplicates(double rawValue) {
        List<LineChart.Data<Number, Number>> polynomialPoints = calibrationModel.getObservableListForPoints();
        return polynomialPoints.stream().map(p -> p.getXValue()).anyMatch(p -> p.equals(rawValue));
    }

    private void changeCalibrationDataItem() {
        try {
            Double rawElement = checkElement(calibratorView.getRawDataCalibrationSetter().getText());
            Double realElement = checkElement(calibratorView.getRealDataCalibrationSetter().getText());
            calibratorView.getRawDataCalibrationSetter().setText("");
            calibratorView.getRealDataCalibrationSetter().setText("");
            calibratorView.getRawDataCalibrationSetter().setDisable(false);
            int index = calibratorView.getCalibrationTable().getSelectionModel().getSelectedIndex();
            calibrationModel.updateCalibrationPoint(index, realElement);
        } catch (IllegalCalibrationPointException e) {
            if (calibratorView.getRawDataCalibrationSetter().getText().isEmpty()) {
                tableMouseClickedEvent(2);
            }
        }


    }

    private void tableMouseClickedEvent(int clickCount) {
        if (clickCount == 1) {

            calibratorView.getRawDataCalibrationSetter().setText("");
            calibratorView.getRealDataCalibrationSetter().setText("");
            //calibratorView.getAddCalibrationButton().setDisable(false);
            calibratorView.getDeleteCalibrationButton().setDisable(false);
            calibratorView.getChangeCalibrationButton().setDisable(false);
            calibratorView.getRawDataCalibrationSetter().setDisable(false);

        }
        if (clickCount > 1) {

            LineChart.Data<Number, Number> dataItem = calibrationModel.getDataItem(calibratorView.getCalibrationTable().getSelectionModel().getSelectedIndex());
            calibratorView.getRawDataCalibrationSetter().setText(dataItem.getXValue().toString());
            calibratorView.getRealDataCalibrationSetter().setText(dataItem.getYValue().toString());

            //calibratorView.getAddCalibrationButton().setDisable(true);
            calibratorView.getDeleteCalibrationButton().setDisable(false);
            calibratorView.getChangeCalibrationButton().setDisable(false);
            calibratorView.getRawDataCalibrationSetter().setDisable(true);


        }
    }

    private void deleteCalibrationDataItem() {
        calibrationModel.deleteDataItem(calibratorView.getCalibrationTable().getSelectionModel().getSelectedIndex());
        calibratorView.getRawDataCalibrationSetter().setText("");
        calibratorView.getRealDataCalibrationSetter().setText("");
        calibratorView.getDeleteCalibrationButton().setDisable(false);
        calibratorView.getChangeCalibrationButton().setDisable(false);
        calibratorView.getRawDataCalibrationSetter().setDisable(false);
    }

    private void manageConnection() {
        boolean selectedStatus = calibratorView.getCreateConnectionButton().isSelected();
        String text = selectedStatus ?
                ViewConstants.CREATE_CONNECTION_BUTTON_TEXT_TRUE : ViewConstants.CREATE_CONNECTION_BUTTON_TEXT_FALSE;
        calibratorView.getCreateConnectionButton().setText(text);
        if(selectedStatus) {calibrationModel.startSignalReceive();}
        else {calibrationModel.stopSignalReceive();}
    }
}
