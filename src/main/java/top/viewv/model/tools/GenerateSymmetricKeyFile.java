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
            case "AES-CBC" -> head += 0b00100000;
            case "AEC-CFB" -> head += 0b01000000;
            case "AES-GCM" -> head += 0b01100000;
        }

        switch (keylength) {
            case 128 -> head += 0b00001000;
            case 256 -> head += 0b00010000;
            case 512 -> head += 0b00011000;
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
