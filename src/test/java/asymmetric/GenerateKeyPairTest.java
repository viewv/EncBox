package asymmetric;

import top.viewv.model.tools.GenerateKeyPair;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;

public class GenerateKeyPairTest {
    public static void main(String[] args) {

        KeyPair kp =  GenerateKeyPair.generate("EC",384);

        assert kp != null;
        Key publicKey = kp.getPublic();
        Key privateKey = kp.getPrivate();

        System.out.println("Public Key:");
        String pk = String.format("%x", new BigInteger(1, publicKey.getEncoded()));
        System.out.println(pk);
        System.out.println("Public Key Length: "+publicKey.getEncoded().length);
        System.out.println("Private Key:");
        String rk = String.format("%x", new BigInteger(1, privateKey.getEncoded()));
        System.out.println(rk);
        System.out.println("Private Key Length: "+ privateKey.getEncoded().length);

        String currentPath = System.getProperty("user.dir");
        String path = currentPath + "/src/test/java/";



    }
}
