package top.viewv.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {
    public static byte[] digest(String filepath,String mode){
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
            partialHash = new byte[hashSum.getDigestLength()];
            partialHash = hashSum.digest();

            StringBuilder hexString = new StringBuilder();

            //TODO: Fix bug, when hash is less then 10, will be no lead 0
            for (byte hash : partialHash) {
                hexString.append(Integer.toHexString(0xFF & hash));
            }

            long endTime = System.nanoTime();

            System.out.println(partialHash.length);
            System.out.println(endTime - startTime);
            System.out.println(hexString.toString());

            return partialHash;

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
