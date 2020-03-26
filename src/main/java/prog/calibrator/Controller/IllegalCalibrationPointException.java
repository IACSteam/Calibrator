package prog.calibrator.Controller;

public class IllegalCalibrationPointException extends Exception {

    /**
     * Constructs a <code>IllegalCalibrationPointException</code> with no detail message.
     */
    public IllegalCalibrationPointException(){
        super();
    }

    /**
     * Constructs a <code>IllegalCalibrationPointException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public IllegalCalibrationPointException(String s){
            super(s);
        }
}
