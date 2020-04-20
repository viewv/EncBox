package toolstest;

import top.viewv.model.tools.GenerateSecKey;
import top.viewv.model.tools.PasswordGenerate;

import javax.crypto.SecretKey;
import java.util.Base64;

public class GenerateKeyTest {
    public static void main(String[] args) {
        String password = PasswordGenerate.generatePassword(
                2,5,
                4,5,20);
        System.out.println(password);
        System.out.println(password.length());

        SecretKey secretKey = GenerateSecKey.generateKey(password,256,65566,
                                                        1,"ChaCha20-Poly1305");
        assert secretKey != null : "key is empty!";
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println(encodedKey);
    }
}
