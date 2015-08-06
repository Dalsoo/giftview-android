package giftview.co.giftview_android03;

/**
 * Created by deneb on 5/18/15.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BLEWrap {
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;

    private int mState = UART_PROFILE_DISCONNECTED;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;

    public BLEWrap() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter BLEState() {
        return mBtAdapter;
    }

    private void setBLEState() {
    }

}