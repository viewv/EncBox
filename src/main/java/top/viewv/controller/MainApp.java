package top.viewv.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import top.viewv.model.passwordmanager.PMSerialize;
import top.viewv.model.passwordmanager.PMStorage;
import top.viewv.model.tools.TwoFactorAuthentication;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new
                    FXMLLoader(Objects.requireNonNull(getClass()).getClassLoader()
                    .getResource("data/ui/Main.fxml"));

            System.out.println("Start EncBox Plus Success");

            // Print some useful information
            String javaversion = System.getProperty("java.version");
            System.out.println("Java Version: " + javaversion);

            String javaFxversion = System.getProperty("javafx.version");
            System.out.println("JavaFx Version: " + javaFxversion);

            final String os = System.getProperty("os.name");
            System.out.println("operate System: " + os);

            String currentPath = System.getProperty("user.dir");
            System.out.println("Current Path: " + currentPath);

            Parent root = loader.load();
            Scene scene = new Scene(root);

            String css = Objects.requireNonNull(MainApp.class.getClassLoader().getResource("data/style/main.css")).toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("EncBox Plus");
            primaryStage.setScene(scene);

            File file = new File("vault.ser");
            PMStorage pmStorage = new PMStorage();

            while (true){

                String title;
                String typeText;

                if (!file.exists()){
                    title = "Set your main password";
                    typeText = "Done";
                }else {
                    title = "Input your main password";
                    typeText = "Unlock";
                }

                javafx.scene.control.Dialog<String> dialog = new Dialog<>();
                dialog.setTitle(title);
                dialog.setHeaderText("Enter your main password");

                ButtonType buttonType = new ButtonType(typeText, ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

                GridPane gridPane = new GridPane();
                gridPane.setHgap(10);
                gridPane.setVgap(10);
                gridPane.setPadding(new Insets(20, 20, 10, 10));

                PasswordField pwd = new PasswordField();
                pwd.setPromptText("password");

                gridPane.add(new Label("Password:"), 0, 0);
                gridPane.add(pwd, 1, 0);

                dialog.getDialogPane().setContent(gridPane);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == buttonType) {
                        return pwd.getText();
                    }
                    return null;
                });

                String password;
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()){
                    password = result.get();
                    if (password.length() != 0){
                        if (!file.exists()) {
                            pmStorage.password = password;

                            String twokey = TwoFactorAuthentication.generateSecretKey();
                            pmStorage.twofa = twokey;

                            String email = "encbox.viewv.top";
                            String companyName = "EncBox";
                            String barCodeUrl = TwoFactorAuthentication.getGoogleAuthenticatorBarCode(twokey,email,companyName);

                            TwoFactorAuthentication.createQRCode(barCodeUrl,"QRCode.png",300,300);
                            File qrcode = new File("QRCode.png");

                            javafx.scene.control.Dialog<String> twodialog = new Dialog<>();
                            twodialog.setTitle("2FA");
                            twodialog.setHeaderText("Please Save your 2FA information");

                            ButtonType twobuttonType = new ButtonType(typeText, ButtonBar.ButtonData.OK_DONE);
                            twodialog.getDialogPane().getButtonTypes().addAll(twobuttonType, ButtonType.CANCEL);

                            GridPane twogridPane = new GridPane();
                            twogridPane.setHgap(10);
                            twogridPane.setVgap(10);
                            twogridPane.setPadding(new Insets(10, 10, 10, 10));

                            TextField textField = new TextField(twokey);
                            textField.setEditable(false);
                            textField.getStyleClass().add("copyable-label");

                            ImageView imageView = new ImageView(new Image(String.valueOf(qrcode.toURI().toURL())));

                            twogridPane.add(textField, 0, 0);
                            twogridPane.add(imageView, 0, 1);

                            twodialog.getDialogPane().setContent(twogridPane);

                            twodialog.showAndWait();

                            qrcode.delete();

                            try {
                                PMSerialize.serialize(pmStorage, "vault.ser");
                                break;
                            }catch (Exception e){
                                e.printStackTrace();
                                sendAlert(Alert.AlertType.ERROR,
                                        "Error",
                                        "File write error!");
                                Platform.exit();
                            }
                        }else {
                            pmStorage = PMSerialize.deserialize("vault.ser");
                            if (pmStorage.password.equals(password)){
                                String twofa = pmStorage.twofa;

                                TextInputDialog textdialog = new TextInputDialog();
                                textdialog.setTitle("2FA");
                                textdialog.setHeaderText("Input your 2FA code");
                                textdialog.setContentText("2FA code:");

                                int i = 5;
                                boolean flag = false;

                                while (i > 0){
                                    String code;
                                    Optional<String> textresult = textdialog.showAndWait();
                                    if (textresult.isPresent()){
                                        code = textresult.get();
                                        String currentcode = TwoFactorAuthentication.getTOTPCode(twofa);
                                        if(code.equals(currentcode)){
                                            flag = true;
                                            break;
                                        }else {
                                            sendAlert(Alert.AlertType.WARNING,
                                                    "Warning",
                                                    "Error 2FA code! please input again!");
                                        }
                                    }
                                    i--;
                                }

                                if (flag){
                                    break;
                                }else {
                                    sendAlert(Alert.AlertType.WARNING,
                                            "Error",
                                            "You need 2FA code!");
                                    Platform.exit();
                                }

                            }else {
                                sendAlert(Alert.AlertType.WARNING,
                                        "Error",
                                        "Error password!");
                                Platform.exit();
                            }
                        }
                    }else {
                        sendAlert(Alert.AlertType.WARNING,
                                "Password error",
                                "Empty password!");
                    }
                }else {
                    sendAlert(Alert.AlertType.WARNING,
                            "Warning",
                            "You need input password!");
                    Platform.exit();
                }
            }

            primaryStage.show();

            //Set min-size
            primaryStage.setMinWidth(primaryStage.getWidth());
            primaryStage.setMinHeight(primaryStage.getHeight());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
