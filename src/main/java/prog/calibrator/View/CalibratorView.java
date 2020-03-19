package prog.calibrator.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import prog.calibrator.SystemInfo;


/**
 * JavaFX App
 */
public class CalibratorView extends Application {

    Scene scene;

    BorderPane borderPaneContainer;
    /* */ VBox mainContainer;
    /* *//* */ HBox calibrationInformationContainer;
    /* *//* *//* */ LineChart<Number, Number> lineChart;
    /* *//* *//* */ LineChart<Number, Number> polynomialChart;
    /* *//* *//* */ HBox CalibrationPointsInteractionContainer;
    /* *//* *//* *//* */TableView table;
    TableView table1;

    NumberAxis xAxis;
    NumberAxis yAxis;
    NumberAxis xAxis1;
    NumberAxis yAxis1;
    StackPane systemInfoPane;

    private Scene createScene() {
        // Use a border pane as the root for scene
        borderPaneContainer = new BorderPane();
        //mainContainer.setLeft(addVBoxToLeftPanel());
        borderPaneContainer.setCenter(addMainContainer());
        borderPaneContainer.setBottom(addSystemInfoPane());
        return new Scene(borderPaneContainer);
    }

    private Pane addSystemInfoPane() {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        return systemInfoPane = new StackPane(label);
    }

    private VBox addMainContainer() {

        VBox mainContainer = new VBox();
        HBox CalibrationInformationContainer = new HBox();
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Ось X");
        yAxis.setLabel("Ось Y");
        xAxis1 = new NumberAxis();
        yAxis1 = new NumberAxis();
        xAxis1.setLabel("Ось X");
        yAxis1.setLabel("Ось Y");
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Вид полинома");

        table = new TableView<>();
        table.setPrefSize(200, 250);
        table.setEditable(false);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table1 = new TableView<>();
        table1.setPrefSize(200, 250);
        table1.setEditable(false);
        table1.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        polynomialChart = new LineChart<Number,Number>(xAxis1,yAxis1);
        polynomialChart.setTitle("Вид полинома");
        VBox.setVgrow(polynomialChart, Priority.ALWAYS);
        HBox.setHgrow(lineChart, Priority.ALWAYS);

        CalibrationInformationContainer.getChildren().addAll(table1, lineChart, table);
        mainContainer.getChildren().addAll(CalibrationInformationContainer, polynomialChart);
        return mainContainer;
    }

    @Override
    public void start(Stage stage) {
        /*var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        var scene = new Scene(new StackPane(label), 640, 480);*/
        scene = createScene();
        stage.setHeight(600);
        stage.setWidth(1200);
        stage.setMinWidth(1200);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}