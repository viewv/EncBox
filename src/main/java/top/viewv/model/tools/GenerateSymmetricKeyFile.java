package top.viewv.model.tools;

import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GenerateSymmetricKeyFile {
    public static void generate(String destFilepath, String algorithm,int keylength,
                                SecretKey key, byte[] iv) throws FileNotFoundException {

        RandomAccessFile file = new RandomAccessFile(destFilepath, "rw");
        byte head = 0b0;
        byte[] keybyte = key.getEncoded();

        //TODO 3bit about algorithm
        switch (algorithm) {
            case "AES-CBC":
                head += 0b00100000;
                break;
            case "AEC-CFB":
                head += 0b01000000;
                break;
            case "AES-GCM":
                head += 0b01100000;
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
        //TODO Add True thing!

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
