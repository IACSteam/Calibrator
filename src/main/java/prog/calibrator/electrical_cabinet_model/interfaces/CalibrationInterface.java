package prog.calibrator.electrical_cabinet_model.interfaces;

import prog.calibrator.electrical_cabinet_model.channels.ReceiveChannel;
import prog.calibrator.electrical_cabinet_model.observer.ObserverElectricalCabinet;

public interface CalibrationInterface {
    ReceiveChannel[] getReceiveChannel();
    ReceiveChannel getReceiveChannel(int channelNumber);
    ReceiveChannel getReceiveChannel(String channelName);
    boolean setReceiveChannel(ReceiveChannel[] receiveChannels);
    boolean setReceiveChannel(ReceiveChannel receiveChannel);
    Float[] getChannelData(String channelName) throws InterruptedException;
    Float[] getChannelData(int channelNumber) throws InterruptedException;
    void subscribe(ObserverElectricalCabinet observer);
    void unsubscribe(ObserverElectricalCabinet observer);
    Thread getThread(); //will be deleted
}
