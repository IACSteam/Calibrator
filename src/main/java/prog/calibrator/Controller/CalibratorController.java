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
    }
}
