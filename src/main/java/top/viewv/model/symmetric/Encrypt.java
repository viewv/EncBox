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

    public static void encrypt(String sourcefilepath, String sourcefilename, String destfile, String algorithm,
            SecretKey secretKey, Boolean ifAEAD, byte[] associatedData) {

        Security.addProvider(new BouncyCastleProvider());

        try {
            Cipher cipher = Cipher.getInstance(algorithm, "BC");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] filename = sourcefilename.getBytes();
            int filenameLength = filename.length;

            if (filenameLength > 256){
                throw new IllegalStateException("Too Long Filename! " + filenameLength);
            }

            System.out.println("Filename Length: " + filenameLength);

            int associatedDataLength = 0;

            //Hope Not a long data
            if (ifAEAD){
                associatedDataLength = associatedData.length;
                if (associatedDataLength > 256){
                    throw new IllegalStateException("Too Long associateData!: " + associatedDataLength);
                }
            }

            //Add AES GCM with associateData Support
            if (ifAEAD){
                cipher.updateAAD(associatedData);
            }

            AlgorithmParameters params = cipher.getParameters();

            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

            InputStream is = new FileInputStream(sourcefilepath + sourcefilename);

            FileOutputStream body = new FileOutputStream(destfile);

            byte head = 0b0;

            switch (algorithm) {
                case "AES/CBC/PKCS7Padding" -> head += 0b00010000;
                case "AES/CFB/NoPadding" -> head += 0b00100000;
                case "AES/CTR/NoPadding" -> head += 0b00110000;
                case "AES/CBC/CS3Padding" -> head += 0b01000000;
                case "AES/GCM/NoPadding" -> head += 0b01010000;
                case "AES/CCM/NoPadding" -> head += 0b01100000;
                default -> {
                    body.close();
                    is.close();
                    throw new IllegalStateException("Unexpected algorithm: " + algorithm);
                }
            }

            byte[] keybytes = secretKey.getEncoded();
            int keylength = keybytes.length;

            switch (keylength) {
                case 16 -> head += 0b00000100;
                case 32 -> head += 0b00001000;
                case 64 -> head += 0b00001100;
                default -> {
                    body.close();
                    is.close();
                    throw new IllegalStateException("Unexpected keylength: " + keylength);
                }
            }

            switch (iv.length) {
                case 12 -> {
                }
                case 16 -> head += 0b00000001;
                case 32 -> head += 0b00000010;
                case 64 -> head += 0b00000011;
                default -> {
                    body.close();
                    is.close();
                    throw new IllegalStateException("Unexpected IVlength: " + iv.length);
                }
            }

            body.write(head);
            body.write(iv);

            //Check IF use associate data a little bit waist space 1 byte
            if (ifAEAD){
                body.write(associatedDataLength);
                body.write(associatedData);
            }else {
                body.write(0);
            }

            CipherOutputStream out = new CipherOutputStream(body, cipher);

            out.write(filenameLength);
            out.write(filename);

            IOUtils.copyLarge(is, out);

            is.close();
            out.close();
            body.close();

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException | InvalidKeyException
                | InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Io Error!!!");
        }
    }
}
