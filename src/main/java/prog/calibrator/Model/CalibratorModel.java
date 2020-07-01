package prog.calibrator.Model;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.util.Callback;
import prog.calibrator.electrical_cabinet_model.channels.ReceiveChannel;
import prog.calibrator.electrical_cabinet_model.interfaces.CalibrationInterface;
import prog.calibrator.electrical_cabinet_model.observer.EventType;
import prog.calibrator.electrical_cabinet_model.observer.ObserverElectricalCabinet;


import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CalibratorModel {

    private static GUICalibratorModel guiCalibrationModelInstance = null;
    private static ArrayList<CalibrationInterface> electricalCabinetsInstances;

    public void setElectricalCabinets(CalibrationInterface[] electricalCabinets) {
        if (electricalCabinets.length == 0) return;
        electricalCabinetsInstances = new ArrayList<CalibrationInterface>(Arrays.asList(electricalCabinets));
        if (guiCalibrationModelInstance == null) getGUICalibratorModel();
        guiCalibrationModelInstance.setElectricalCabinetsInstance(electricalCabinetsInstances.get(0));
    }

    public CalibratorModelInterface getGUICalibratorModel() {
        if (guiCalibrationModelInstance == null)
            guiCalibrationModelInstance = new GUICalibratorModel();
        return guiCalibrationModelInstance;
    }

    private class GUICalibratorModel implements CalibratorModelInterface, ObserverElectricalCabinet {

        private ObservableList<LineChart.Data<Number, Number>> listForPoints;
        private ObservableList<LineChart.Data<Number, Number>> listForPolynomial;
        private StringProperty polynomial;
        private DoubleProperty amplitudeSignal = new SimpleDoubleProperty();
        private DoubleProperty maxValueSignal = new SimpleDoubleProperty();
        private DoubleProperty minValueSignal = new SimpleDoubleProperty();
        private DoubleProperty meanValueSignal = new SimpleDoubleProperty();
        private DoubleProperty rmsValueSignal = new SimpleDoubleProperty();
        private BooleanProperty enablePolynomial = new SimpleBooleanProperty();

        private ListProperty<LineChart.Data<Number, Number>> signalChartPoints =
                new SimpleListProperty<>(FXCollections.observableArrayList());

        private CalibrationInterface electricalCabinetExample;// = new CabinetExample();
        private ObservableList<String> listOfChannels;
        private ReceiveChannel[] receiveChannels;
        private ReceiveChannel receiveChannel;
        private UpdateSignalData updateSignalData = new UpdateSignalData();

        private double[] coefficients;
        //private volatile Boolean newElectricalCabinetDataReady = false;
        private volatile AtomicBoolean newElectricalCabinetDataReady = new AtomicBoolean(false);
        ScheduledFuture scheduledFuture;

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
        public void setElectricalCabinetsInstance(CalibrationInterface electricalCabinetsInstance) {
            if (electricalCabinetExample != null) {
                electricalCabinetExample.unsubscribe(this);
                scheduledFuture.cancel(true);
                newElectricalCabinetDataReady.set(false);
                while(scheduledFuture.isCancelled()) {}
            }
            if (electricalCabinetsInstance != null) {
                electricalCabinetExample = electricalCabinetsInstance;
                //cabinetExample.subscribe(this);
                scheduledFuture = updatePlotExecutor.scheduleAtFixedRate(updateSignalData, 1000, 100, TimeUnit.MILLISECONDS);
                getAllChannelsData();
            }
            else throw new NullPointerException("Объект выбранного шкафа не был создан в программе или был испорчен");
        }

        private GUICalibratorModel() {

            polynomial = new SimpleStringProperty();
            initPolynomialData();
            createEventListeners();
        }

        private void initPolynomialData() {

            listForPoints = FXCollections.observableArrayList(extractor);
            listForPolynomial = FXCollections.observableArrayList();
//            listForPoints.add(new LineChart.Data<>(0.0, 0.0));
//            listForPolynomial.add(new LineChart.Data<>(0.0, 0.0));
//            listForPoints.add(new LineChart.Data<>(1.0, 1.0));
//            listForPolynomial.add(new LineChart.Data<>(1.0, 1.0));
            //calculatePolynomialData();
//        Thread calculatePolynomialDataThread = new Thread(calculatePolynomialDataRunnable);
//        calculatePolynomialDataThread.start();
        }

        private void getAllChannelsData() {
            listOfChannels = FXCollections.observableArrayList();
            receiveChannels = electricalCabinetExample.getReceiveChannel();
            for (ReceiveChannel receiveChannel : receiveChannels) {
                listOfChannels.add(receiveChannel.getChannelName());
            }
            getChannelData(0);
        }

        private void getChannelData(int channelNumber) {

            receiveChannel = electricalCabinetExample.getReceiveChannel(receiveChannels[channelNumber].getChannelName());
            Float[][] calibrationPoints = receiveChannel.getCalibrationPoints();
            List<LineChart.Data<Number, Number>> points = new ArrayList<>();
            for (int i = 0; i < calibrationPoints.length; i++) {
                points.add(new LineChart.Data<Number, Number>((double) calibrationPoints[i][0], (double) calibrationPoints[i][1]));
            }
            listForPoints.setAll(points);
        }

        public void selectElectricalCabinetByName(String name) {
//            for(CalibrationInterface electricalCabinet : electricalCabinetsInstances) {
//                if(electricalCabinet.)
//            }
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
            LineChart.Data<Number, Number> newPoint = new XYChart.Data<>(point.getXValue(), yCoordinate);
            listForPoints.set(index, newPoint);
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

        public void startSignalReceive() {
            electricalCabinetExample.subscribe(this);
        }

        public void stopSignalReceive() {
            electricalCabinetExample.unsubscribe(this);
        }

        private void createEventListeners() {

            listForPoints.addListener((ListChangeListener.Change<? extends LineChart.Data<Number, Number>> listEvent) -> {
                Thread calculatePolynomialDataThread = new Thread(calculatePolynomialDataRunnable);
                calculatePolynomialDataThread.start();
            });

            enablePolynomial.addListener((observable, oldValue, newValue) -> {
                if (electricalCabinetExample != null) {
                    Thread calculateSignalParametersThread = new Thread(() -> {
                        updateSignalData.calculateSignalParameters();
                    });
                    calculateSignalParametersThread.setDaemon(true);
                    calculateSignalParametersThread.start();
                }
            });
        }

        Runnable calculatePolynomialDataRunnable = () -> {

            calculatePolynomialData();
            calculatePlotPointsForPolynomialCoefficients();
        };

        private void calculatePolynomialData() {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String polynomialString;
            Map<Double, Double> polynomialPoints = new TreeMap<>();
            for (int i = 0; i < listForPoints.size(); i++) {
                polynomialPoints.put((double) listForPoints.get(i).getXValue(), (double) listForPoints.get(i).getYValue());
            }
            coefficients = new CalculatePolynomial().loadData(polynomialPoints);
            String[] signs = new String[coefficients.length];
            int i = 0;
            for (double coefficient : coefficients) {
                signs[i] = coefficient > 0 ? "+" : "-";
                i++;
            }
            if (signs[0].equals("+")) signs[0] = " ";

            polynomialString = String.format("y = %s %.3fx %s %.3f", signs[0], Math.abs(coefficients[0]), signs[1], Math.abs(coefficients[1]));
            Platform.runLater(() -> {
                polynomial.set(polynomialString);
            });
        }

        private void calculatePlotPointsForPolynomialCoefficients() {

            if (coefficients == null || coefficients.length == 0 || listForPoints == null) return;
            ArrayList<LineChart.Data<Number, Number>> plot = new ArrayList<>();
            List<Number> listOfPointsX = listForPoints.stream().map(p -> p.getXValue()).sorted().collect(Collectors.toList());

            if (listOfPointsX.size() > 0) {
                double min = (double) listOfPointsX.get(0);
                double max = (double) listOfPointsX.get(listOfPointsX.size() - 1);
                double delta = (max - min) / 100;
                double border = delta * 10;
                if (min == max) {
                    border = 5;
                    delta = 1;
                }
                for (double i = min - border; i <= max + border; i = i + delta) {
                    double value = 0;
                    int step = 0;
                    for (int pow = coefficients.length - 1; pow >= 0; pow--) {
                        value += coefficients[step++] * Math.pow(i, pow);
                    }
                    plot.add(new LineChart.Data<>(i, value));
                }
            }
            if (plot.isEmpty()) {
                plot.add(new LineChart.Data<>(0, 0));
                plot.add(new LineChart.Data<>(1, 0));
            }

            Platform.runLater(() -> {
                listForPolynomial.setAll(plot);
            });
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

        public ListProperty<XYChart.Data<Number, Number>> signalChartPointsProperty() {
            return signalChartPoints;
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
                signalChartPoints.set(signalChartDataLocalBuffer[0]);
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

                while (!GUIReady.compareAndSet(true, false)) {
                }
                while (!newElectricalCabinetDataReady.compareAndSet(true, false)) {
                } // TODO: change to Atomic or make synchronized method
                //newElectricalCabinetDataReady = false;
                synchronized (this) {
                    channelDataArray = requestElectricalCabinetChannelData();
                    updateSignalChartData();
                    calculateSignalParameters();
                }
            }

            public synchronized void calculateSignalParameters() {

                if(channelDataArray==null || channelDataArray.length==0) return;
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
                    signalChartPoints.set(signalChartDataLocalBuffer[bufferNumber]);
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
                    return electricalCabinetExample.getChannelData(0);
                } catch (InterruptedException e) {
                    return null;
                }
            }

        }
    }

}


