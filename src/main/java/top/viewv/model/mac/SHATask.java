package top.viewv.model.mac;
import javafx.concurrent.Task;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class SHATask extends Task<byte[]> {

    String filepath;
    String mode;

    public void setValue(String filepath,String mode){
        this.filepath = filepath;
        this.mode = mode;
    }

    @Override
    protected byte[] call() throws Exception {
        int buff = 16384;

        try {

            Security.addProvider(new BouncyCastleProvider());
            MessageDigest hashSum;
            RandomAccessFile file = new RandomAccessFile(filepath, "r");

            long startTime = System.nanoTime();

            switch (mode) {
                case "512/224":
                    hashSum =  MessageDigest.getInstance("SHA-512/224", "BC");
                    break;
                case "512/226":
                    hashSum = MessageDigest.getInstance("SHA-512/226", "BC");
                    break;
                case "3/256":
                    hashSum = MessageDigest.getInstance("SHA3-256", "BC");
                    break;
                case "3/512":
                    hashSum = MessageDigest.getInstance("SHA3-512", "BC");
                    break;
                default:
                    hashSum = MessageDigest.getInstance("SHA-256", "BC");
                    break;
            }

            byte[] buffer = new byte[buff];
            byte[] partialHash;

            long read = 0;

            // calculate the hash of the hole file for the test
            long offset = file.length();
            int unitsize;
            while (read < offset) {
                unitsize = (int) (((offset - read) >= buff) ? buff : (offset - read));
                file.read(buffer, 0, unitsize);

                hashSum.update(buffer, 0, unitsize);

                read += unitsize;
                this.updateProgress(read,offset);
            }

            file.close();
            partialHash = hashSum.digest();

            StringBuilder hexString = new StringBuilder();
            String tempHexString;

            //Fixed, really ugly!
            for (byte hash : partialHash) {
                tempHexString = Integer.toHexString(0xFF & hash);
                if (tempHexString.length() != 2)
                    tempHexString = "0" + tempHexString;
                hexString.append(tempHexString);
            }

            long endTime = System.nanoTime();

            System.out.println(partialHash.length);
            System.out.println(endTime - startTime);
            System.out.println(hexString);

            return partialHash;

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }
}
