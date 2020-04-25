package top.viewv.model.symmetric;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.*;

public class Decrypt {
    public static void decrypt(String sourcefilepath, String destfilepath, String algorithm, SecretKey secretKey,byte[] iv) throws FileNotFoundException {
        Security.addProvider(new BouncyCastleProvider());

        try {
            Cipher cipher = Cipher.getInstance(algorithm,"BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,new IvParameterSpec(iv));

            FileInputStream body = new FileInputStream(sourcefilepath);
            // TODO remember to fix ont only 16
            body.skipNBytes(16);

            CipherInputStream is = new CipherInputStream(body,cipher);
            OutputStream out = new FileOutputStream(destfilepath);

            IOUtils.copyLarge(is, out);

            is.close();
            out.close();

        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOError!!!");
        }
    }
}
