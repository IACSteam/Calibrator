package prog.calibrator;

import javafx.application.Application;
import javafx.stage.Stage;
import prog.calibrator.Controller.CalibratorController;
import prog.calibrator.Model.CalibratorModel;
import prog.calibrator.View.CalibratorView;
import prog.calibrator.electrical_cabinet_model.interfaces.CalibrationInterface;
import prog.calibrator.electrical_cabinet_model.model.CabinetExample;

import static javafx.application.Application.launch;

public class Start extends Application {
    public static boolean THREADS_STOP = false;

    public static void main(String[] args) {
        launch(args);
    }
    private CalibratorView calibratorView;
    private CalibratorModel calibratorModel;
    private CalibratorController calibratorController;
    @Override
    public void start(Stage primaryStage) {

        CalibrationInterface[] electricalCabinets = {new CabinetExample()};
        this.calibratorView = new CalibratorView(primaryStage);
        this.calibratorModel = new CalibratorModel();
        calibratorModel.setElectricalCabinets(electricalCabinets);
        this.calibratorController = new CalibratorController(calibratorView, calibratorModel.getGUICalibratorModel());

    }
}