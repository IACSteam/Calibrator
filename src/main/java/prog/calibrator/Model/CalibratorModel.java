package prog.calibrator.Model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
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
    private DoubleProperty amplitudeSignal = new SimpleDoubleProperty();
    private DoubleProperty maxValueSignal = new SimpleDoubleProperty();
    private DoubleProperty minValueSignal = new SimpleDoubleProperty();
    private DoubleProperty meanValueSignal = new SimpleDoubleProperty();
    private DoubleProperty rmsValueSignal = new SimpleDoubleProperty();
    private BooleanProperty enablePolynomial = new SimpleBooleanProperty();

    private Timeline animation;
    private CalibrationInterface cabinetExample = new CabinetExample();
    private ObservableList<String> listOfChannels;
    private ReceiveChannel[] receiveChannels;
    private ReceiveChannel receiveChannel;

    private double[] coefficients;

    public CalibratorModel(CalibrationInterface[] cabinets) {

        polynomial = new SimpleStringProperty("y = 1.0x + 0");
        calculatePolynomial = new CalculatePolynomial();
        initPolynomialData();
        initSignalChartData(1000);
        createEventListeners();
        getAllChannelsData();

    }

    private void initPolynomialData() {

        listForPoints = FXCollections.observableArrayList();
        listForPolynomial = FXCollections.observableArrayList();
        listForPoints.add(new LineChart.Data<>(0.0, 0.0));
        listForPolynomial.add(new LineChart.Data<>(0.0, 0.0));
        listForPoints.add(new LineChart.Data<>(1.0, 1.0));
        listForPolynomial.add(new LineChart.Data<>(1.0, 1.0));
        calculatePolynomialData();
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
                            //updatePlot();
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
        for (ReceiveChannel receiveChannel : receiveChannels) {
            listOfChannels.add(receiveChannel.getChannelName());
        }
        getChannelData(0);
    }

    private void getChannelData(int channelNumber) {

        receiveChannel = cabinetExample.getReceiveChannel(receiveChannels[channelNumber].getChannelName());
        Float[][] calibrationPoints = receiveChannel.getCalibrationPoints();
        List<LineChart.Data<Number, Number>> points = new ArrayList<>();
        for (int i = 0; i <calibrationPoints.length ; i++) {
            points.add(new LineChart.Data<Number, Number>((double)calibrationPoints[i][0], (double)calibrationPoints[i][1]));
        }
        listForPoints.clear();
        listForPoints.addAll(points);
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
        if (index >= 0) {
            listForPoints.remove(index);
        }
    }

    @Override
    public void addDataItem(Double xCoordinate, Double yCoordinate) {
        Platform.runLater(() -> {
            listForPoints.add(new LineChart.Data<>(xCoordinate, yCoordinate));
        });
        /*Float[][] points = new Float[listForPoints.size()][2];
        int i = 0;
        for(LineChart.Data<Number, Number> point : listForPoints) {
            points[i++][0] = (float)point.getXValue();
            points[i++][1] = (float)point.getXValue();
        }
        receiveChannel.setCalibrationPoints(points);*/

    }

    @Override
    public void updateDataItem(int index, Double yCoordinate) {
        LineChart.Data<Number, Number> point = listForPoints.get(index);
                point.setYValue(yCoordinate);
                calculatePolynomialData();
    }

    @Override
    public LineChart.Data<Number, Number> getDataItem(int index) {

            return listForPoints.get(index);
    }

    @Override
    public void readDataFromFile(String filePath) {

    }

    @Override
    public boolean checkIfXIsUnique(Double xCoordinate) {
        System.out.println(xCoordinate);
        return !listForPoints.stream().map(p -> p.getXValue()).anyMatch(p -> p.equals(xCoordinate));
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

        Platform.runLater(() -> {
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
        if (i >= 0) {
            polynomialObservers.remove(observer);
        }
    }

    private void notifyPolynomialObservers() {
        for (PolynomialObserver observer : polynomialObservers) {
            observer.updatePolynomialText(polynomialString);
        }
    }

    private void createEventListeners() {
        listForPoints.addListener((ListChangeListener.Change<? extends LineChart.Data<Number, Number>> e) -> {
            calculatePolynomialData();
        });

        listForPoints.addListener((ListChangeListener.Change<? extends LineChart.Data<Number, Number>> e) -> {
            //TODO: make event handler (Maybe in another place)
        });

    }

    private void calculatePolynomialData() {

        /*Map<Number, Number> dataToLoad = listForPoints.stream().collect(
                Collectors.toMap(LineChart.Data<Number, Number>::getXValue, LineChart.Data<Number, Number>::getYValue));*/
        Map<Double, Double> polynomialPoints = new TreeMap<>();
        for (int i = 0; i < listForPoints.size(); i++) {
            polynomialPoints.put((double) listForPoints.get(i).getXValue(), (double) listForPoints.get(i).getYValue());
        }
        /*double[] */coefficients = calculatePolynomial.loadData(polynomialPoints);
        String[] signs = new String[3];
        ArrayList<LineChart.Data<Number, Number>> plot = new ArrayList<>();
        List<Number> listOfPointsX = listForPoints.stream().map(p -> p.getXValue()).sorted().collect(Collectors.toList());

        if (listOfPointsX.size() > 1) {
            double min = (double) listOfPointsX.get(0);
            double max = (double) listOfPointsX.get(listOfPointsX.size() - 1);
            double delta = (max - min) / 100;
            double border = delta * 10;
            for (double i = min - border; i < max + border; i = i + delta) {
                double value2 = coefficients[0] * i + coefficients[1];
                plot.add(new LineChart.Data<>(i, value2));
            }
        } else if (listOfPointsX.size() == 1) {
            double min = (double) listOfPointsX.get(0);
            for (double i = min - 5; i < min + 5; i = i + 1) {
                double value2 = coefficients[0] * i + coefficients[1];
                plot.add(new LineChart.Data<>(i, value2));
            }
        }
        int i = 0;
        for (double coefficient : coefficients) {
            signs[i] = coefficient > 0 ? "+" : "-";
            i++;
        }
        if (signs[0].equals("+")) signs[0] = " ";

        polynomialString = String.format("y = %s %.3fx %s %.3f", signs[0], Math.abs(coefficients[0]), signs[1], Math.abs(coefficients[1]));
        polynomial.set(polynomialString);
        Platform.runLater(() -> {
            listForPolynomial.clear();
            listForPolynomial.addAll(plot);
        });
        notifyPolynomialObservers();
    }

    private void updatePlot() {

        double mean = 0, max = 0, min = 0, amplitude = 0, rms = 0;
        Float[] arr = new Float[0];
        try {
            arr = cabinetExample.getChannelData(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;

        //final ObservableList<XYChart.Data<Number, Number>> minuteList =
        //        sinusDataSeries.getData();
        Date date = new Date();
        System.out.println("before: " + date.getTime());
        for (int i = 0; i < arr.length; i++) {
            if(enablePolynomial.get()){
                arr[i] = arr[i]*(float)coefficients[0]+(float)coefficients[1];
            }
            signalChartData.get(i).setYValue(arr[i]);
            //minuteList.get(i).setXValue(i + x);
            max = max < arr[i] ? arr[i] : max;
            min = min > arr[i] ? arr[i] : min;
            mean += arr[i];
            rms += Math.pow(arr[i], 2);
        }
        mean = mean / arr.length;
        rms = Math.sqrt(rms / arr.length);
        amplitude = max - min;
        amplitudeSignal.set(amplitude);
        maxValueSignal.set( max);
        minValueSignal.set(min);
        meanValueSignal.set(mean);
        rmsValueSignal.set(rms);
        Date date1 = new Date();
        //System.out.println(amplitude +" "+max +" "+min +" "+mean +" "+rms);
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
        if (e == EventType.Data) {
            //Date date = new Date();
            //System.out.println("before: " + date.getTime());
            //System.out.println("before: " + date.getTime());
            Platform.runLater(() -> updatePlot()
            );
            //updatePlot();
        }
    }

    public DoubleProperty amplitudeSignalProperty() {
        return amplitudeSignal;
    }

    public DoubleProperty maxValueSignalProperty() {
        return maxValueSignal;
    }


    public DoubleProperty minValueSignalProperty() {
        return minValueSignal;
    }

    public DoubleProperty meanValueSignalProperty() {
        return meanValueSignal;
    }

    public DoubleProperty rmsValueSignalProperty() {
        return rmsValueSignal;
    }

    public BooleanProperty enablePolynomialProperty() {
        return enablePolynomial;
    }

    public ObservableList<String> getListOfChannels() {
        return listOfChannels;
    }
}
