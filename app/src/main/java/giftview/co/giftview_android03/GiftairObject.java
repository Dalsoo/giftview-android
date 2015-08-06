package giftview.co.giftview_android03;

/**
 * Created by deneb on 5/18/15.
 */
public class GiftairObject {
    private String umdust;
    private String co2;
    private String tvco;

    public GiftairObject(String umdust, String co2, String tvco) {
        this.umdust = umdust;
        this.co2 = co2;
        this.tvco = tvco;
    }

    public String getUmdust() { return umdust; }
    public String getCo2() {
        return co2;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "name='" + umdust + '\'' +
                ", email='" + co2 + '\'' +
                '}';
    }
}
