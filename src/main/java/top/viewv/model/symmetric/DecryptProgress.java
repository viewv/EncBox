package top.viewv.model.symmetric;

import javafx.application.Platform;
import top.viewv.controller.Deliver;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class DecryptProgress implements CallBack {
    private final Decrypt decrypt;
    private final Deliver deliver;

    public DecryptProgress(Deliver deliver, Decrypt decrypt) {
        this.decrypt = decrypt;
        this.deliver = deliver;
    }

    public void doDecrypt(String sourcefile, String destfilepath, String password) {

        Platform.runLater(() -> {
            try {
                decrypt.decrypt(DecryptProgress.this,
                        sourcefile, destfilepath, password);
            } catch (IOException e) {
                e.printStackTrace();
                deliver.reply("IO Error!");
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException |
                    NoSuchPaddingException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
                deliver.reply("Password Error!");
            }catch (NegativeArraySizeException e){
                e.printStackTrace();
                deliver.reply("Password Don't Match");
            }
        });
    }

    @Override
    public void report(long process) {
        if (process == -10) {
            deliver.reply("OK");
            return;
        }
        deliver.deliver(process);
    }
}
