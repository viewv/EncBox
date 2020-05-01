package top.viewv.model.symmetric;

import javax.crypto.SecretKey;

public class EncryptProgress implements CallBack {
    private final Encrypt encrypt;

    public EncryptProgress(Encrypt encrypt){
        this.encrypt = encrypt;
    }

    public void doEncrypt(String sourcefilepath, String sourcefilename, String destfile, String algorithm,
                          SecretKey secretKey, Boolean ifAEAD, byte[] associatedData){
        new Thread(() -> encrypt.encrypt(EncryptProgress.this,sourcefilepath,sourcefilename,destfile,algorithm,secretKey,ifAEAD,associatedData)).start();
    }

    @Override
    public void report(long process){
        System.out.println("Callback Encryption Process: "+process);
    }
}
