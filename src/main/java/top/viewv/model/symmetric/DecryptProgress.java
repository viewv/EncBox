package top.viewv.model.symmetric;

import javax.crypto.SecretKey;

public class DecryptProgress implements CallBack {
    private final Decrypt decrypt;

    public DecryptProgress(Decrypt decrypt){
        this.decrypt = decrypt;
    }

    public void doDecrypt(String sourcefile, String destfilepath, SecretKey secretKey){
        new Thread(() -> decrypt.decrypt(DecryptProgress.this,sourcefile,destfilepath,secretKey)).start();
    }

    @Override
    public void report(int process){
        System.out.println("Callback Decryption: "+process);
    }
}
