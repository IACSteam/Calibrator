package prog.calibrator.electrical_cabinet_model.channels;

public class ReceiveChannel {
    private int electricalCabinetID;
    private int channelNumber;
    private String channelName;
    private float coeficient_1;
    private float coeficient_2;
    private float coeficient_3;
    private Float errorLinitHigh;
    private Float errorLinitLow;

    public ReceiveChannel(int electricalCabinetID, int channelNumber, String channelName, float coeficient_1,
                          float coeficient_2, float coeficient_3, Float errorLinitHigh, Float errorLinitLow) {
        this.electricalCabinetID = electricalCabinetID;
        this.channelNumber = channelNumber;
        this.channelName = channelName;
        this.coeficient_1 = coeficient_1;
        this.coeficient_2 = coeficient_2;
        this.coeficient_3 = coeficient_3;
        this.errorLinitHigh = errorLinitHigh;
        this.errorLinitLow = errorLinitLow;
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

    public float getCoeficient_1() {
        return coeficient_1;
    }

    public void setCoeficient_1(float coeficient_1) {
        this.coeficient_1 = coeficient_1;
    }

    public float getCoeficient_2() {
        return coeficient_2;
    }

    public void setCoeficient_2(float coeficient_2) {
        this.coeficient_2 = coeficient_2;
    }

    public float getCoeficient_3() {
        return coeficient_3;
    }

    public void setCoeficient_3(float coeficient_3) {
        this.coeficient_3 = coeficient_3;
    }

    public Float getErrorLinitHigh() {
        return errorLinitHigh;
    }

    public void setErrorLinitHigh(Float errorLinitHigh) {
        this.errorLinitHigh = errorLinitHigh;
    }

    public Float getErrorLinitLow() {
        return errorLinitLow;
    }

    public void setErrorLinitLow(Float errorLinitLow) {
        this.errorLinitLow = errorLinitLow;
    }

    @Override
    public ReceiveChannel clone(){
        return new ReceiveChannel( this.electricalCabinetID, this.channelNumber, this.channelName,
                this.coeficient_1, this.coeficient_2, this.coeficient_3, this.errorLinitHigh, this.errorLinitLow );
    }
    public void replace(ReceiveChannel channel){
        this.electricalCabinetID = channel.electricalCabinetID;
        this.channelNumber = channel.channelNumber;
        this.channelName = channel.channelName;
        this.coeficient_1 = channel.coeficient_1;
        this.coeficient_2 = channel.coeficient_2;
        this.coeficient_3 = channel.coeficient_3;
        this.errorLinitHigh = channel.errorLinitHigh;
        this.errorLinitLow = channel.errorLinitLow;
    }
}
