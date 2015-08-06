package giftview.co.giftview_android03;

/**
 * Created by parkdgun on 2015-07-10.
 */
public class GItem {

    private String title;
    private String data;

    public GItem(String title, String data) {
        super();
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
