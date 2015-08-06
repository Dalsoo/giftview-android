package giftview.co.giftview_android03;

/**
 * Created by deneb on 5/18/15.
 */
public class GiftairEvent {
    private GiftairObject mDataObject;

    public GiftairEvent(GiftairObject _dataObject) {
        mDataObject = _dataObject;
    }

    public GiftairObject getDataObject() {
        return mDataObject;
    }
}
