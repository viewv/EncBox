package top.viewv.model.mac;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
            /* TODO change provider into an open-source one like Bouncy Castle or Commons Codec */
            MessageDigest hashSum;
            RandomAccessFile file = new RandomAccessFile(filepath, "r");

            long startTime = System.nanoTime();

            switch (mode) {
                case "512/224":
                    hashSum = MessageDigest.getInstance("SHA-512/224");
                    break;
                case "512/226":
                    hashSum = MessageDigest.getInstance("SHA-512/226");
                    break;
                case "3/256":
                    hashSum = MessageDigest.getInstance("SHA3-256");
                    break;
                case "3/512":
                    hashSum = MessageDigest.getInstance("SHA3-512");
                    break;
                default:
                    hashSum = MessageDigest.getInstance("SHA-256");
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

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
