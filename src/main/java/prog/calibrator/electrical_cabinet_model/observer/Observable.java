package prog.calibrator.electrical_cabinet_model.observer;

import java.util.ArrayList;

public abstract class Observable {
    ArrayList<ObserverElectricalCabinet> observers = new ArrayList<ObserverElectricalCabinet>();

    public void subscribe(ObserverElectricalCabinet observer) {
        if(this.observers.contains(observer)) return;
        this.observers.add(observer);
    }

    public void unsubscribe(ObserverElectricalCabinet observer) {
        this.observers.remove(observer);
    }

    public void update(EventType e){
        if(this.observers == null) return;
        for(ObserverElectricalCabinet ob: this.observers){
            ob.notify(e);
        }
    };
}
