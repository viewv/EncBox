package top.viewv.model.passwordmanager;

import java.util.HashMap;

public class PMStorage {
    public HashMap<String, String> passwords = new HashMap<>();
    public String password;
    public String uuid;
    public String twofa;
}
