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
import top.viewv.model.mac.SHA;
import top.viewv.model.symmetric.Encrypt;
import top.viewv.model.symmetric.EncryptProgress;
import top.viewv.model.tools.GenerateSecKey;
import top.viewv.model.tools.PasswordGenerate;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ResourceBundle;

public class MainController implements Initializable,Deliver {

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

    private SecureRandom secureRandom = new SecureRandom();

    private long sourceFileLength = 1;

    @Override
    public void deliver(long process){
        pbarProcess.setProgress(process / sourceFileLength);
        int p = (int) (process/sourceFileLength) * 100;
        labProcess.setText(p + "%");
    }

    @Override
    public void reply(String message){
        if (message.equals("Error")){
            labFinalAlert.setText("IO Error!");
        }
        if (message.equals("OK")){
            labFinalAlert.setText("All Done!");

            btnStart.setDisable(false);
            labProcess.setText("S3");
            setS1state(true);
            setS2state(true);
        }
    }

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
    //ugly end just very ugly, really sorry foe that

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setS1state(true);
        setS2state(false);
        setS3state(false);

        cboxAlgo.getItems().add("AES");
        cboxAlgo.getItems().add("ChaCha20");

        Security.addProvider(new BouncyCastleProvider());
    }

    public void onClickedBtnOpenFile(MouseEvent mouseEvent) {
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

    public void selectMode(MouseEvent mouseEvent) {

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


    public void selectLength(MouseEvent mouseEvent) {

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


    public void setPassword(MouseEvent mouseEvent) {

        length = cboxLength.getValue();
        labAlgoLength.setText(length.toString());

        setS3state(true);
        btnStart.setDisable(true);
    }

    public void changeLength(MouseEvent mouseEvent) {
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

    public void changeAeadMode(MouseEvent mouseEvent) {
        if (togAead.isSelected()) {
            textAEAD.setVisible(true);
            textAEAD.setDisable(false);
        } else {
            textAEAD.setVisible(false);
            textAEAD.setDisable(true);
        }
    }


    public void onClickedChooseDir(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File selectPath = directoryChooser.showDialog(null);

        if (selectPath != null) {
            destFilepath = selectPath.getAbsolutePath();

            labFilepath.setText(destFilepath);

            btnStart.setDisable(false);
        }
    }

    public void onClickStart(MouseEvent mouseEvent) {

        String password = textPassword.getText();
        if (password == null) {
            labFinalAlert.setText("Password!");
            return;
        }

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
        EncryptProgress encryptProgress = new EncryptProgress(MainController.this,encrypt);
        SecretKey secretKey = GenerateSecKey.generateKey(password, length, 65566,
                1, "AES");

        if (algorithm.equals("ChaCha20")) {

            byte[] associatedBytes;

            if (togAead.isSelected()) {
                associateData = textAEAD.getText();
                associatedBytes = associateData.getBytes();
            }else {
                try {
                    associatedBytes = SHA.digest(sourceFile,"3/512");
                } catch (IOException e) {
                   labFinalAlert.setText("Source File Broken!");
                   return;
                }
            }

            Platform.runLater(() -> encryptProgress.doEncrypt(sourceFile,sourceFilename,destFilepath,
                    "ChaCha20-Poly1305",secretKey,true,associatedBytes));

            btnStart.setDisable(true);
            setS1state(false);
            setS2state(false);
        }

        if (algorithm.equals("AES")){

        }
    }
}


