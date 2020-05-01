package top.viewv.model.symmetric;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class Decrypt {

    public static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public void decrypt(CallBack callBack, String sourcefile, String destfilepath, SecretKey secretKey) {
        Security.addProvider(new BouncyCastleProvider());

        try {

            System.out.println("Staring Decrypt!");

            FileInputStream body = new FileInputStream(sourcefile);
            byte[] heads = body.readNBytes(1);
            byte head = heads[0];

            int algoMask = head & 0b11110000;
            int keyMask = head & 0b00001100;
            int ivMask = head & 0b00000011;

            int ivLength, keyLength;

            String algorithm;

            switch (algoMask) {
                case 0b00010000 -> algorithm = "AES/CBC/PKCS7Padding";
                case 0b00100000 -> algorithm = "AES/CFB/NoPadding";
                case 0b00110000 -> algorithm = "AES/CTR/NoPadding";
                case 0b01000000 -> algorithm = "AES/CBC/CS3Padding";
                case 0b01010000 -> algorithm = "AES/GCM/NoPadding";
                case 0b01100000 -> algorithm = "AES/CCM/NoPadding";
                case 0b01110000 -> algorithm = "ChaCha20-Poly1305";
                default -> {
                    body.close();
                    throw new IllegalStateException("Unexpected AlgoMask: " + algoMask);
                }
            }

            switch (keyMask) {
                case 0b00000100 -> keyLength = 16;
                case 0b00001000 -> keyLength = 32;
                case 0b00001100 -> keyLength = 64;
                default -> {
                    body.close();
                    throw new IllegalStateException("Unexpected value: " + keyMask);
                }
            }

            switch (ivMask) {
                case 0b00000000 -> ivLength = 12;
                case 0b00000001 -> ivLength = 16;
                case 0b00000010 -> ivLength = 32;
                case 0b00000011 -> ivLength = 64;
                default -> {
                    body.close();
                    throw new IllegalStateException("Unexpected value: " + ivMask);
                }
            }

            byte[] iv = new byte[ivLength];

            body.readNBytes(iv, 0, ivLength);

            //Check whether use AEAD
            int associatedDataLength = body.readNBytes(1)[0];

            Cipher cipher = Cipher.getInstance(algorithm, "BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            if (associatedDataLength != 0) {
                byte[] associatedData = new byte[associatedDataLength];
                body.readNBytes(associatedData, 0, associatedDataLength);
                cipher.updateAAD(associatedData);
            }

            CipherInputStream is = new CipherInputStream(body, cipher);

            byte[] filenameLengthBytes = is.readNBytes(1);
            int filenameLength = filenameLengthBytes[0];

            byte[] filenameBytes = is.readNBytes(filenameLength);

            String filename = new String(filenameBytes, StandardCharsets.UTF_8);

            OutputStream out = new FileOutputStream(destfilepath + filename);

            long count = 0;
            int n;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            while (EOF != (n = is.read(buffer))) {
                out.write(buffer, 0, n);
                count += n;
                callBack.report(count);
            }

            is.close();
            out.close();
            body.close();

            System.out.println("Decryption Finish");

        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOError!!!");
        }
    }
}
