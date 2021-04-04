package top.viewv.controller;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import top.viewv.api.PublishGithubGist;
import top.viewv.model.mac.SHA;
import top.viewv.model.symmetric.Decrypt;
import top.viewv.model.symmetric.DecryptProgress;
import top.viewv.model.symmetric.Encrypt;
import top.viewv.model.symmetric.EncryptProgress;
import top.viewv.model.tools.Base64Tool;
import top.viewv.model.tools.GenerateKeyPair;
import top.viewv.model.tools.GenerateSecKey;
import top.viewv.model.tools.PasswordGenerate;

import javax.crypto.SecretKey;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController implements Initializable, Deliver {

    private final SecureRandom secureRandom = new SecureRandom();
    // UI part
    public AnchorPane basePane;
    public AnchorPane paneSide;
    public AnchorPane paneMainfunction;
    public JFXButton btnOpenFile;
    public JFXToggleButton togModeChooser;
    public Label labFileAlert;
    public Label labFileinfo;
    public Label labAlgoAlert;
    public Group GroupAES;
    public Label labAlgoName;
    public Label labAlgoLength;
    public Label labAlgoMode;
    public Label labPadding;
    public Group GroupChaCha20;
    public Label labAlgoChaCha20;
    public Label labAlgoChaChaLength;
    public JFXComboBox<String> cboxAlgo;
    public JFXComboBox<Integer> cboxLength;
    public JFXComboBox<String> cboxMode;
    public JFXComboBox<String> cboxPadding;
    public JFXTextField textPassword;
    public JFXToggleButton togAead;
    public JFXTextField textAEAD;
    public Group GroupPassword;
    public JFXCheckBox chkSpecial;
    public JFXCheckBox chkUpper;
    public JFXSlider sldPasswordLength;
    public Label labProcess;
    public JFXButton btnChooseDir;
    public Label labFilepath;
    public JFXButton btnStart;
    public JFXProgressBar pbarProcess;
    public Label labFinalAlert;

    // Asymmetric part this controller is really terrible
    public AnchorPane paneAsymmetric;
    public JFXTextArea arePublic;
    public Label labPublic;
    public JFXTextArea arePrivate;
    public Label labPrivate;
    public JFXButton btnSavekey;
    public JFXButton btnGenerateKey;
    public JFXToggleButton togASMode;
    public JFXToggleButton togUpload;
    public JFXButton btnUpload;
    public JFXTextField textOauth;
    public JFXButton btnSymmetric;
    public JFXButton btnAsymmetric;
    public JFXTextArea areURL;
    public JFXButton btnOpenURL;
    public Label labASalert;
    public JFXToggleButton togASencdec;
    public JFXButton btnASencdec;

    //Data
    private String sourceFile;
    private String sourceFilename;
    private String destFilepath;
    private String algorithm;
    private String mode;
    private String padding;
    private Integer length;
    private int passwordLength = 20;
    private int numOfSpecial = 0;
    private int numOfUpper = 0;
    private long sourceFileLength = 1;

    //work mode 0 enc 1 for dec
    private int workMode = 0;
    // AS work mode
    // 0 generate public key
    // 1 enc mode
    // 2 dec mode
    private int asworkMode = 0;

    private String publicURL;

    //callback function start
    @Override
    public void deliver(long process) {
        pbarProcess.setProgress(process / sourceFileLength);
        int p = (int) (process / sourceFileLength) * 100;
        labProcess.setText(p + "%");
        System.out.println(process);
    }

    @Override
    public void reply(String message) {
        if (message.equals("Error")) {
            labFinalAlert.setText("IO Error!");
        }
        if (message.equals("IO Error")) {
            labFinalAlert.setText("IO Error");
        }
        if (message.equals("Password Error")) {
            labFinalAlert.setText("Password Error");
        }
        if (message.equals("Password Don't Match")) {
            labFinalAlert.setText("Password Don't Match");
        }
        if (message.equals("OK")) {
            labFinalAlert.setText("All Done!");

            btnStart.setDisable(false);
            labProcess.setText("S3");
            setS1state(true);
            setS2state(true);
        }
    }

    //

    // ugly start
    private void setS1state(boolean state) {
        state = !state;
        labFileinfo.setDisable(state);
        labFileAlert.setDisable(state);
        btnOpenFile.setDisable(state);
        togModeChooser.setDisable(state);
    }

    private void setS2state(boolean state) {
        state = !state;
        GroupAES.setDisable(state);
        labAlgoAlert.setDisable(state);
        cboxAlgo.setDisable(state);
        cboxMode.setDisable(state);
        cboxLength.setDisable(state);
        cboxPadding.setDisable(state);
        GroupPassword.setDisable(state);
        chkSpecial.setDisable(state);
        chkUpper.setDisable(state);
        sldPasswordLength.setDisable(state);
        textAEAD.setDisable(state);
        textPassword.setDisable(state);
        togAead.setDisable(state);
    }

    private void setS3state(boolean state) {
        state = !state;
        labProcess.setDisable(state);
        labFilepath.setDisable(state);
        pbarProcess.setDisable(state);
        btnChooseDir.setDisable(state);
        btnStart.setDisable(state);

    }
    //callback function end

    //ugly, just very ugly, really sorry for that
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setS1state(true);
        setS2state(false);
        setS3state(false);

        cboxAlgo.getItems().add("AES");
        cboxAlgo.getItems().add("ChaCha20");

        Security.addProvider(new BouncyCastleProvider());

        paneAsymmetric.setVisible(false);
        paneMainfunction.setVisible(true);
    }

    public void switchMode() {
        if (togModeChooser.isSelected()) {

            togModeChooser.setText("Decrypt");

            workMode = 1;

            setS2state(false);
            setS3state(true);

            textPassword.setDisable(false);

            btnStart.setDisable(true);
        } else {
            togModeChooser.setText("Encrypt");
            workMode = 0;
        }
    }

    public void onClickedBtnOpenFile() {
        FileChooser fileChooser = new FileChooser();

        File selectFile = fileChooser.showOpenDialog(null);

        if (selectFile != null) {
            String filename = selectFile.getName();
            String filepath = selectFile.getAbsolutePath();

            sourceFile = filepath;
            sourceFilename = filename;

            sourceFileLength = selectFile.length();

            labFileinfo.setText(filename);
            labFileAlert.setText(filepath);

            if (workMode == 0) {
                setS2state(true);

                // after choose right AEAD mode will use this part
                togAead.setVisible(false);
                textAEAD.setVisible(false);

                cboxMode.setDisable(true);
                cboxPadding.setDisable(true);
                cboxLength.setDisable(true);

                // must select right algorithm
                GroupPassword.setDisable(true);
                textPassword.setDisable(true);
                sldPasswordLength.setDisable(true);
                chkUpper.setDisable(true);
                chkSpecial.setDisable(true);

                cboxAlgo.getSelectionModel().selectFirst();
            }

        } else {
            labFileAlert.setText("Error Please Choose the right file");
        }
    }

    public void selectAlgorithm() {

        GroupChaCha20.setVisible(false);

        labAlgoName.setText("S2");
        labAlgoLength.setText("");
        labAlgoMode.setText("");
        labPadding.setText("");

        GroupAES.setVisible(true);

        cboxMode.getItems().clear();
        cboxPadding.getItems().clear();
        cboxLength.getItems().clear();

        cboxMode.setDisable(false);
        cboxPadding.setDisable(true);
        cboxLength.setDisable(true);
    }

    public void selectMode() {

        cboxMode.getItems().clear();

        algorithm = cboxAlgo.getValue();

        cboxPadding.getItems().clear();
        cboxLength.getItems().clear();

        if (algorithm.equals("ChaCha20")) {

            cboxMode.getItems().add("Poly1305");

            cboxLength.getItems().add(256);

            GroupAES.setVisible(false);
            GroupChaCha20.setVisible(true);

        } else if (algorithm.equals("AES")) {

            cboxMode.getItems().add("CBC");
            cboxMode.getItems().add("CFB");
            cboxMode.getItems().add("CTR");
            cboxMode.getItems().add("GCM");
            cboxMode.getItems().add("CCM");

            cboxLength.getItems().add(128);
            cboxLength.getItems().add(256);

            labAlgoName.setText("AES");
        }

        cboxMode.getSelectionModel().selectFirst();

        cboxPadding.setDisable(false);

    }

    public void selectPadding() {

        cboxPadding.getItems().clear();

        mode = cboxMode.getValue();

        if (algorithm.equals("AES")) {
            labAlgoMode.setText(mode);
        }

        if (mode.equals("GCM") || mode.equals("CCM") || mode.equals("Poly1305")) {
            togAead.setVisible(true);
        }

        if (algorithm.equals("ChaCha20")) {
            cboxPadding.getItems().add("Default");

        } else if (algorithm.equals("AES")) {
            if (mode.equals("CBC")) {
                cboxPadding.getItems().add("PKCS7Padding");
                cboxPadding.getItems().add("CS3Padding");
            }
            if (mode.equals("CTR")) {
                cboxPadding.getItems().add("NoPadding");
            }
            if (mode.equals("CFB")) {
                cboxPadding.getItems().add("NoPadding");
            }
            if (mode.equals("GCM")) {
                cboxPadding.getItems().add("NoPadding");
            }
            if (mode.equals("CCM")) {
                cboxPadding.getItems().add("NoPadding");
            }
        }

        cboxPadding.getSelectionModel().selectFirst();

        cboxLength.setDisable(false);
    }


    public void selectLength() {

        cboxLength.getItems().clear();

        padding = cboxPadding.getValue();

        if (algorithm.equals("AES")) {
            labPadding.setText(padding);
            cboxLength.getItems().add(128);
            cboxLength.getItems().add(256);
        } else if (algorithm.equals("ChaCha20")) {
            cboxLength.getItems().add(256);
        }

        cboxLength.getSelectionModel().selectFirst();

        textPassword.setDisable(false);
        GroupPassword.setDisable(false);
        chkSpecial.setDisable(false);
        chkUpper.setDisable(false);
        sldPasswordLength.setDisable(false);
    }


    public void setPassword() {

        if (workMode == 0) {
            length = cboxLength.getValue();
            labAlgoLength.setText(length.toString());

            setS3state(true);
            btnStart.setDisable(true);
        }
    }

    public void changeLength() {
        passwordLength = (int) sldPasswordLength.getValue();

        if (chkSpecial.isSelected()) {
            int maxNumberOfSpecial = passwordLength / 2 - 1;
            if (maxNumberOfSpecial > 0) {
                numOfSpecial = secureRandom.nextInt(maxNumberOfSpecial) + 1;
            } else {
                numOfSpecial = 1;
            }
        }
        if (chkUpper.isSelected()) {
            int maxNumberOfUpper = (passwordLength - numOfSpecial) / 2 - 1;
            if (maxNumberOfUpper > 0) {
                numOfUpper = secureRandom.nextInt(maxNumberOfUpper) + 1;
            } else {
                numOfUpper = 1;
            }
        }

        String randomPassword = PasswordGenerate.generatePassword(numOfUpper, numOfSpecial, passwordLength);

        textPassword.setText(randomPassword);
    }

    public void changeAeadMode() {
        if (togAead.isSelected()) {
            textAEAD.setVisible(true);
            textAEAD.setDisable(false);
        } else {
            textAEAD.setVisible(false);
            textAEAD.setDisable(true);
        }
    }


    public void onClickedChooseDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File selectPath = directoryChooser.showDialog(null);

        if (selectPath != null) {
            destFilepath = selectPath.getAbsolutePath();

            labFilepath.setText(destFilepath);

            btnStart.setDisable(false);
        }
    }

    public void onClickStart() {

        String password = textPassword.getText();

        if (password == null) {
            labFinalAlert.setText("Password!");
            return;
        }

        if (workMode == 0) {
            String associateData;

            if (algorithm.equals("ChaCha20") || mode.equals("GCM") || mode.equals("CCM")) {
                if (togAead.isSelected()) {
                    associateData = textAEAD.getText();
                    if (associateData == null) {
                        labFinalAlert.setText("AEAD String");
                        return;
                    }
                }
            }

            labFinalAlert.setText("");

            Encrypt encrypt = new Encrypt();
            EncryptProgress encryptProgress = new EncryptProgress(MainController.this, encrypt);
            SecretKey secretKey = GenerateSecKey.generateKey(password, length, 65566,
                    1, "AES");

            String destfile = destFilepath + File.separator + sourceFilename.substring(0, sourceFilename.lastIndexOf(".")) + ".enc";

            byte[] associatedBytes = new byte[0];

            boolean ifAEAD = false;

            if (algorithm.equals("ChaCha20") || mode.equals("GCM") || mode.equals("CCM")) {
                ifAEAD = true;
                if (togAead.isSelected()) {
                    associateData = textAEAD.getText();
                    associatedBytes = associateData.getBytes();
                }else {
                    try {
                        associatedBytes = SHA.digest(sourceFile, "3/512");
                    }catch (IOException e) {
                        labFinalAlert.setText("Source File Broken!");
                        return;
                    }
                }
            }

            if (algorithm.equals("ChaCha20")) {
                algorithm = "ChaCha20-Poly1305";
            } else if (algorithm.equals("AES")) {
                algorithm += "/" + mode;
                algorithm += "/" + padding;
            }

            boolean finalIfAEAD = ifAEAD;
            byte[] finalAssociatedBytes = associatedBytes;
            Platform.runLater(() -> encryptProgress.doEncrypt(sourceFile, sourceFilename, destfile,
                    algorithm, secretKey, finalIfAEAD, finalAssociatedBytes));

        } else {
            Decrypt decrypt = new Decrypt();
            DecryptProgress decryptProgress = new DecryptProgress(MainController.this, decrypt);
            Platform.runLater(() -> decryptProgress.doDecrypt(sourceFile, destFilepath, password));

        }
        btnStart.setDisable(true);

        System.out.println(sourceFileLength);

        setS1state(false);
        setS2state(false);
    }

    public void onClickedbtnSymmetric() {
        paneAsymmetric.setDisable(true);
        paneAsymmetric.setVisible(false);

        paneMainfunction.setDisable(false);
        paneMainfunction.setVisible(true);
    }

    public void onClickedbtnAsymmetric() {
        paneMainfunction.setDisable(true);
        paneMainfunction.setVisible(false);

        paneAsymmetric.setDisable(false);

        //pre work
        textOauth.setDisable(true);
        textOauth.setVisible(false);

        btnUpload.setDisable(true);
        btnUpload.setVisible(false);

        btnOpenURL.setDisable(true);
        btnOpenURL.setVisible(false);

        areURL.setDisable(true);
        areURL.setVisible(false);

        btnSavekey.setDisable(true);

        btnASencdec.setDisable(true);
        btnASencdec.setVisible(false);

        togASencdec.setVisible(false);

        paneAsymmetric.setVisible(true);
    }

    public void onClickedSavetofile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File selectPath = directoryChooser.showDialog(null);

        if (selectPath != null) {

            try {
                String publicKey = arePublic.getText();
                String privateKey = arePrivate.getText();

                BufferedWriter pubwriter = new BufferedWriter(new FileWriter(
                        selectPath + File.separator + "encbox-public.txt"));
                pubwriter.write(publicKey);

                BufferedWriter priwriter = new BufferedWriter(new FileWriter(
                        selectPath+File.separator+"encbox-private.txt"));

                priwriter.write(privateKey);

                pubwriter.close();
                priwriter.close();

                labASalert.setText("Save OK !");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void onClickbtnGenerate() {
        KeyPair kp = GenerateKeyPair.generate("RSA",2048);

        if (kp != null){

            arePublic.clear();
            arePrivate.clear();

            labPublic.setText("Public Key");
            labPrivate.setText("Private Key");

            Key pk = kp.getPublic();
            Key rk = kp.getPrivate();

            String publickey = Base64Tool.tobase64(pk.getEncoded());
            String privatekey = Base64Tool.tobase64(rk.getEncoded());

            arePublic.setText(publickey);
            arePrivate.setText(privatekey);

            btnSavekey.setDisable(false);
        }
    }

    public void onClickedtogUpload() {
        if (togUpload.isSelected()) {
            textOauth.setDisable(false);
            textOauth.setVisible(true);

            btnUpload.setDisable(false);
            btnUpload.setVisible(true);

            btnOpenURL.setVisible(false);
            btnOpenURL.setDisable(true);

            areURL.setVisible(false);
            areURL.setDisable(true);

        } else {
            textOauth.setVisible(false);
            textOauth.setDisable(true);

            btnUpload.setVisible(false);
            btnUpload.setDisable(true);
        }
    }

    public void onClickedUpload() {
        String oauth = textOauth.getText();
        String publickey = arePublic.getText();

        if (oauth != null && !oauth.equals("") && publickey != null && !publickey.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());

                //use date as description
                String url = PublishGithubGist.publish(oauth,
                        publickey, formatter.format(date), "Public Key", true);

                if (url != null) {
                    areURL.setDisable(false);
                    areURL.setVisible(true);

                    areURL.clear();
                    areURL.setText(url);
                    publicURL = url;

                    btnOpenURL.setDisable(false);
                    btnOpenURL.setVisible(true);

                    labASalert.setText("Upload OK !");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickedOpenURL() {
        try {
            if (publicURL != null){
                Desktop.getDesktop().browse(new URI(publicURL));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onClickedbtnASmode(MouseEvent mouseEvent) {
        if (togASMode.isSelected()){
            togASMode.setText("Crypto");

            asworkMode = 1;

            btnASencdec.setVisible(true);
            btnASencdec.setDisable(false);

            togUpload.setVisible(false);

            togASencdec.setVisible(true);

            btnGenerateKey.setDisable(true);

            labPrivate.setText("Plain Text");

        }else {
            togASMode.setText("Generate");

            asworkMode = 0;

            btnASencdec.setVisible(false);
            btnASencdec.setDisable(true);

            togUpload.setVisible(true);

            togASencdec.setVisible(false);

            btnGenerateKey.setDisable(false);

            labPublic.setText("Public Key");
            labPrivate.setText("Private Key");
        }
    }

    public void onClickedtogASencdec(MouseEvent mouseEvent) {
        if (!togASencdec.isSelected()){
            asworkMode = 1;

            togASencdec.setText("Encrypt");

            labPrivate.setText("Plain Text");
            labPublic.setText("Public Key");
        }else {
            asworkMode = 2;

            labPublic.setText("Cipher Text");

            togASencdec.setText("Decrypt");

            labPrivate.setText("Private Key");
        }
    }

    public void onClickedbtnASencdec(MouseEvent mouseEvent) {
        if (asworkMode == 1){
            String publickey = arePublic.getText();
            String plain = arePrivate.getText();

            if (publickey != null && plain != null){
                byte[] publickeyBytes = Base64Tool.tobytes(publickey);
                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA","BC");

                    X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(publickeyBytes);
                    PublicKey pk = kf.generatePublic(pkSpec);

                    byte[] cipher = top.viewv.model.asymmetric.Encrypt.encrypt(plain.getBytes() ,"RSA",pk);

                    labASalert.setText("Finish!");

                    arePrivate.setText(Base64Tool.tobase64(cipher));

                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                    labASalert.setText("Illegal Public Key");
                } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                    e.printStackTrace();
                    System.out.println("Panic!!! Provider Error!");
                }

            }
        }else if (asworkMode == 2){
            String privatekey = arePrivate.getText();
            String cipher = arePublic.getText();

            if (privatekey != null && cipher != null){
                byte [] privatekeyBytes  = Base64Tool.tobytes(privatekey);

                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA","BC");

                    PKCS8EncodedKeySpec skSpec = new PKCS8EncodedKeySpec(privatekeyBytes);
                    PrivateKey sk = kf.generatePrivate(skSpec);

                    byte[] plain = top.viewv.model.asymmetric.Decrypt.decrypt(Base64Tool.tobytes(cipher),"RSA",sk);

                    labASalert.setText("Finish");
                    arePublic.setText(new String(plain, StandardCharsets.UTF_8));

                } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                    e.printStackTrace();
                    System.out.println("Panic!!! Provider Error!");
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                    labASalert.setText("Illegal Private Key");
                }
            }
        }
    }
}


