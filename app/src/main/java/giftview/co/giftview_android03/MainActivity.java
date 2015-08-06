package giftview.co.giftview_android03;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity {

    String Tem;
    String Battery;

    public static DataEvent dataEvent;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    public static Button Login_Button, DeviceSet;
    public static TextView MailPanel, NamePanel;

    private BLEWrap blewarp;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;

    private int mState = UART_PROFILE_DISCONNECTED;

    public void onEvent(MeasurementEvent e) {
        Tem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        dataEvent = new DataEvent("","");

        blewarp = new BLEWrap();

        if (blewarp.BLEState() == null) {
            Toast.makeText(this, R.string.not_available, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Login_Button = (Button) findViewById(R.id.login_button);
        DeviceSet = (Button) findViewById(R.id.DeviceSet);
        MailPanel = (TextView) findViewById(R.id.MailPan);
        NamePanel = (TextView) findViewById(R.id.NamePan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);

        Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Button.getText().equals(getString(R.string.Login))) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
                    EventBus.getDefault().post(new LogoutEvent());
                    NamePanel.setText("Name");
                    MailPanel.setText("Guest");
                    Login_Button.setText(R.string.Login);
                }
            }
        });

        if (mDevice != null && mService != null) {
            DeviceSet.setText(getString(R.string.DetailInfo));
        } else {
            DeviceSet.setText(getString(R.string.DeviceConnect));
        }

        DeviceSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceSet.getText().equals(getString(R.string.DeviceConnect))) {
                    if (!blewarp.BLEState().isEnabled()) {
                        Log.i(TAG, "onResume - BT not enabled yet");
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    } else if (blewarp.BLEState().isEnabled()) {
                        Intent newIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), DetailInfoActivity.class));
                }
            }
        });

        service_init();

        if (mDevice != null) {
            mService.disconnect();
        }
    }

    public void Tem() {
        String message = "02064f000000";
        byte[] value;

        message = checksumcalc(message, 0);

        byte[] testv = new byte[message.length() / 2];

        for (int i = 0; i < testv.length; i++) {
            testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }
        value = testv;
        try {
            mService.writeRXCharacteristic(value);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void battery() {
        String message = "020646000000";
        byte[] value;

        message = checksumcalc(message, 0);

        byte[] testv = new byte[message.length() / 2];

        for (int i = 0; i < testv.length; i++) {
            testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }
        value = testv;
        try {
            mService.writeRXCharacteristic(value);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        menuItem.getItemId();

                        switch (menuItem.getItemId()) {
                            case R.id.App_Settings:
                                startActivity(new Intent(getApplicationContext(), AppSettingActivity.class));
                                break;
                            case R.id.App_Info:
                                startActivity(new Intent(getApplicationContext(), AppInfoActivity.class));
                                break;
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new AirFragment(), "");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(GiftairEvent.class);
        super.onDestroy();

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService = null;
    }

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();

                    }
                });
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {

                            int size = txValue.length;
                            StringBuffer strBuff = new StringBuffer();
                            StringBuffer chksum = new StringBuffer();
                            StringBuffer confirm = new StringBuffer();

                            for(int i = 0; i < size; i++) {
                                String str = String.format("%02X ", txValue[i]);
                                strBuff.append(str);
                                String con = String.format("%02x", txValue[i]);
                                confirm.append(con);
                                if(i != size - 1) {
                                    String chk = String.format("%02x", txValue[i]);
                                    chksum.append(chk);
                                }
                            }
                            String chk = chksum.toString();
                            String chk2 = checksumcalc(chk, 0);
                            String finalcon = confirm.toString();

                            Log.w(TAG, finalcon.substring(2, 8));
                            if(finalcon.substring(2, 6).equals("0742")) {
                                String text = strBuff.toString();
                                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                float temp = calc_bodytemp(finalcon);
                                String strtemp = String.format("%.2f", temp);

                                //showMessage("숫자 체온: " + Float.toString(temp));

                            }
                            else if (finalcon.substring(2, 6).equals("0746")) {
                                //showMessage("배터리 상태: " + getBatteryInfo(finalcon));
                                Battery = getBatteryInfo(finalcon);
                                dataEvent.setBattery(Battery);
                                EventBus.getDefault().post(dataEvent);
                            }
                            else if(finalcon.substring(2, 6).equals("074f")) {
                                //showMessage("문자 체온: " + stringbodytemp(finalcon));
                                Tem = stringbodytemp(finalcon);
                                dataEvent.setTem(Tem);
                                battery();
                            }
                            else if (finalcon.substring(2, 8).equals("0e4d01")) {
                                showMessage("측정주기/정상온도/정상범위/최소값/최대값: " + getTempSetting(finalcon));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
                showMessage("Device doesn't support UART. Disconnecting");
                mService.disconnect();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String getTempSetting(String msg) {
        String res = "";

        String period1 = msg.substring(10,11);
        String period2 = msg.substring(11,12);
        String normal1 = msg.substring(14,15);
        String normal2 = msg.substring(15,16);
        String normal3 = msg.substring(12,13);
        String normal4 = msg.substring(13,14);
        String range1 = msg.substring(18,19);
        String range2 = msg.substring(19,20);
        String range3 = msg.substring(16,17);
        String range4 = msg.substring(17,18);
        String min1 = msg.substring(22,23);
        String min2 = msg.substring(23,24);
        String min3 = msg.substring(20,21);
        String min4 = msg.substring(21,22);
        String max1 = msg.substring(26,27);
        String max2 = msg.substring(27,28);
        String max3 = msg.substring(24,25);
        String max4 = msg.substring(25,26);

        Log.i(TAG, "period1= " + period1);
        Log.i(TAG, "period2= " + period2);
        Log.i(TAG, "normal1= " + normal1);
        Log.i(TAG, "normal2= " + normal2);
        Log.i(TAG, "normal3= " + normal3);
        Log.i(TAG, "normal4= " + normal4);
        Log.i(TAG, "range1= " + range1);
        Log.i(TAG, "range2= " + range2);
        Log.i(TAG, "range3= " + range3);
        Log.i(TAG, "range4= " + range4);
        Log.i(TAG, "min1= " + min1);
        Log.i(TAG, "min2= " + min2);
        Log.i(TAG, "min3= " + min3);
        Log.i(TAG, "min4= " + min4);
        Log.i(TAG, "max1= " + max1);
        Log.i(TAG, "max2= " + max2);
        Log.i(TAG, "max3= " + max3);
        Log.i(TAG, "max4= " + max4);

        int period = (int) (Integer.parseInt(period1, 16) * Math.pow(16, 1) + Integer.parseInt(period2, 16) * Math.pow(16, 0));
        double normal = (float) (
                Integer.parseInt(normal1, 16) * Math.pow(16, 3) + Integer.parseInt(normal2, 16) * Math.pow(16, 2) + Integer.parseInt(normal3, 16) * Math.pow(16, 1) + Integer.parseInt(normal4, 16) * Math.pow(16, 0)
        ) * 0.01;
        double range = (float) (
                Integer.parseInt(range1, 16) * Math.pow(16, 3) + Integer.parseInt(range2, 16) * Math.pow(16, 2) + Integer.parseInt(range3, 16) * Math.pow(16, 1) + Integer.parseInt(range4, 16) * Math.pow(16, 0)
        ) * 0.01;
        double min = (float) (
                Integer.parseInt(min1, 16) * Math.pow(16, 3) + Integer.parseInt(min2, 16) * Math.pow(16, 2) + Integer.parseInt(min3, 16) * Math.pow(16, 1) + Integer.parseInt(min4, 16) * Math.pow(16, 0)
        ) * 0.01;
        double max = (float) (
                Integer.parseInt(max1, 16) * Math.pow(16, 3) + Integer.parseInt(max2, 16) * Math.pow(16, 2) + Integer.parseInt(max3, 16) * Math.pow(16, 1) + Integer.parseInt(max4, 16) * Math.pow(16, 0)
        ) * 0.01;

        res = Integer.toString(period) + "/" + String.format("%.2f", normal) + "/" + String.format("%.2f", range) + "/" + String.format("%.2f", min) + "/" + String.format("%.2f", max);

        Log.i(TAG, res);

        return res;
    }

    private String stringbodytemp(String msg) {
        int a = 0;

        String temp1 = msg.substring(12,13);
        String temp2 = msg.substring(13,14);
        String temp3 = msg.substring(10,11);
        String temp4 = msg.substring(11,12);

        Log.i(TAG, "temp1=" + temp1);
        Log.i(TAG, "temp2=" + temp2);
        Log.i(TAG, "temp3=" + temp3);
        Log.i(TAG, "temp4=" + temp4);

        a = (int) (
                Integer.parseInt(temp1, 16) * Math.pow(16, 3) + Integer.parseInt(temp2, 16) * Math.pow(16, 2) + Integer.parseInt(temp3, 16) * Math.pow(16, 1) + Integer.parseInt(temp4, 16) * Math.pow(16, 0)
        );

        String res = Integer.toString(a).substring(0, 2) + "." + Integer.toString(a).substring(2, 4);

        return res;
    }

    private String getBatteryInfo(String msg) {
        // 3v에서 shutdown
        // 0.0022
        String res = "";

        String sub1 = msg.substring(9,10);
        String sub2 = msg.substring(8,9);

        Log.i(TAG, "sub1=" + sub1);
        Log.i(TAG, "sub2=" + sub2);

        int state = (int) (Integer.parseInt(sub1, 16) * Math.pow(16, 1) + Integer.parseInt(sub2, 16) * Math.pow(16, 0));
        String s_state = "";
        if(state == 0) {
            Log.i(TAG, "state = 0");
            s_state = "충전 중 아님";
        }
        else if(state == 1) {
            Log.i(TAG, "state = 1");
            s_state = "충전 중";
        }
        else if(state == 2) {
            Log.i(TAG, "state = 2");
            s_state = "충전 완료";
        }

        String info1 = msg.substring(12,13);
        String info2 = msg.substring(13,14);
        String info3 = msg.substring(10,11);
        String info4 = msg.substring(11,12);

        Log.i(TAG, "info1=" + info1);
        Log.i(TAG, "info2=" + info2);
        Log.i(TAG, "info3=" + info3);
        Log.i(TAG, "info4=" + info4);

        double info = (float) (
                Integer.parseInt(info1, 16) * Math.pow(16, 3) + Integer.parseInt(info2, 16) * Math.pow(16, 2) + Integer.parseInt(info3, 16) * Math.pow(16, 1) + Integer.parseInt(info4, 16) * Math.pow(16, 0)
        ) * 0.0022;

        res = String.format("%.2f", info);

        return res;
    }

    private float calc_bodytemp(String msg) {
        float a = (float) 36.5;

        Log.v(TAG, msg);

        String temp1 = msg.substring(12,13);
        String temp2 = msg.substring(13,14);
        String temp3 = msg.substring(10,11);
        String temp4 = msg.substring(11,12);

        Log.i(TAG, "temp1=" + temp1);
        Log.i(TAG, "temp2=" + temp2);
        Log.i(TAG, "temp3=" + temp3);
        Log.i(TAG, "temp4=" + temp4);

        a = (float) (Integer.parseInt(temp1, 16) * Math.pow(16, 3)
                + Integer.parseInt(temp2, 16) * Math.pow(16, 2)
                + Integer.parseInt(temp3, 16) * Math.pow(16, 1)
                + Integer.parseInt(temp4, 16) * Math.pow(16, 0));

        float res = (float) (a * 0.02 - 273.15);

        return res;
    }

    public String checksumcalc(String str, int flag) {
        String result = "";

        String sums = str.substring(2, str.length());
        System.out.println(sums);

        int checksumres = 0;
        for(int i = 0; i < sums.length() / 2; i++) {
            checksumres += Integer.parseInt(sums.substring(i * 2, i * 2 + 2), 16);
        }
        System.out.printf("%02X\n", checksumres);

        if(checksumres < 0x100) {
            result = String.format("%02x", checksumres);
            System.out.println("checksum : " + result);
        } else {
            result = String.format("%02x", checksumres);
            String rescarry = result.substring(0, 1);
            String resborrow = result.substring(1, 3);

            int carrysum = Integer.parseInt(rescarry, 16) + Integer.parseInt(resborrow, 16);
            result = String.format("%02x", carrysum);
            System.out.println("checksum : " + result);
        }
        if(flag == 0)
            str = str.concat(result);
        else if(flag == 1)
            str = result;
        return str;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    Log.d(TAG, "...onActivityResultdevice.address = " + mDevice + "mserviceValue" + mService);
                    mService.connect(deviceAddress);
                    startActivity(new Intent(getApplicationContext(), DeviceEnrollActivity.class));
                    DeviceSet.setText(getString(R.string.DetailInfo));
                } else {
                    DeviceSet.setText(getString(R.string.DeviceConnect));
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    //Toast.makeText(this, R.string.bluetoothonString, Toast.LENGTH_SHORT).show();
                    Intent newIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                    startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bluetoothproString, Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                Log.i(TAG, data.getStringExtra("period").toString());
                Log.i(TAG, data.getStringExtra("normal").toString());
                Log.i(TAG, data.getStringExtra("range").toString());
                Log.i(TAG, data.getStringExtra("min").toString());
                Log.i(TAG, data.getStringExtra("max").toString());

                String strPeriod = String.format("%02x", Integer.parseInt(data.getStringExtra("period").toString()));
                String strNormal = String.format("%04x", Integer.parseInt(data.getStringExtra("normal").toString()));
                String strRange = String.format("%04x", Integer.parseInt(data.getStringExtra("range").toString()));
                String strMin = String.format("%04x", Integer.parseInt(data.getStringExtra("min").toString()));
                String strMax = String.format("%04x", Integer.parseInt(data.getStringExtra("max").toString()));

                String message = "020e4d0000" + strPeriod + strNormal + strRange + strMin + strMax;

                Log.i(TAG, message);

                byte[] value;

                message = checksumcalc(message, 0);

                byte[] testv = new byte[message.length() / 2];

                for(int i = 0; i < testv.length; i++) {
                    testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
                }
                value = testv;
                try {
                    mService.writeRXCharacteristic(value);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }
}
