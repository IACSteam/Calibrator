package prog.calibrator.electrical_cabinet_model.channels;

public class ReceiveChannel {
    private int electricalCabinetID;
    private int channelNumber;
    private String channelName;
    private float coefficient1;
    private float coefficient2;
    private float coefficient3;
    private Float errorLimitHigh;
    private Float errorLimitLow;
    private Float[][] calibrationPoints;

    public ReceiveChannel(int electricalCabinetID, int channelNumber, String channelName, float coefficient1,
                          float coefficient2, float coefficient3, Float errorLimitHigh, Float errorLimitLow, Float[][] calibrationPoints) {
        this.electricalCabinetID = electricalCabinetID;
        this.channelNumber = channelNumber;
        this.channelName = channelName;
        this.coefficient1 = coefficient1;
        this.coefficient2 = coefficient2;
        this.coefficient3 = coefficient3;
        this.errorLimitHigh = errorLimitHigh;
        this.errorLimitLow = errorLimitLow;
        this.calibrationPoints = new Float[2][calibrationPoints[0].length];
        System.arraycopy(calibrationPoints[0],0,this.calibrationPoints[0],0, calibrationPoints[0].length);
        System.arraycopy(calibrationPoints[1], 0, this.calibrationPoints[1], 0, calibrationPoints[1].length);
    }

    public Float[][] getCalibrationPoints() {
        Float[][] clone = new Float[2][this.calibrationPoints[0].length];
        System.arraycopy(this.calibrationPoints[0],0, clone[0], 0, this.calibrationPoints[0].length);
        System.arraycopy(this.calibrationPoints[1],0, clone[1], 0, this.calibrationPoints[1].length);
        return clone;
    }

    public void setCalibrationPoints(Float[][] calibrationPoints) {
        System.arraycopy(calibrationPoints[0],0,this.calibrationPoints[0],0, calibrationPoints[0].length);
        System.arraycopy(calibrationPoints[1], 0, this.calibrationPoints[1], 0, calibrationPoints[1].length);

    }

    public int getElectricalCabinetID() {
        return electricalCabinetID;
    }

    public void setElectricalCabinetID(int electricalCabinetID) {
        this.electricalCabinetID = electricalCabinetID;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public float getCoefficient1() {
        return coefficient1;
    }

    public void setCoefficient1(float coefficient1) {
        this.coefficient1 = coefficient1;
    }

    public float getCoefficient2() {
        return coefficient2;
    }

    public void setCoefficient2(float coefficient2) {
        this.coefficient2 = coefficient2;
    }

    public float getCoefficient3() {
        return coefficient3;
    }

    public void setCoefficient3(float coefficient3) {
        this.coefficient3 = coefficient3;
    }

    public Float getErrorLimitHigh() {
        return errorLimitHigh;
    }

    public void setErrorLimitHigh(Float errorLimitHigh) {
        this.errorLimitHigh = errorLimitHigh;
    }

    public Float getErrorLimitLow() {
        return errorLimitLow;
    }

    public void setErrorLimitLow(Float errorLimitLow) {
        this.errorLimitLow = errorLimitLow;
    }

    @Override
    public ReceiveChannel clone(){
        return new ReceiveChannel( this.electricalCabinetID, this.channelNumber, this.channelName,
                this.coefficient1, this.coefficient2, this.coefficient3, this.errorLimitHigh, this.errorLimitLow, this.calibrationPoints);
    }
    public void replace(ReceiveChannel channel){
        this.electricalCabinetID = channel.electricalCabinetID;
        this.channelNumber = channel.channelNumber;
        this.channelName = channel.channelName;
        this.coefficient1 = channel.coefficient1;
        this.coefficient2 = channel.coefficient2;
        this.coefficient3 = channel.coefficient3;
        this.errorLimitHigh = channel.errorLimitHigh;
        this.errorLimitLow = channel.errorLimitLow;
        System.arraycopy(channel.calibrationPoints[0],0,this.calibrationPoints[0],0, calibrationPoints[0].length);
        System.arraycopy(channel.calibrationPoints[1], 0, this.calibrationPoints[1], 0, calibrationPoints[1].length);

    }
}
