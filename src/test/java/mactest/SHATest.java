package mactest;

import top.viewv.model.mac.SHA;

public class SHATest {
    public static void main(String[] args) {
        String currentPath = System.getProperty("user.dir");
        System.out.println(currentPath);
        byte[] hashSHA =  SHA.digest(currentPath+"/src/test/java/mactest/test.txt","512/224");
        assert hashSHA != null;
        System.out.println(hashSHA.length * 8);
    }
}
