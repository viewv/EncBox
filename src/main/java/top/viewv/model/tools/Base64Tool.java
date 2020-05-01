package top.viewv.model.tools;

import java.util.Base64;

public class Base64Tool {
    public static String tobase64(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }
    public static byte[] tobytes(String data){
        return Base64.getDecoder().decode(data);
    }
}
