package toolstest;

import top.viewv.model.tools.PasswordGenerate;

public class PasswordGenerateTest {
    public static void main(String[] args) {
        String password = PasswordGenerate.generatePassword(
                2,2,
                2,4,10
        );
        System.out.println(password);
        System.out.println(password.length());
    }
}
