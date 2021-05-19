package extra;

import top.viewv.model.passwordmanager.PMSerialize;
import top.viewv.model.passwordmanager.PMStorage;

import java.io.FileNotFoundException;

public class PMTest {
    public static void main(String[] args) {
        PMStorage pmStorage = new PMStorage();

        pmStorage.password = "mainpassword";
        pmStorage.twofa = "UB6EOUFPL33SDBNPEEJGJMYOLZZX77LW";

        pmStorage.passwords.put("testfile01.enc","testpassword01");
        pmStorage.passwords.put("testfile02.enc","testpassword02");
        pmStorage.passwords.put("testfile03.enc","testpassword03");
        pmStorage.passwords.put("testfile04.enc","testpassword04");
        pmStorage.passwords.put("testfile05.enc","testpassword06");

        try {
            PMSerialize.serialize(pmStorage,"vault.ser");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
