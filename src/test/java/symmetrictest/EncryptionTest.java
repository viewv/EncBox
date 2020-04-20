package symmetrictest;

import top.viewv.model.symmetric.Decrypt;
import top.viewv.model.symmetric.Encrypt;
import top.viewv.model.tools.GenerateSecKey;
import top.viewv.model.tools.PasswordGenerate;

import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.util.Base64;

public class EncryptionTest {
    public static void main(String[] args) {
        String currentPath = System.getProperty("user.dir");

        String password = PasswordGenerate.generatePassword(
                2,5,
                4,5,20);
        System.out.println(password);
        System.out.println(password.length());

        SecretKey secretKey = GenerateSecKey.generateKey(password,256,65566,
                1,"AES");
        assert secretKey != null : "key is empty!";
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println(encodedKey);
        try {
            // AES/GCM/NoPadding AES/CBC/PKCS5Padding
            byte[] iv = Encrypt.encrypt(currentPath + "/src/test/java/symmetrictest/test.txt",
                            currentPath + "/src/test/java/symmetrictest/testenc.txt",
                            "AES/CTR/NoPadding",secretKey);
            if (iv.length == 0){
                System.out.println("Error IV lenght is zero");
            }else {
                Decrypt.decrypt(currentPath + "/src/test/java/symmetrictest/testenc.txt",
                                currentPath + "/src/test/java/symmetrictest/testdec.txt",
                                "AES/CTR/NoPadding",secretKey,iv);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
