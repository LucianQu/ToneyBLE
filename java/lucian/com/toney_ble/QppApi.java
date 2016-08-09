package lucian.com.toney_ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by QLS on 2016/8/5.
 */
public class QppApi {
    private static String TAG = QppApi.class.getSimpleName();
    private static iQppCallback iQppCallback;
    private static ArrayList<BluetoothGattCharacteristic>
            arrayNtfCharList = new ArrayList<BluetoothGattCharacteristic>();
    private static BluetoothGattCharacteristic writeCharacteristic;//write Characteristic
    private static BluetoothGattCharacteristic notifyCharacteristic;//notify Characteristic
    private static byte notifyCharIndex;

    private static String uuidQppService;
    private static String uuidQppCharWrite;

    private static final String UUIDDes="00002902-0000-1000-8000-00805f9b34fb";
    private static final String HexStr = "0123456789abcdefABCDEF";

    private static boolean NotifyEnabled = false;
    public static boolean CheckHex = false;

    /**
     *Created at : 2016/8/8 15:36
     *Description: Callback
     */
    public static void setCallback(iQppCallback mCallback) {
        iQppCallback = mCallback;
    }

    public static void setCheckHexState(boolean state) {
        if (state)
            CheckHex = true;
        else
            CheckHex = false;
    }

    public static void updateValueForNotifition(BluetoothGatt bluetoothGatt,
                                                BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null || characteristic == null) {
            Log.e(TAG, "Invalid argument!");
            return;
        }

        String strUUIDFornotifyChar = characteristic.getUuid().toString();
        final byte[] qppData = characteristic.getValue();
        if (qppData != null && qppData.length > 0) {
            iQppCallback.onQppReceiveData(bluetoothGatt, strUUIDFornotifyChar, qppData);
        }
    }




    private static void resetQppField() {
        arrayNtfCharList.clear();
        writeCharacteristic = null;
        notifyCharacteristic = null;
        notifyCharIndex = 0;
    }

    public static boolean qppEnable(BluetoothGatt bluetoothGatt,String qppServiceUUID,String wtriteCharUUID) {
        resetQppField();
        if (qppServiceUUID != null) {
            uuidQppService = qppServiceUUID;
        }

        if (wtriteCharUUID != null) {
            uuidQppCharWrite = wtriteCharUUID;
        }

        if (bluetoothGatt ==null || qppServiceUUID.isEmpty() ||wtriteCharUUID.isEmpty()) {
            Log.e(TAG,"invalid arguments");
            return false;
        }

        BluetoothGattService qppService = bluetoothGatt.getService(UUID.fromString(qppServiceUUID));
        if (qppService == null) {
            Log.e(TAG,"qppService not found!");
            return false;
        }

        List<BluetoothGattCharacteristic> gattCharacteristics = qppService.getCharacteristics();
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic :gattCharacteristics) {
            BluetoothGattCharacteristic chara = bluetoothGattCharacteristic;
            if (chara.getUuid().toString().equals(wtriteCharUUID)) {
                writeCharacteristic = chara;
            }else if (chara.getProperties() == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                notifyCharacteristic = chara;
                arrayNtfCharList.add(chara);
            }
        }

        if (arrayNtfCharList != null && arrayNtfCharList.size() > 0) {
            if (!setCharacteristicNotification(bluetoothGatt ,arrayNtfCharList.get(0),true)) {
                return false;
            }
        }
        notifyCharIndex++;
        return true;
    }
    /**
     *
     *@author LucianQu
     *created at 2016/8/5 17:19
     */
    private static boolean setCharacteristicNotification(BluetoothGatt bluetoothGatt
            , BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (bluetoothGatt == null) {
            Log.w(TAG,"BluetoothAdapter not initialized!");
            return false;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        try {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(UUIDDes));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                return (bluetoothGatt.writeDescriptor(descriptor));
            }else {
                Log.e(TAG, "descriptor is null!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    /**
     *Created at : 2016/8/6 18:46
     *Description: 删除空格
     */
    private static byte[] deleteSpace(byte[] bytes) {
        byte[] mbytes = null;
        if(bytes == null) {
            return null;
        }
        ArrayList<Byte> arr = new ArrayList<Byte>();
        for (Byte mbyte:bytes
             ) {
            if (mbyte != 32)
                arr.add(mbyte);
        }
        if (arr.isEmpty()) {
            return null;
        }

        try {
            Iterator<Byte> it = arr.iterator();
            int i = 0;
            if (null != it) {
                mbytes = new byte[arr.size()];
            }
            while (it.hasNext()) {
                mbytes[i] = (Byte) it.next();
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mbytes;
    }

    /**
     *Created at : 2016/8/6 18:43
     *Description: 检查输入字节数组
     */
    public static boolean chenkInputString(byte[] bytes) {
        int i = 0;
        byte[] mbytes = null;
        mbytes = deleteSpace(bytes);
       // mbytes = bytes.toString().split(" ").toString().getBytes();
        if (mbytes == null)
            return false;
        Log.i("Qn Dbg", "mBytes[].length : " + mbytes.length);

       do {
           int checkChar = HexStr.indexOf(mbytes[i]);
           if (checkChar == -1)
               return false;
           i++;
       }while (i < mbytes.length);

        return true;
    }
    /**
     *Created at : 2016/8/6 19:04
     *Description:
     */
    public static boolean qppSendData(BluetoothGatt bluetoothGatt, byte[] qppData) {
        boolean ret = false;
        if (bluetoothGatt == null) {

        }
        return true;
    }


}
