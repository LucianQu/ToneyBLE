package lucian.com.toney_ble;

import android.bluetooth.BluetoothGatt;

/**
 * Created by QLS on 2016/8/8.
 */
public interface iQppCallback {
    void onQppReceiveData(BluetoothGatt mBluetoothGatt, String qppUUIDForNotifyChar, byte[] qppData);
}
