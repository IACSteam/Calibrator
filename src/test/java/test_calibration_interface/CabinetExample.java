package test_calibration_interface;

import prog.calibrator.electrical_cabinet_model.channels.ReceiveChannel;
import prog.calibrator.electrical_cabinet_model.interfaces.CalibrationInterface;
import prog.calibrator.electrical_cabinet_model.observer.EventType;
import prog.calibrator.electrical_cabinet_model.observer.Observable;

class CabinetExample extends Observable implements CalibrationInterface {
    ReceiveChannel[] channels;
    Float data [][];
    Thread thr ;

    public CabinetExample() {
        this.channels = new ReceiveChannel[2];
        this.channels[0] = new ReceiveChannel(1,0,"Current",1,1,1,3000.f,-3000.f);
        this.channels[1] = new ReceiveChannel(1,1,"Voltage",1,1,1,3000.f,-3000.f);

        this.data = new Float[2][1000];
        thr = new JThread(data,(e) -> update(e));
        thr.start();
    }

    @Override
    public ReceiveChannel[] getReceiveChannel() {
        ReceiveChannel[] clone = new ReceiveChannel[this.channels.length];
        for (int i = 0; i < this.channels.length; i++) {
            clone[i] = this.channels[i].clone();
        }
        return clone;
    }

    @Override
    public ReceiveChannel getReceiveChannel(int channelNumber) {
        for(ReceiveChannel ch: this.channels){
            if(ch.getChannelNumber() == channelNumber)
                return  ch.clone();
        }
        return null;
    }

    @Override
    public ReceiveChannel getReceiveChannel(String channelName) {
        for(ReceiveChannel ch: this.channels){
            if(ch.getChannelName() == channelName)
                return  ch.clone();
        }
        return null;
    }

    @Override
    public boolean setReceiveChannel(ReceiveChannel[] receiveChannels) {
        if(receiveChannels.length != this.channels.length) return false;
        for (int i = 0; i < this.channels.length ; i++) {
            if (this.channels[i].getChannelNumber() != receiveChannels[i].getChannelNumber())
                return false;
            this.channels[i].replace( receiveChannels[i]);
        }

        return true;
    }

    @Override
    public boolean setReceiveChannel(ReceiveChannel receiveChannel) {
        for (int i = 0; i < this.channels.length; i++) {
            if(this.channels[i].getChannelNumber() == receiveChannel.getChannelNumber()) {
                this.channels[i].replace(receiveChannel);
                return true;
            }
        }
        return false;
    }

    @Override
    public Float[] getChannelData(String channelName) {
        for(ReceiveChannel ch: this.channels){
            if(ch.getChannelName().equals(channelName))
                return this.data[ ch.getChannelNumber() ];
        }
        return null;
    }

    @Override
    public Float[] getChannelData(int channelNumber) {

        for(ReceiveChannel ch: this.channels){
            if(ch.getChannelNumber() == channelNumber)
                return this.data[ channelNumber ];
        }
        return null;
    }

    @Override
    public Thread getThread(){
        return this.thr;
    }
}

interface Handler{
    void handler(EventType e);
}

class JThread extends Thread {
    Float[][] data;
    SinusGenerator sin;
    Handler handler;

    JThread(Float data[][], Handler handler){
        this.data =  data;
        this.handler = handler;
        sin = new SinusGenerator(3600);
    }

    public void run() {

        while (TestECInterface.THREADS_STATUS) {
            for (int i = 0; i < data.length; i++) {
                data[i] = sin.getSignalData( data[i].length);
            }
            this.handler.handler(EventType.Data);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Thread has been interrupted");
            }
        }
    }
}
