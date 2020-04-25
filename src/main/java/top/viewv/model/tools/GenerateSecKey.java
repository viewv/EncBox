package top.viewv.model.tools;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class GenerateSecKey{
    public static SecretKey generateKey(String password ,int keylength,
                                        int interationcount,int mode,String algorithm){
        try {
            Security.addProvider(new BouncyCastleProvider());
            //TODO may be not only this one secret key generator
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256","BC");

            byte[] salt;

            if (mode == 0){
                //TODO maybe we need a place to store salt, and also find out how to convert salt to string vice versa
                salt = SecureRandom.getInstanceStrong().generateSeed(16);
            }else {
                // not really safe to use a default fixed salt!
                salt = "1234567890encbox".getBytes(StandardCharsets.UTF_8);
            }

            //TODO may be we should limit the length of user-set-password's length
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, interationcount, keylength);
            SecretKey tmp = factory.generateSecret(spec);

            return new SecretKeySpec(tmp.getEncoded(), algorithm);

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}