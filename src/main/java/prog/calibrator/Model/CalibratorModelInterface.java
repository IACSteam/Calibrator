package prog.calibrator.Model;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;

public interface CalibratorModelInterface {
    ObservableList<LineChart.Data> getObservableListForPoints();
    ObservableList<LineChart.Data> getObservableListForPolynomial();
    boolean checkIfXIsUnique(Double xCoordinate);
    void deleteDataItem(int index);
    void addDataItem(Double xCoordinate, Double yCoordinate);
    void readDataFromFile(String filePath);
    void generateRandomData();
    void registerObserver(PolynomialObserver observer);
    void removeObserver(PolynomialObserver observer);
    StringProperty getPolynomialProperty();
}
