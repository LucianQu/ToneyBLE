package lucian.com.toney_ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by QLS on 2016/8/5.
 */
public class QppApi {
    private static String TAG = QppApi.class.getSimpleName();
    private static ArrayList<BluetoothGattCharacteristic>
            arrayNtfCharList = new ArrayList<BluetoothGattCharacteristic>();
    private static BluetoothGattCharacteristic writeCharacteristic;//write Characteristic
    private static BluetoothGattCharacteristic notifyCharacteristic;//notify Characteristic
    private static byte notifyCharIndex;

    private static String uuidQppService;
    private static String uuidQppCharWrite;
    private static final String UUIDDes="00002902-0000-1000-8000-00805f9b34fb";

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
}
