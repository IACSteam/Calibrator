package prog.calibrator.Model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.util.Callback;
import javafx.util.Duration;
import prog.calibrator.electrical_cabinet_model.channels.ReceiveChannel;
import prog.calibrator.electrical_cabinet_model.interfaces.CalibrationInterface;
import prog.calibrator.electrical_cabinet_model.model.CabinetExample;
import prog.calibrator.electrical_cabinet_model.observer.EventType;
import prog.calibrator.electrical_cabinet_model.observer.ObserverElectricalCabinet;


import java.time.chrono.ThaiBuddhistEra;
import java.util.*;
import java.util.stream.Collectors;

public class CalibratorModel implements CalibratorModelInterface, ObserverElectricalCabinet {

    private ObservableList<LineChart.Data<Number, Number>> listForPoints;
    private ObservableList<LineChart.Data<Number, Number>> listForPolynomial;
    private List<XYChart.Data<Number, Number>> signalChartData;
    private CalculatePolynomial calculatePolynomial;
    private String polynomialString;
    private StringProperty polynomial;
    private DoubleProperty amplitudeSignal = new SimpleDoubleProperty();
    private DoubleProperty maxValueSignal = new SimpleDoubleProperty();
    private DoubleProperty minValueSignal = new SimpleDoubleProperty();
    private DoubleProperty meanValueSignal = new SimpleDoubleProperty();
    private DoubleProperty rmsValueSignal = new SimpleDoubleProperty();
    private BooleanProperty enablePolynomial = new SimpleBooleanProperty();

    public static ListProperty<LineChart.Data<Number, Number>> lp1 =
            new SimpleListProperty<>(FXCollections.observableArrayList());

    private Timeline animation;
    private CalibrationInterface cabinetExample = new CabinetExample();
    private ObservableList<String> listOfChannels;
    private ReceiveChannel[] receiveChannels;
    private ReceiveChannel receiveChannel;

    private ObservableList<XYChart.Data<Number, Number>> signalChartDataLocalBuffer[] = new ObservableList[2];
    private int bufferNumber = 0;
    private volatile Boolean waitData = false;

    private double[] coefficients;
    private volatile Boolean newElectricalCabinetData = false;

    Callback<LineChart.Data<Number, Number>, javafx.beans.Observable[]> extractor = (LineChart.Data<Number, Number> p) -> {

        return new Observable[]{p.XValueProperty(), p.YValueProperty()};
    };

    public CalibratorModel() {

        polynomial = new SimpleStringProperty("y = 1.0x + 0");
        calculatePolynomial = new CalculatePolynomial();
        initPolynomialData();
        initSignalChartData(1000);
        createEventListeners();
        getAllChannelsData();

    }
    /*public CalibratorModel() {
            }*/

    private void initPolynomialData() {

        listForPoints = FXCollections.observableArrayList(extractor);
        listForPolynomial = FXCollections.observableArrayList();
        listForPoints.add(new LineChart.Data<>(0.0, 0.0));
        listForPolynomial.add(new LineChart.Data<>(0.0, 0.0));
        listForPoints.add(new LineChart.Data<>(1.0, 1.0));
        listForPolynomial.add(new LineChart.Data<>(1.0, 1.0));
        //calculatePolynomialData();
        Thread calculatePolynomialDataThread = new Thread(calculatePolynomialDataRunnable);
        calculatePolynomialDataThread.start();
}

    private void updateSignalChartData() {
        signalChartData = signalChartDataLocalBuffer[bufferNumber];
        bufferNumber = bufferNumber == 0 ? 1 : 0;
    }

    private void initSignalChartData(int amountOfPoints) {

        signalChartDataLocalBuffer[0] = FXCollections.observableArrayList();
        signalChartDataLocalBuffer[1] = FXCollections.observableArrayList();
        lp1.set(signalChartDataLocalBuffer[0]);
        for (double m = 0; m < amountOfPoints; m++) {
            signalChartDataLocalBuffer[0].add(new XYChart.Data<Number, Number>(m, 10));
            signalChartDataLocalBuffer[1].add(new XYChart.Data<Number, Number>(m, 10));
        }
        updateSignalChartData();
        cabinetExample.subscribe(this);

        final KeyFrame frame =
                new KeyFrame(Duration.millis(1000 / 10),
                        (ActionEvent actionEvent) -> {
                            //Date date = new Date();
                            //System.out.println("before: " + date.getTime());
                            //nextTime();
                            //updatePlot();
                            System.out.println("qwerty " + waitData);
                            synchronized (waitData) {
                                if (!waitData) {
                                    //newElectricalCabinetData = false;
                                    Thread thread = new Thread(runnable);
                                    thread.start();
                                    //updatePlot();

                                    Date date = new Date();
                                    System.out.println("in: " + date.getTime());
                                }
                                else {
                                    Date date = new Date();
                                    System.out.println("out: " + date.getTime());
                                }
                            }
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
        for (int i = 0; i < calibrationPoints.length; i++) {
            points.add(new LineChart.Data<Number, Number>((double) calibrationPoints[i][0], (double) calibrationPoints[i][1]));
        }
        //listForPoints.clear();
        listForPoints.setAll(points);
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
        //calculatePolynomialData();
    }

    @Override
    public LineChart.Data<Number, Number> getDataItem(int index) {

        return listForPoints.get(index);
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


    private void createEventListeners() {

        listForPoints.addListener((ListChangeListener.Change<? extends LineChart.Data<Number, Number>> listEvent) -> {
            if (listEvent.getList().size() > 0) {
                Thread calculatePolynomialDataThread = new Thread(calculatePolynomialDataRunnable);
                calculatePolynomialDataThread.start();
            }
            else System.out.println("1234567");
        });
    }

    Runnable runnable = () -> {

        updatePlot();
    };

    Runnable calculatePolynomialDataRunnable = () -> {

        calculatePolynomialData();
    };

    private void calculatePolynomialData() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*Map<Number, Number> dataToLoad = listForPoints.stream().collect(
                Collectors.toMap(LineChart.Data<Number, Number>::getXValue, LineChart.Data<Number, Number>::getYValue));*/
        Map<Double, Double> polynomialPoints = new TreeMap<>();
        for (int i = 0; i < listForPoints.size(); i++) {
            polynomialPoints.put((double) listForPoints.get(i).getXValue(), (double) listForPoints.get(i).getYValue());
        }
        /*double[] */
        //coefficients = calculatePolynomial.loadData(polynomialPoints);
        coefficients = new CalculatePolynomial().loadData(polynomialPoints);
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
            listForPolynomial.setAll(plot);
            //listForPolynomial.addAll(plot);
        });
    }

    private void updatePlot() {

        System.out.println("updatePlot " + waitData);
        //synchronized (waitData) {
            System.out.println("updatePlot1 " + waitData);
            if (!waitData) {
                //System.out.println("updatePlot5 " + waitData);
                waitData = true;
                System.out.println("newElectricalCabinetData " + newElectricalCabinetData);
                while (!newElectricalCabinetData) {
                }
                newElectricalCabinetData = false;
                double mean = 0.0, max = 0.0, min = 0.0, amplitude = 0.0, rms = 0.0;
                Float[] arr = new Float[0];
                try {
                    arr = cabinetExample.getChannelData(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                max = Double.MIN_VALUE;
                min = Double.MAX_VALUE;

//        Date date = new Date();
//        System.out.println("before: " + date.getTime());

                for (int i = 0; i < arr.length; i++) {
                    arr[i] = enablePolynomial.get() ? arr[i] * (float) coefficients[0] + (float) coefficients[1] : arr[i];
                    signalChartDataLocalBuffer[bufferNumber].get(i).setYValue(arr[i]);
                    max = max < arr[i] ? arr[i] : max;
                    min = min > arr[i] ? arr[i] : min;
                    mean += arr[i];
                    rms += Math.pow(arr[i], 2);
                }
                mean = mean / arr.length;
                rms = Math.sqrt(rms / arr.length);
                amplitude = max - min;

                updateSignalChartData();
                waitData = false;
                System.out.println("asdfgh " + waitData);
                double finalAmplitude = amplitude, finalMax = max, finalMin = min, finalMean = mean, finalRMS = rms;
                Platform.runLater(() -> {
                    //signalChartData.clear();
                    //signalChartData.addAll(signalChartDataLocalBuffer[bufferNumber]);
                    lp1.set(signalChartDataLocalBuffer[bufferNumber]);
                    amplitudeSignal.set(finalAmplitude);
                    maxValueSignal.set(finalMax);
                    minValueSignal.set(finalMin);
                    meanValueSignal.set(finalMean);
                    rmsValueSignal.set(finalRMS);
                });
                //Date date1 = new Date();
                //System.out.println(amplitude +" "+max +" "+min +" "+mean +" "+rms);
            }
        //}
    }

    public void play() {
        animation.play();
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
            newElectricalCabinetData = true;
//            Thread thread = new Thread(runnable);
//            thread.start();
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
