package prog.calibrator.Model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;

import java.util.*;
import java.util.stream.Collectors;

public class CalibratorModel implements CalibratorModelInterface {

    private ObservableList<LineChart.Data<Number, Number>> listForPoints;
    private ObservableList<LineChart.Data<Double, Double>> listForPointsLocal;
    private ObservableList<LineChart.Data<Number, Number>> listForPolynomial;
    private ArrayList<PolynomialObserver> polynomialObservers = new ArrayList<PolynomialObserver>();
    private CalculatePolynomial calculatePolynomial;
    private String polynomialString;
    private StringProperty polynomial;

    public CalibratorModel() {

        polynomial = new SimpleStringProperty("y = 1.0x + 0");
        listForPoints = FXCollections.observableArrayList();
        listForPolynomial = FXCollections.observableArrayList();
        listForPoints.add(new LineChart.Data<>(0, 0));
        listForPolynomial.add(new LineChart.Data<>(0, 0));
        listForPoints.add(new LineChart.Data<>(1, 1));
        listForPolynomial.add(new LineChart.Data<>(1, 1));
        calculatePolynomial = new CalculatePolynomial();
        createEventListeners();
    }

    public static void main(String[] args) {

    }


    @Override
    public ObservableList<LineChart.Data<Number, Number>> getObservableListForPoints() {

        return listForPoints;
    }

    @Override
    public ObservableList<LineChart.Data<Number, Number>> getObservableListForPolynomial() {

        return listForPolynomial;
    }

    @Override
    public void deleteDataItem(int index) {
        if( index>=0 ) {
            listForPoints.remove(index);
        }
    }

    @Override
    public void addDataItem(Double xCoordinate, Double yCoordinate) {
        Platform.runLater( () -> {
            listForPoints.add(new LineChart.Data<>(xCoordinate, yCoordinate));
        });
    }

    @Override
    public void readDataFromFile(String filePath) {

    }

    @Override
    public boolean checkIfXIsUnique(Double xCoordinate) {
        System.out.println(xCoordinate);
        return !listForPoints.stream().map(p -> p.getXValue()).anyMatch( p -> p.equals(xCoordinate) );
    }

    @Override
    public void generateRandomData() {
        int max = 10;
        Random random = new Random();
        ArrayList<LineChart.Data<Number, Number>> aaa = new ArrayList<>();
        for (int i = 1; i < max; i++) {
            double value1 = 2 * i;
            double value2 = 10 + i + random.nextInt(10);
            aaa.add(new LineChart.Data<>(value1, value2));
        }

        Platform.runLater( () -> {
            listForPoints.clear();
            listForPoints.addAll(aaa);
        });
    }

    @Override
    public void registerObserver(PolynomialObserver observer) {
        polynomialObservers.add(observer);
    }

    @Override
    public void removeObserver(PolynomialObserver observer) {
        int i = polynomialObservers.indexOf(observer);
        if( i>=0 ) {
            polynomialObservers.remove(observer);
        }
    }

    private void notifyPolynomialObservers() {
        for( PolynomialObserver observer : polynomialObservers) {
            observer.updatePolynomialText(polynomialString);
        }
    }

    private void createEventListeners() {
        listForPoints.addListener( (ListChangeListener.Change<? extends LineChart.Data<Number, Number>> e) -> {
            calculatePolynomialData();
        });

        listForPoints.addListener( (ListChangeListener.Change<? extends LineChart.Data<Number, Number>> e) -> {
            //TODO: make event handler (Maybe in another place)
        });

    }

    private void calculatePolynomialData() {

        /*Map<Number, Number> dataToLoad = listForPoints.stream().collect(
                Collectors.toMap(LineChart.Data<Number, Number>::getXValue, LineChart.Data<Number, Number>::getYValue));*/
        Map<Double, Double> polynomialPoints = new TreeMap<>();
        for (int i = 0; i < listForPoints.size(); i++) {
            polynomialPoints.put((double)listForPoints.get(i).getXValue(), (double)listForPoints.get(i).getYValue());
        }
        double[] coefficients = calculatePolynomial.loadData(polynomialPoints);
        String[] signs = new String[3];
        ArrayList<LineChart.Data<Number, Number>> plot = new ArrayList<>();
        List<Number> listOfPointsX = listForPoints.stream().map(p -> p.getXValue()).sorted().collect(Collectors.toList());

        if( listOfPointsX.size()>1 ) {
            double min = (double) listOfPointsX.get(0);
            double max = (double) listOfPointsX.get(listOfPointsX.size() - 1);
            double delta = (max - min) / 100;
            double border = delta * 10;
            for (double i = min-border; i<max+border; i=i+delta) {
                double value2 = coefficients[0] * i + coefficients[1];
                plot.add(new LineChart.Data<>(i, value2));
            }
        } else if( listOfPointsX.size() == 1 ) {
            double min = (double) listOfPointsX.get(0);
            for (double i = min-5; i < min+5; i=i+1) {
                double value2 = coefficients[0] * i + coefficients[1];
                plot.add(new LineChart.Data<>(i, value2));
            }
        }
        int i = 0;
        for( double coefficient : coefficients ) {
            signs[i] = coefficient>0 ? "+" : "-";
            i++;
        }
        if( signs[0].equals("+") ) signs[0] = " ";

        polynomialString = String.format("y = %s %.3fx %s %.3f", signs[0], Math.abs(coefficients[0]), signs[1], Math.abs(coefficients[1]));
        polynomial.set(polynomialString);
        Platform.runLater( () -> {
            listForPolynomial.clear();
            listForPolynomial.addAll(plot);
        });
        notifyPolynomialObservers();
    }

    public String getPolynomial() {
        return polynomial.get();
    }

    public StringProperty getPolynomialProperty() {
        return polynomial;
    }
}
