package top.viewv.model.tools;

import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GenerateSymmetricKeyFile {
    public static void generate(String destFilepath, String algorithm, int keylength,
                                SecretKey key, byte[] iv) throws FileNotFoundException {

        RandomAccessFile file = new RandomAccessFile(destFilepath, "rw");
        byte head = 0b0;
        byte[] keybyte = key.getEncoded();

        switch (algorithm) {
            case "AES/CBC/PKCS7Padding":
                head += 0b00100000;
                break;
            case "AES/CFB/NoPadding":
                head += 0b01000000;
                break;
            case "AES/CTR/NoPadding":
                head += 0b01100000;
                break;
            case "AES/CBC/CS3Padding":
                head += 0b10000000;
                break;
            case "AES/GCM/NoPadding":
                head += 0b10100000;
                break;
            case "AES/CCM/NoPadding":
                head += 0b11000000;
                break;
        }

        switch (keylength) {
            case 128:
                head += 0b00001000;
                break;
            case 256:
                head += 0b00010000;
                break;
            case 512:
                head += 0b00011000;
                break;
        }

        int ivlength = iv.length;
        System.out.println("IV Length: " + ivlength);
        switch (ivlength) {
            case 16:
                head += 0b00000010;
                break;
            case 32:
                head += 0b00000100;
                break;
            case 64:
                head += 0b00000110;
                break;
        }

        try {
            file.writeByte(head);
            file.write(keybyte);
            file.write(iv);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
