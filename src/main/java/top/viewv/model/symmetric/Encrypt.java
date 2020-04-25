package top.viewv.model.symmetric;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;

public class Encrypt {

    public static byte[] encrypt(String sourcefilepath, String destfilepath,String algorithm, SecretKey secretKey) {

        Security.addProvider(new BouncyCastleProvider());

        try {
            Cipher cipher = Cipher.getInstance(algorithm,"BC");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            AlgorithmParameters params = cipher.getParameters();

            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

            //TODO test IV remember to delete when test OK!
            System.out.println("IV" + iv.length);
            String tempHexString;
            StringBuilder hexString = new StringBuilder();
            for (byte hash : iv) {
                tempHexString = Integer.toHexString(0xFF & hash);
                if (tempHexString.length() != 2)
                    tempHexString = "0" + tempHexString;
                hexString.append(tempHexString);
            }
            System.out.println(hexString);

            InputStream is = new FileInputStream(sourcefilepath);

            FileOutputStream body = new FileOutputStream(destfilepath);
            //TODO Add more head
            body.write(iv);

            CipherOutputStream out =  new CipherOutputStream(body,cipher);

            IOUtils.copyLarge(is, out);
            is.close();
            //out.write(iv);
            out.close();

            return iv;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException | InvalidKeyException | InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Io Error!!!");
        }
        return new byte[0];
    }
}
