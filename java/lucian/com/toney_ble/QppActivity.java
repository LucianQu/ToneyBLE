package lucian.com.toney_ble;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by QLS on 2016/7/28.
 */
public class QppActivity extends Activity{
    public  static final String EXTRAS_DEVICE_NAME = "deviceName";
    public  static final String EXTRAS_DEVICE_ADDRESS = "deviceAddress";

    private boolean initialize() {
        return true;
    }
    ///////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
