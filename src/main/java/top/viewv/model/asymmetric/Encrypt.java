package top.viewv.model.asymmetric;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class Encrypt {
    public static void encrypt(String data,String algorithm,int keysize){
        Security.addProvider(new BouncyCastleProvider());

    }
}
