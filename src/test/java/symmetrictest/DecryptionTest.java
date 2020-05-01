package symmetrictest;

import top.viewv.model.symmetric.Decrypt;
import top.viewv.model.symmetric.DecryptProgress;
import top.viewv.model.tools.Base64Tool;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DecryptionTest {
    public static void main(String[] args) {

        String currentPath = System.getProperty("user.dir");

        Decrypt decrypt = new Decrypt();
        DecryptProgress decryptProgress = new DecryptProgress(decrypt);

        String s = "9PGzcAz0XcL87l0i+cx9p/t9xEJytMBIKlo9vJ4AxQA=";

        byte [] secbytes = Base64Tool.tobytes(s);
        SecretKey secretKey= new SecretKeySpec(secbytes, 0, secbytes.length, "AES");

        decryptProgress.doDecrypt(currentPath + "/src/test/java/symmetrictest/testenc.enc",
                currentPath + "/src/test/java/", secretKey);
    }
}
