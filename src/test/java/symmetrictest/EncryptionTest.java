package symmetrictest;

import top.viewv.model.mac.SHA;
// import top.viewv.model.symmetric.Encrypt;
// import top.viewv.model.symmetric.EncryptProgress;
import top.viewv.model.tools.Base64Tool;
import top.viewv.model.tools.GenerateSecKey;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Base64;

public class EncryptionTest {
    public static void main(String[] args) {
        String currentPath = System.getProperty("user.dir");

        String password = ".N9RmGoq7E6VM@";
        System.out.println(password);
        System.out.println(password.length());

        SecretKey secretKey = GenerateSecKey.generateKey(password,128,65566,
                1,"AES");

        assert secretKey != null : "key is empty!";
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println(encodedKey);
        byte[] hashcode = new byte[0];

        try {
            hashcode = SHA.digest(currentPath + "/src/test/java/symmetrictest/test.pdf" ,"3/512");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // AES/GCM/NoPadding AES/CBC/PKCS5Padding
//        Encrypt encrypt = new Encrypt();
//        EncryptProgress encryptProgress = new EncryptProgress(encrypt);
//
//        encryptProgress.doEncrypt(currentPath + "/src/test/java/symmetrictest/","test.pdf",
//                currentPath + "/src/test/java/symmetrictest/testenc.enc",
//                "AES/CCM/NoPadding",secretKey,true,hashcode);

        System.out.println("Security Key");
        System.out.println(Base64Tool.tobase64(secretKey.getEncoded()));
        System.out.println("Hash Code");
        System.out.println(Base64Tool.tobase64(hashcode));
        
    }
}
