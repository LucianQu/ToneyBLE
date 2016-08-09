package lucian.com.toney_ble;

/**
 * Created by QLS on 2016/8/9.
 */
public class HexBytesUtils {
    private static final String qppHexStr = "0123456789ABCDEF";
    

    public static String byteAscII2hex_Str(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i == bytes.length - 1) {
                if (bytes[i] < 0) {
                    stringBuilder.append(Integer.toHexString(bytes[i]).substring(6, 8));
                }else {
                    if (bytes[i] < 10 && bytes[i] >= 0)
                        stringBuilder.append("0" + Integer.toHexString(bytes[i]));
                    else
                        stringBuilder.append(Integer.toHexString(bytes[i]));
                }
            }else {
                if (bytes[i] < 0) {
                    stringBuilder.append(Integer.toHexString(bytes[i]).substring(6, 8) + " ");
                }else {
                    if (bytes[i] < 10 && bytes[i] >= 0)
                        stringBuilder.append("0" + Integer.toHexString(bytes[i]) + " ");
                    else
                        stringBuilder.append(Integer.toHexString(bytes[i]) + " ");
                }
            }
        }
        return stringBuilder.toString();
    }
}
