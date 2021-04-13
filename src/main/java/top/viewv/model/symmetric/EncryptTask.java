package top.viewv.model.symmetric;

import javafx.concurrent.Task;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.Security;

public class EncryptTask extends Task<Void> {
    public static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    String sourcefilepath;
    String sourcefilename;
    long sourcefilelength;
    String destfile;
    String algorithm;
    SecretKey secretKey;
    Boolean ifAEAD;
    byte[] associatedData;

    public void setValue(String sourcefilepath, String sourcefilename, long sourcefilelength,String destfile, String algorithm,
                    SecretKey secretKey, Boolean ifAEAD, byte[] associatedData){
        this.sourcefilepath = sourcefilepath;
        this.sourcefilename = sourcefilename;
        this.sourcefilelength = sourcefilelength;
        this.destfile = destfile;
        this.algorithm = algorithm;
        this.secretKey = secretKey;
        this.ifAEAD = ifAEAD;
        this.associatedData = associatedData;
    }

    @Override
    protected Void call() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(algorithm, "BC");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] filename = sourcefilename.getBytes();
        int filenameLength = filename.length;

        if (filenameLength > 256) {
            throw new IllegalStateException("Too Long Filename! " + filenameLength);
        }

        int associatedDataLength = 0;

        //Hope Not a long data
        if (ifAEAD) {
            associatedDataLength = associatedData.length;
            if (associatedDataLength > 256) {
                throw new IllegalStateException("Too Long associateData!: " + associatedDataLength);
            }
        }

        //Add AES GCM with associateData Support
        if (ifAEAD) {
            cipher.updateAAD(associatedData);
        }

        AlgorithmParameters params = cipher.getParameters();

        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        InputStream is = new FileInputStream(sourcefilepath);

        FileOutputStream body = new FileOutputStream(destfile);

        byte head = 0b0;

        switch (algorithm) {
            case "AES/CBC/PKCS7Padding":
                head += 0b00010000;
                break;
            case "AES/CFB/NoPadding":
                head += 0b00100000;
                break;
            case "AES/CTR/NoPadding":
                head += 0b00110000;
                break;
            case "AES/CBC/CS3Padding":
                head += 0b01000000;
                break;
            case "AES/GCM/NoPadding":
                head += 0b01010000;
                break;
            case "AES/CCM/NoPadding":
                head += 0b01100000;
                break;
            case "ChaCha20-Poly1305":
                head += 0b01110000;
                break;
            default:
                body.close();
                is.close();
                throw new IllegalStateException("Unexpected algorithm: " + algorithm);
        }

        byte[] keybytes = secretKey.getEncoded();
        int keylength = keybytes.length;

        switch (keylength) {
            case 16:
                head += 0b00000100;
                break;
            case 32:
                head += 0b00001000;
                break;
            case 64:
                head += 0b00001100;
                break;
            default:
                body.close();
                is.close();
                throw new IllegalStateException("Unexpected keylength: " + keylength);
        }

        switch (iv.length) {
            case 12:
                break;
            case 16:
                head += 0b00000001;
                break;
            case 32:
                head += 0b00000010;
                break;
            case 64:
                head += 0b00000011;
                break;
            default:
                body.close();
                is.close();
                throw new IllegalStateException("Unexpected IVlength: " + iv.length);
        }

        body.write(head);
        body.write(iv);

        //Check IF use associate data a little bit waist space 1 byte
        if (ifAEAD) {
            body.write(associatedDataLength);
            body.write(associatedData);
        } else {
            body.write(0);
        }

        CipherOutputStream out = new CipherOutputStream(body, cipher);

        out.write(filenameLength);
        out.write(filename);

        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        while (EOF != (n = is.read(buffer))) {
            out.write(buffer, 0, n);
            count += n;
            this.updateProgress(count,sourcefilelength);
        }

        is.close();
        out.close();
        body.close();

        System.out.println("Encryption Finish!");

        return null;
    }

}
