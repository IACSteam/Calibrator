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


import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CalibratorModel implements CalibratorModelInterface, ObserverElectricalCabinet {

    private ObservableList<LineChart.Data<Number, Number>> listForPoints;
    private ObservableList<LineChart.Data<Number, Number>> listForPolynomial;
    //private List<XYChart.Data<Number, Number>> signalChartData;
    private CalculatePolynomial calculatePolynomial;
    private String polynomialString;
    private StringProperty polynomial;
    private DoubleProperty amplitudeSignal = new SimpleDoubleProperty();
    private DoubleProperty maxValueSignal = new SimpleDoubleProperty();
    private DoubleProperty minValueSignal = new SimpleDoubleProperty();
    private DoubleProperty meanValueSignal = new SimpleDoubleProperty();
    private DoubleProperty rmsValueSignal = new SimpleDoubleProperty();
    private BooleanProperty enablePolynomial = new SimpleBooleanProperty();

    public ListProperty<LineChart.Data<Number, Number>> lp1 =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    public ListProperty<LineChart.Data<Number, Number>> listForPoints1 = new SimpleListProperty<>(FXCollections.observableArrayList());

    private Timeline animation;
    private CalibrationInterface cabinetExample = new CabinetExample();
    private ObservableList<String> listOfChannels;
    private ReceiveChannel[] receiveChannels;
    private ReceiveChannel receiveChannel;
    UpdateSignalData updateSignalData = new UpdateSignalData();

    private double[] coefficients;
    //private volatile Boolean newElectricalCabinetDataReady = false;
    private volatile AtomicBoolean newElectricalCabinetDataReady = new AtomicBoolean(false);

    // Add observable for list item updates.
    Callback<LineChart.Data<Number, Number>, javafx.beans.Observable[]> extractor = (LineChart.Data<Number, Number> p) -> {

        return new Observable[]{p.XValueProperty(), p.YValueProperty()};
    };
    ScheduledExecutorService updatePlotExecutor = Executors.newScheduledThreadPool(1);

    /*
     * Methods ready to work in multithreading applications
     * */
    @Override
    public void notify(EventType e) {
        if (e == EventType.Data) {
            newElectricalCabinetDataReady.set(true);
            System.out.println("notify " + System.currentTimeMillis());
        }
    }


    //*********************************************************************************
    static CalibratorModel instance = null;
    public static CalibratorModel getInstance() {

        if(instance == null) instance = new CalibratorModel();
        return instance;
    }

    private CalibratorModel() {

        polynomial = new SimpleStringProperty("y = 1.0x + 0");
        calculatePolynomial = new CalculatePolynomial();
        initPolynomialData();
        createEventListeners();
        getAllChannelsData();
        ScheduledFuture scheduledFuture = updatePlotExecutor.scheduleAtFixedRate(updateSignalData, 1000, 100, TimeUnit.MILLISECONDS);
        cabinetExample.subscribe(this);
    }

    private void initPolynomialData() {

        listForPoints = FXCollections.observableArrayList(extractor);
        listForPoints1.set(listForPoints);
        listForPolynomial = FXCollections.observableArrayList();
        listForPoints.add(new LineChart.Data<>(0.0, 0.0));
        listForPolynomial.add(new LineChart.Data<>(0.0, 0.0));
        listForPoints.add(new LineChart.Data<>(1.0, 1.0));
        listForPolynomial.add(new LineChart.Data<>(1.0, 1.0));
        //calculatePolynomialData();
//        Thread calculatePolynomialDataThread = new Thread(calculatePolynomialDataRunnable);
//        calculatePolynomialDataThread.start();
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
        return null;//signalChartData;
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
    public void addCalibrationPoint(Double xCoordinate, Double yCoordinate) {
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
    public void updateCalibrationPoint(int index, Double yCoordinate) {

            LineChart.Data<Number, Number> point = listForPoints.get(index);
            LineChart.Data<Number, Number> newPoint = new XYChart.Data<>();
            newPoint.setXValue(point.getXValue());
            newPoint.setYValue(yCoordinate);
            listForPoints.set(index, newPoint);
                    //
            //point.YValueProperty().setValue(yCoordinate);
            //listForPoints.remove(point);
            //point.setYValue(yCoordinate);
            //listForPoints.add(point);

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

        //Platform.runLater(() -> {
            listForPoints.clear();
            listForPoints.addAll(aaa);
        //});
    }


    private void createEventListeners() {

        listForPoints.addListener((ListChangeListener.Change<? extends LineChart.Data<Number, Number>> listEvent) -> {
            if (listEvent.getList().size() > 0) {
                Thread calculatePolynomialDataThread = new Thread(calculatePolynomialDataRunnable);
                calculatePolynomialDataThread.start();
            } else System.out.println("1234567");
        });

        enablePolynomial.addListener((observable, oldValue, newValue) -> {
            if(cabinetExample != null) {
                updateSignalData.calculateSignalParameters();
            }
        });
    }

    Runnable calculatePolynomialDataRunnable = () -> {

        calculatePolynomialData();
    };

    private void calculatePolynomialData() {

        try {
            Thread.sleep(500);
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




    public void play() {
        animation.play();
    }

    public String getPolynomial() {
        return polynomial.get();
    }

    public StringProperty getPolynomialProperty() {
        return polynomial;
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

//**********************************************************************************************************************
// Class that builds signal plot and calculates signal parameters
//**********************************************************************************************************************

    private class UpdateSignalData implements Runnable {

        private ObservableList<XYChart.Data<Number, Number>> signalChartDataLocalBuffer[] = new ObservableList[2];
        private int bufferNumber = 0;
        private Float[] channelDataArray;
        private AtomicBoolean GUIReady = new AtomicBoolean(true);

        public UpdateSignalData() {
            this(1000);
        }

        public UpdateSignalData(int amountOfPoints) {
            initSignalChartData(amountOfPoints);
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
        }

        private synchronized void updateSignalChartData() {

            //signalChartData = signalChartDataLocalBuffer[bufferNumber];
            bufferNumber = bufferNumber == 0 ? 1 : 0;
        }

        @Override
        public void run() {
            updatePlot();
        }

        private void updatePlot() {

            while (!GUIReady.compareAndSet(true, false)) {}
            while (!newElectricalCabinetDataReady.compareAndSet(true, false)) { } // TODO: change to Atomic or make synchronized method
            //newElectricalCabinetDataReady = false;
            synchronized (this) {
                channelDataArray = requestElectricalCabinetChannelData();
                updateSignalChartData();
                calculateSignalParameters();
            }
        }

        public synchronized void calculateSignalParameters() {

            double mean = 0.0, max = 0.0, min = 0.0, amplitude = 0.0, rms = 0.0;
            double arrayElement = 0;
            max = Double.MIN_VALUE;
            min = Double.MAX_VALUE;
            System.out.println(System.currentTimeMillis());

            for (int i = 0; i < channelDataArray.length; i++) {
                arrayElement = enablePolynomial.get() ? channelDataArray[i] * (float) coefficients[0] + (float) coefficients[1] : channelDataArray[i];
                signalChartDataLocalBuffer[bufferNumber].get(i).setYValue(arrayElement);
                max = max < arrayElement ? arrayElement : max;
                min = min > arrayElement ? arrayElement : min;
                mean += arrayElement;
                rms += Math.pow(arrayElement, 2);
            }
            mean = mean / channelDataArray.length;
            rms = Math.sqrt(rms / channelDataArray.length);
            amplitude = max - min;

            double finalAmplitude = amplitude, finalMax = max, finalMin = min, finalMean = mean, finalRMS = rms;
            Platform.runLater(() -> {
                lp1.set(signalChartDataLocalBuffer[bufferNumber]);
                amplitudeSignal.set(finalAmplitude);
                maxValueSignal.set(finalMax);
                minValueSignal.set(finalMin);
                meanValueSignal.set(finalMean);
                rmsValueSignal.set(finalRMS);
                GUIReady.set(true);
            });
        }

        private synchronized Float[] requestElectricalCabinetChannelData() {

            try {
                return cabinetExample.getChannelData(0);
            } catch (InterruptedException e) {
                return null;
            }
        }

    }
}


