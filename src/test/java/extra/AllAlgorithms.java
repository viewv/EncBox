package extra;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

public class AllAlgorithms {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        for (Provider provider: Security.getProviders()) {
            System.out.println(provider.getName());
            for (Provider.Service s: provider.getServices()){
                if (s.getType().equals("Cipher"))
                    System.out.println("\t"+s.getType()+" "+ s.getAlgorithm());
            }
        }
    }
}
