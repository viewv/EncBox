package top.viewv.model.symmetric;

import javafx.concurrent.Task;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import top.viewv.model.tools.GenerateSecKey;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;

public class DecryptTask extends Task<Void> {

    public static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    String sourcefile;
    long sourcefileLength;
    String destfilepath;
    String password;

    public void setValue(String sourcefile, long sourcefileLength,String destfilepath, String password){
        this.sourcefile = sourcefile;
        this.sourcefileLength = sourcefileLength;
        this.destfilepath = destfilepath;
        this.password = password;
    }

    @Override
    protected Void call() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        try {
            FileInputStream body = new FileInputStream(sourcefile);

            byte[] heads = new byte[1];
            body.readNBytes(heads,0,1);
            byte head = heads[0];

            int algoMask = head & 0b11110000;
            int keyMask = head & 0b00001100;
            int ivMask = head & 0b00000011;

            int ivLength, keyLength;

            String algorithm;

            switch (algoMask) {
                case 0b00010000:
                    algorithm = "AES/CBC/PKCS7Padding";
                    break;
                case 0b00100000:
                    algorithm = "AES/CFB/NoPadding";
                    break;
                case 0b00110000:
                    algorithm = "AES/CTR/NoPadding";
                    break;
                case 0b01000000:
                    algorithm = "AES/CBC/CS3Padding";
                    break;
                case 0b01010000:
                    algorithm = "AES/GCM/NoPadding";
                    break;
                case 0b01100000:
                    algorithm = "AES/CCM/NoPadding";
                    break;
                case 0b01110000:
                    algorithm = "ChaCha20-Poly1305";
                    break;
                default:
                    body.close();
                    throw new IllegalStateException("Unexpected AlgoMask: " + algoMask);
            }

            switch (keyMask) {
                case 0b00000100:
                    keyLength = 128;
                    break;
                case 0b00001000:
                    keyLength = 256;
                    break;
                case 0b00001100:
                    keyLength = 512;
                    break;
                default:
                    body.close();
                    throw new IllegalStateException("Unexpected value: " + keyMask);
            }

            // Temp I only use mode 1, means will not add salt and also fix interationcount
            SecretKey secretKey = GenerateSecKey.generateKey(password,keyLength,65566,
                    1,"AES");

            switch (ivMask) {
                case 0b00000000:
                    ivLength = 12;
                    break;
                case 0b00000001:
                    ivLength = 16;
                    break;
                case 0b00000010:
                    ivLength = 32;
                    break;
                case 0b00000011:
                    ivLength = 64;
                    break;
                default:
                    body.close();
                    throw new IllegalStateException("Unexpected value: " + ivMask);
            }

            byte[] iv = new byte[ivLength];
            body.readNBytes(iv, 0, ivLength);

            //Check whether use AEAD
            byte[] associatedLengthData = new byte[1];
            body.readNBytes(associatedLengthData,0,1);
            int associatedDataLength = associatedLengthData[0];

            Cipher cipher = Cipher.getInstance(algorithm, "BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            if (associatedDataLength != 0) {
                byte[] associatedData = new byte[associatedDataLength];
                body.readNBytes(associatedData, 0, associatedDataLength);
                cipher.updateAAD(associatedData);
            }

            CipherInputStream is = new CipherInputStream(body, cipher);

            byte[] filenameLengthBytes = new byte[1];
            is.readNBytes(filenameLengthBytes,0,1);
            int filenameLength = filenameLengthBytes[0];

            byte[] filenameBytes = new byte[filenameLength];
            is.readNBytes(filenameBytes, 0, filenameLength);

            String filename = new String(filenameBytes, StandardCharsets.UTF_8);

            OutputStream out = new FileOutputStream(destfilepath + File.separator + filename);

            long count = 0;
            int n;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            while (EOF != (n = is.read(buffer))) {
                out.write(buffer, 0, n);
                count += n;
                this.updateProgress(count,sourcefileLength);
            }

            is.close();
            out.close();
            body.close();

        }catch (NegativeArraySizeException e){
            e.printStackTrace();
            throw new NegativeArraySizeException("Password Don't match");
        }

        return null;
    }
}
