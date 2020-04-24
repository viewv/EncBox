package top.viewv.model.mac;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SHA {
    /**
     * Get SHA of one file
     * @param mode
     *  512/224 : SHA-512/224
     *  512/226 : SHA-512/226
     *  3/256   : SHA3-256
     *  3/512   : SHA3-512
     *  default : SHA-256
     * @return
     * if error return null else return a byte[]
     * @exception IOException
     * No file founded
     *  */
    public static byte[] digest(String filepath,String mode) throws IOException {
        int buff = 16384;
        try {

            Security.addProvider(new BouncyCastleProvider());
            MessageDigest hashSum;
            RandomAccessFile file = new RandomAccessFile(filepath, "r");

            long startTime = System.nanoTime();

            hashSum = switch (mode) {
                case "512/224" -> MessageDigest.getInstance("SHA-512/224", "BC");
                case "512/226" -> MessageDigest.getInstance("SHA-512/226", "BC");
                case "3/256" -> MessageDigest.getInstance("SHA3-256", "BC");
                case "3/512" -> MessageDigest.getInstance("SHA3-512", "BC");
                default -> MessageDigest.getInstance("SHA-256", "BC");
            };

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
            System.out.println(hexString.toString());

            return partialHash;

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }
}
