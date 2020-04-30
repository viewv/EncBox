package top.viewv.model.asymmetric;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class Encrypt {
    public static byte[] encrypt(String plain, String algorithm, Key publickey){

        Security.addProvider(new BouncyCastleProvider());
        try {
            Cipher cipher = Cipher.getInstance(algorithm,"BC");
            cipher.init(Cipher.ENCRYPT_MODE, publickey);

            return cipher.doFinal(plain.getBytes());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | NoSuchProviderException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
