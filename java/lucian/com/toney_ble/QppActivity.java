package lucian.com.toney_ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by QLS on 2016/7/28.
 */
public class QppActivity extends Activity{

    protected static final String TAG = QppActivity.class.getSimpleName();
    //Obtain the deviceName and deviceAddress from blueWelcomeActivity
    public  static final String EXTRAS_DEVICE_NAME = "deviceName";
    public  static final String EXTRAS_DEVICE_ADDRESS = "deviceAddress";

    private static String uuidQppService = "0003cdd0-0000-1000-8000-00805f9b0131";
    private static String uuidQppCharWrite = "0003cdd2-0000-1000-8000-00805f9b0131";

    private String deviceName;
    private String deviceAddress;

    private BluetoothManager mBluetoothManager = null;
    private static BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothGatt mBluetoothGatt = null;
    private boolean mConnected = false;
    private boolean isInitialize = false;

    private static final int MAX_DATA_SIZE = 40;

    private TextView textDeviceName;
    private TextView textDeviceAddres;
    private TextView textConnectionStatus;

    //Rx:
    private TextView textQppNotify;
    private TextView textQppDataRate;

    //Tx:
    private EditText editSend;
    private Button btnQppTextSend;
    //Repeat start
    private CheckBox checkboxRepeat;
    private CheckBox checkTransFormat;

    private TextView labelRepeatCounter;
    private TextView labelTransFormat;
    private TextView textRepeatCounter;

    private long qppRepeatCounter = 0;



    //获取BluetoothManager--->BluetoothAdapter
    private boolean initialize() {
        //For API level 18  and above ,get a reference to BluetoothAdapter through BluetoothManager
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            //使用一个getSystemService()方法,返回一个BluetootManager对象
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        //从BluetoothManager对象中获取BluetoothAdapter适配器
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }
    /**
     *
     *@author LucianQu
     *created at 2016/8/5 18:14
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w("Qn Dbg", "BluetoothAdapter not initialized or unspecified address" );
            return false;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found , Unable to connect !");
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection. Gatt :" + mBluetoothGatt);
        return true;
    }
    /**
     *discover service
     *@author LucianQu
     *created at 2016/8/5 17:52
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (QppApi.qppEnable(mBluetoothGatt, uuidQppService, uuidQppCharWrite)) {
                isInitialize = true;
                setConnectState(R.string.qpp_support);
            }else {
                isInitialize = false;
                setConnectState(R.string.qpp_not_support);
            }

        }
    };

    private void setConnectState(final int stat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textConnectionStatus.setText(stat);
            }
        });
    }

    ///////////////////////////
    /*
    * create main view
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qpp);
        getActionBar().setDisplayHomeAsUpEnabled(true);//Add back button at the upper left corner

        textDeviceName = (TextView)findViewById(R.id.text_device_name);
        textDeviceAddres = (TextView)findViewById(R.id.text_device_address);
        textConnectionStatus = (TextView)findViewById(R.id.text_deviceAddressdeviceAddression_state);

        textQppNotify = (TextView) findViewById(R.id.text_qpp_notify);
        textQppDataRate = (TextView) findViewById(R.id.text_qpp_data_rate);

        editSend = (EditText)findViewById(R.id.edit_send);
        editSend.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_DATA_SIZE)});

        checkboxRepeat = (CheckBox) findViewById(R.id.cb_repeat);
        labelRepeatCounter = (TextView) findViewById(R.id.label_repeat_counter);
        textRepeatCounter = (TextView) findViewById(R.id.text_repeat_counter);
        btnQppTextSend = (Button) findViewById(R.id.btn_qpp_text_send);

        checkTransFormat = (CheckBox) findViewById(R.id.cb_trans_format);

        deviceName = getIntent().getExtras().getString(EXTRAS_DEVICE_NAME);
        deviceAddress = getIntent().getExtras().getString(EXTRAS_DEVICE_ADDRESS);

        textDeviceName.setText(deviceName);
        textDeviceAddres.setText(deviceAddress);

        if (!initialize()) {
            Log.e(TAG,"unable to initialize Bluetooth!");
            finish();
        }
    }
    /*
    * create actionBar
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qpp,menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        }else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }
    /*
    * actionBar optionsItemSelected
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                connect(deviceAddress);
                return true;
            case R.id.menu_disconnect:
                return true;
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     *onResume discover service
     *@author LucianQu
     *created at 2016/8/5 18:09
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(!mConnected) {
            textConnectionStatus.setText(R.string.qpp_not_support);
            invalidateOptionsMenu();
            connect(deviceAddress);
        }
    }
}
