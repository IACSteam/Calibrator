package prog.calibrator.electrical_cabinet_model.model;

import prog.calibrator.electrical_cabinet_model.channels.ReceiveChannel;
import prog.calibrator.electrical_cabinet_model.interfaces.CalibrationInterface;
import prog.calibrator.electrical_cabinet_model.observer.EventType;
import prog.calibrator.electrical_cabinet_model.observer.Observable;

import java.util.concurrent.Semaphore;

public class CabinetExample extends Observable implements CalibrationInterface {
    ReceiveChannel[] channels;
    Float data [][];
    Thread thr ;
    private Semaphore sem;

    public CabinetExample() {
        this.channels = new ReceiveChannel[2];
        Float[][] calibrationPoints = {
                {0.0f, 0.0f},
                {7.0f, 7.0f},
                {5.0f, 5.0f}
        };
        this.channels[0] = new ReceiveChannel(1, 0, "Current", 1,
                0, 0, 3000.f, -3000.f, calibrationPoints);
        this.channels[1] = new ReceiveChannel(1,1,"Voltage",1,
                0,0,3000.f,-3000.f,calibrationPoints);
        this.data = new Float[2][1000];
        this.sem = new Semaphore(1);
        thr = new JThread(data,(e) -> update(e),sem);
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
    public Float[] getChannelData(String channelName) throws InterruptedException {
        for(ReceiveChannel ch: this.channels){
            if(ch.getChannelName().equals(channelName)) {
                sem.acquire();
                int len = this.data[ch.getChannelNumber()].length;
                Float[] clone = new Float[len];
                System.arraycopy(this.data[ch.getChannelNumber()], 0, clone, 0, len);
                sem.release();
                return clone;
            }
        }
        return null;
    }

    @Override
    public Float[] getChannelData(int channelNumber) throws InterruptedException {

        for(ReceiveChannel ch: this.channels){
            if(ch.getChannelNumber() == channelNumber) {
                sem.acquire();
                int len = this.data[ch.getChannelNumber()].length;
                Float[] clone = new Float[len];
                System.arraycopy(this.data[ch.getChannelNumber()], 0, clone, 0, len);
                sem.release();
                return clone;
            }
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
    private Semaphore sem;

    JThread(Float data[][], Handler handler, Semaphore sem){
        this.data =  data;
        this.handler = handler;
        this.sem = sem;
        sin = new SinusGenerator(3600);
    }
    public void run() {
        int cnt = 5000;
        while (cnt > 0) {
            --cnt;
            try {
                sem.acquire();
                for (int i = 0; i < data.length; i++) {
                    data[i] = sin.getSignalData( data[i].length);
                }
                sem.release();
            } catch (InterruptedException e) {
//                e.printStackTrace();
                System.out.println("С семафором в потоке обновления данных при записи что-то пошло не так.");
            }
            this.handler.handler(EventType.Data);
            System.out.println("cabinet " + System.currentTimeMillis() + " cnt= " + cnt + " Thread: " + Thread.currentThread().getName());

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                System.out.println("Thread has been interrupted");
            }
        }
    }
}
