package lucian.com.toney_ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by QLS on 2016/7/28.
 */
public class QppActivity extends Activity{
    protected static final String TAG = QppActivity.class.getSimpleName();//

    public  static final String EXTRAS_DEVICE_NAME = "deviceName";
    public  static final String EXTRAS_DEVICE_ADDRESS = "deviceAddress";

    private BluetoothManager mBluetoothManager = null;
    private static BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothGatt mBluetoothGatt = null;

    private TextView textDeviceName;

    private boolean mConnected = false;

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
    ///////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qpp);
       // textDeviceName = (TextView)findViewById(R.id.)

    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                return true;
            case R.id.menu_disconnect:
                return true;
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
