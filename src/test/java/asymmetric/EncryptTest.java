package asymmetric;

import top.viewv.model.asymmetric.Decrypt;
import top.viewv.model.asymmetric.Encrypt;
import top.viewv.model.tools.GenerateKeyPair;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.util.Base64;

public class EncryptTest {
    public static void main(String[] args) {
        KeyPair kp = GenerateKeyPair.generate("RSA",2048);

        assert kp != null : "Keypair is null!";
        Key pk = kp.getPublic();
        Key rk = kp.getPrivate();

        String data = "Hello World";
        System.out.println("Start Enc.");
        byte[] plain = data.getBytes();
        byte[] enc = Encrypt.encrypt(plain,"RSA",pk);
        String s = Base64.getEncoder().encodeToString(pk.getEncoded());
        System.out.println("Public Key: ");
        System.out.println(s);
        System.out.println("Start Dec.");
        byte[] message = Decrypt.decrypt(enc,"RSA",rk);
        System.out.println(new String(message, StandardCharsets.UTF_8));
    }
}
