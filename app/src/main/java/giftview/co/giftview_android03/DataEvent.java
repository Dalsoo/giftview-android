package giftview.co.giftview_android03;

/**
 * Created by parkdgun on 2015-07-10.
 */
public class DataEvent {

    private String Tem;
    private String Battery;

    public DataEvent(String tem, String battery) {
        this.Tem = tem;
        this.Battery = battery;
    }


    public String getTem() {
        return Tem;
    }

    public void setTem(String tem) {
        Tem = tem;
    }

    public String getBattery() {
        return Battery;
    }

    public void setBattery(String battery) {
        Battery = battery;
    }
}
