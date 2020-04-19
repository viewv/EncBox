package mactest;

import top.viewv.model.mac.SHA;

import java.io.IOException;

public class SHATest {
    public static void main(String[] args) {
        String currentPath = System.getProperty("user.dir");
        System.out.println(currentPath);
        try {
            byte[]hashSHA =  SHA.digest(currentPath+"/src/test/java/mactest/test.txt","");
            assert hashSHA != null;
            System.out.println(hashSHA.length * 8);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
