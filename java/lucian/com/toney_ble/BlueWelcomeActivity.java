package lucian.com.toney_ble;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BlueWelcomeActivity extends ListActivity {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private Boolean mScanning;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;//unit:millisecond


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.title_devices);
       //getActionBar().setTitle(R.string.title_devices);
        // 通过hilde()和show()方法可以控制actionbar的隐藏和显示
        // ActionBar actionBar = getActionBar();
       // actionBar.hide();
        // actionBar.show();
        mHandler = new Handler(); //创建Handler实例对象

        /*getInfo(this);*/
        //Use this check to determine whether BLE is supported on the device .
        //Then you kan selectively disable BLE-related features
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }

        //Initializes a Bluetooth adapter,For API 18 and above ,
        //get a reference to BluetoothAdapter through BluetoothManager
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //checks if Bluetooth is supported on the device .
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        Toast.makeText(this,android.os.Build.MODEL, Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"Tony welcome to your usage!", Toast.LENGTH_LONG).show();
    }

    /**
     * 得到手机品牌
     * @return
     */
    public static String getPhoneBrand() {
        return android.os.Build.BOARD;
    }
    /**
     * 获取IMEI号，IESI号，手机型号
     */
    public static String getInfo(Context context) {
        TelephonyManager mTm = (TelephonyManager)context.
                getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String model = android.os.Build.MODEL; // 手机型号
        String brands = android.os.Build.BRAND;// 手机品牌
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        Log.i("text", "手机IMEI号："+imei  + "手机IESI号："+imsi + "手机型号：" + model
                + "手机品牌：" + brands + "手机号码"+numer );
        return null;
    }
    //create actionBar in Activity through onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);

        if(!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);


        }else {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_stop).setVisible(true);

            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

 /*处理当离开你的活动时要做的事情。最重要的是，用户做的所有改变应该在这里提交（通常ContentProvider保存数据）*/
    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BleDevice bleDevice = mLeDeviceListAdapter.getDevice(position);
        if (bleDevice == null)
            return;
        BluetoothDevice device = bleDevice.device;
        final Intent intent = new Intent(this,QppActivity.class);
        intent.putExtra(QppActivity.EXTRAS_DEVICE_NAME,device.getName());
        intent.putExtra(QppActivity.EXTRAS_DEVICE_ADDRESS,device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallBack);
            mScanning = false;
        }
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallBack);
                    invalidateOptionsMenu();
                }
            },SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallBack);
        }else {
            mBluetoothAdapter.stopLeScan(mLeScanCallBack);
            mScanning = false;
        }
        invalidateOptionsMenu();
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BleDevice> mLeDevice;//ArrayList被称为动态数组,动态的增加和减少元素,
        private LayoutInflater mInflater;   //对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater.inflate()来载
                                            //获取布局文件对象
        //声明--空参构造创建对象
        //空参构造
        public LeDeviceListAdapter() {
            super();
            mLeDevice = new ArrayList<BleDevice>();
            mInflater = BlueWelcomeActivity.this.getLayoutInflater();//将layout的xml布局文件实例化为view对象,实现动态加载布局
        }

        public void addDevice(BleDevice device) {
            BluetoothDevice dev = device.device;

            //遍历ArrayList数组,如果新增加的对象已经存在,则返回本次增加
            for (int i = 0 ; i < mLeDevice.size(); i++) {
                final BleDevice bleDevice = mLeDeviceListAdapter.getDevice(i);
                if (dev.getAddress().equalsIgnoreCase(bleDevice.device.getAddress()))
                    return;
            }
            mLeDevice.add(device);
        }

        //Obtain device
        public BleDevice getDevice(int position) {
            return mLeDevice.get(position);
        }

        public void clear() {
            mLeDevice.clear();
        }
        /////////////////////////////////////////////////////////////////////////////////
        @Override
        public Object getItem(int position) {
            return mLeDevice.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                view = mInflater.inflate(R.layout.listitem_device,null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddres = (TextView) view.findViewById(R.id.text_device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.text_device_name);
                viewHolder.rssi = (TextView) view.findViewById(R.id.text_rssi);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)view.getTag();
            }

            BleDevice BleDevice = mLeDevice.get(i);
            BluetoothDevice device = BleDevice.device;
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddres.setText(device.getAddress());
            viewHolder.rssi.setText("RSSI:" + BleDevice.rssi + "db");

            return view;
        }

        @Override
        public int getCount() {
            return mLeDevice.size();
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device,final int rssi, byte[] scanRecord) {
            final BleDevice bleDevice = new BleDevice();
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bleDevice.device = device;
                    bleDevice.rssi = rssi;
                    mLeDeviceListAdapter.addDevice(bleDevice);
                    mLeDeviceListAdapter.notifyDataSetChanged();

                }
            });
        }
    };

    static class BleDevice {
        BluetoothDevice device;
        int rssi;
    }
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddres;
        TextView rssi;
    }

}
