package test_calibration_interface;
import prog.calibrator.electrical_cabinet_model.interfaces.CalibrationInterface;
import prog.calibrator.electrical_cabinet_model.model.CabinetExample;
import prog.calibrator.electrical_cabinet_model.observer.EventType;
import prog.calibrator.electrical_cabinet_model.observer.ObserverElectricalCabinet;

import java.util.Scanner;

/*
 * Manual test for:
 * CalibrationInterface.java
 * EventType.java
 * Observable.java
 * ObserverElectricalCabinet.java
 */
class TestECInterface implements ObserverElectricalCabinet {
    static boolean THREADS_STATUS = true;
    CalibrationInterface cabinet = new CabinetExample();

    public static void main(String[] args) {

        TestECInterface main = new TestECInterface();
        main.run();
    }

    private void run(){
        Scanner scan = new Scanner(System.in);
        System.out.println("s - subscribe\nu - unsubscribe\ne - exit");
        char command = 0;
        while (command != 'e'){
            command = scan.next().charAt(0);
            if(command == 's'){
                System.out.println("Subscribe");
                this.cabinet.subscribe(this);
            }
            if (command == 'u'){
                System.out.println("Unsubscribe");
                this.cabinet.unsubscribe(this);
            }
        }


        TestECInterface.THREADS_STATUS = false;
        Thread t = cabinet.getThread();
        try{
            t.join();
        }
        catch(InterruptedException e){

            System.out.printf("%s has been interrupted", t.getName());
        }
        System.out.println("Thread closed!");
        scan.close();
    }

    @Override
    public void notify(EventType e) {
        try {
            System.out.println(this.cabinet.getChannelData(0)[0]);
        }catch (InterruptedException ex){
            System.out.println(e);
        }
    }
}
