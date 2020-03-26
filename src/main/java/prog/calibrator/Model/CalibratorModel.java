package prog.calibrator.Model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import prog.calibrator.electrical_cabinet_model.channels.ReceiveChannel;
import prog.calibrator.electrical_cabinet_model.interfaces.CalibrationInterface;
import prog.calibrator.electrical_cabinet_model.model.CabinetExample;
import prog.calibrator.electrical_cabinet_model.observer.EventType;
import prog.calibrator.electrical_cabinet_model.observer.ObserverElectricalCabinet;


import java.util.*;
import java.util.stream.Collectors;

public class CalibratorModel implements CalibratorModelInterface, ObserverElectricalCabinet {

    private ObservableList<LineChart.Data<Number, Number>> listForPoints;
    private ObservableList<LineChart.Data<Number, Number>> listForPolynomial;
    private List<XYChart.Data<Number, Number>> signalChartData;
    private ArrayList<PolynomialObserver> polynomialObservers = new ArrayList<PolynomialObserver>();
    private CalculatePolynomial calculatePolynomial;
    private String polynomialString;
    private StringProperty polynomial;
    private Timeline animation;
    private CalibrationInterface cabinetExample = new CabinetExample();
    private ObservableList<String> listOfChannels;
    private ReceiveChannel[] receiveChannels;

    public CalibratorModel() {

        polynomial = new SimpleStringProperty("y = 1.0x + 0");
        initPolynomialData();
        initSignalChartData(1000);
        getAllChannelsData();
        calculatePolynomial = new CalculatePolynomial();
        createEventListeners();
    }

    private void initPolynomialData() {

        listForPoints = FXCollections.observableArrayList();
        listForPolynomial = FXCollections.observableArrayList();
        listForPoints.add(new LineChart.Data<>(0, 0));
        listForPolynomial.add(new LineChart.Data<>(0, 0));
        listForPoints.add(new LineChart.Data<>(1, 1));
        listForPolynomial.add(new LineChart.Data<>(1, 1));
    }

    private void initSignalChartData(int amountOfPoints) {

        signalChartData = new ArrayList<>();
        for (double m = 0; m < amountOfPoints; m++) {
            signalChartData.add(new XYChart.Data<Number, Number>(m, 10));
        }
        cabinetExample.subscribe(this);

        final KeyFrame frame =
                new KeyFrame(Duration.millis(1000 / 50),
                        (ActionEvent actionEvent) -> {
                            //Date date = new Date();
                            //System.out.println("before: " + date.getTime());
                                //nextTime();
                                updatePlot();
//
                        });
        animation = new Timeline();
        animation.getKeyFrames().add(frame);
        animation.setCycleCount(Animation.INDEFINITE);
        play();
    }

    private void getAllChannelsData() {
        listOfChannels = FXCollections.observableArrayList();
        receiveChannels = cabinetExample.getReceiveChannel();
        for(ReceiveChannel receiveChannel:receiveChannels) {
            listOfChannels.add(receiveChannel.getChannelName());
        }
    }

    public static void main(String[] args) {

    }

    @Override
    public List<XYChart.Data<Number, Number>> getSignalChartData() {
        return signalChartData;
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

    private void updatePlot() {

        Float[] arr = new Float[0];
        try {
            arr = cabinetExample.getChannelData(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //final ObservableList<XYChart.Data<Number, Number>> minuteList =
        //        sinusDataSeries.getData();
        for (int i = 0; i < 1000; i++) {
            signalChartData.get(i).setYValue(arr[i]);
            //minuteList.get(i).setXValue(i + x);
        }
    }

    public void play() {
        //animation.play();
    }

    public String getPolynomial() {
        return polynomial.get();
    }

    public StringProperty getPolynomialProperty() {
        return polynomial;
    }

    @Override
    public void notify(EventType e) {
        if(e == EventType.Data) {
            //Date date = new Date();
            //System.out.println("before: " + date.getTime());
            //System.out.println("before: " + date.getTime());
           Platform.runLater(()-> updatePlot()
           );
            updatePlot();
        }
    }

    public ObservableList<String> getListOfChannels() {
        return listOfChannels;
    }
}
