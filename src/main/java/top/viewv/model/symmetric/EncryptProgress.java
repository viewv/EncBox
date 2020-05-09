package top.viewv.model.symmetric;

import javafx.application.Platform;
import top.viewv.controller.Deliver;

import javax.crypto.SecretKey;
import java.io.IOException;

public class EncryptProgress implements CallBack {
    private final Encrypt encrypt;
    private final Deliver deliver;

    public EncryptProgress(Deliver deliver,Encrypt encrypt) {
        this.encrypt = encrypt;
        this.deliver = deliver;
    }

    public void doEncrypt(String sourcefilepath, String sourcefilename, String destfile, String algorithm,
                          SecretKey secretKey, Boolean ifAEAD, byte[] associatedData) {

        Platform.runLater(()-> {
            try {
                encrypt.encrypt(EncryptProgress.this, sourcefilepath, sourcefilename, destfile,
                        algorithm, secretKey, ifAEAD, associatedData);
            } catch (IOException e) {
                e.printStackTrace();
                deliver.reply("Error");
            }
        });
//        new Thread(() -> {
//            try {
//                encrypt.encrypt(EncryptProgress.this, sourcefilepath, sourcefilename, destfile, algorithm, secretKey, ifAEAD, associatedData);
//            } catch (IOException e) {
//                deliver.reply("Error");
//            }
//        }).start();
    }

    @Override
    public void report(long process) {
        if (process == -10){
            deliver.reply("OK");
            return;
        }
        deliver.deliver(process);
    }
}
