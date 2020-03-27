package prog.calibrator.Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.List;

public interface CalibratorModelInterface {
    ObservableList<LineChart.Data<Number, Number>> getObservableListForPoints();
    ObservableList<LineChart.Data<Number, Number>> getObservableListForPolynomial();
    boolean checkIfXIsUnique(Double xCoordinate);
    void deleteDataItem(int index);
    void addDataItem(Double xCoordinate, Double yCoordinate);
    void readDataFromFile(String filePath);
    void generateRandomData();
    void registerObserver(PolynomialObserver observer);
    void removeObserver(PolynomialObserver observer);
    StringProperty getPolynomialProperty();
    List<XYChart.Data<Number, Number>> getSignalChartData();
    ObservableList<String> getListOfChannels();

    DoubleProperty amplitudeSignalProperty();
    DoubleProperty maxValueSignalProperty();
    DoubleProperty minValueSignalProperty();
    DoubleProperty meanValueSignalProperty();
    DoubleProperty rmsValueSignalProperty();
    BooleanProperty enablePolynomialProperty();
}
