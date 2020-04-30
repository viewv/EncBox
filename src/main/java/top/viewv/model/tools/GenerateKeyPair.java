package top.viewv.model.tools;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class GenerateKeyPair {
    public static KeyPair generate(String algoritm,int keysize){
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algoritm,"BC");
            kpg.initialize(keysize);

            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }
}
