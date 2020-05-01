package symmetrictest;

import top.viewv.model.symmetric.Decrypt;
import top.viewv.model.symmetric.DecryptProgress;

public class DecryptionTest {
    public static void main(String[] args) {

        String currentPath = System.getProperty("user.dir");

        Decrypt decrypt = new Decrypt();
        DecryptProgress decryptProgress = new DecryptProgress(decrypt);

        String password = "PkUsUhh123u8";

        decryptProgress.doDecrypt(currentPath + "/src/test/java/symmetrictest/testenc.enc",
                currentPath + "/src/test/java/", password);
    }
}
