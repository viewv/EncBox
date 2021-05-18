package top.viewv.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class ItemController {

    public Label filename;
    public TextField password;
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setFilename(String filename) {
        this.filename.setText(filename);
    }

    public void setPassword(String password) {
        this.password.setText(password);
    }

    public void delete(MouseEvent mouseEvent) {
        mainController.deletePassword(filename.getText());
    }

    public void modify(MouseEvent mouseEvent) {
        String password;

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Modify");
        dialog.setHeaderText("Modify Password");

        ButtonType buttonType = new ButtonType("Modify", ButtonBar.ButtonData.OK_DONE);
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

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()){
            password = result.get();

            if (password.length() != 0){
                this.password.setText(password);
                mainController.modifyPassword(filename.getText(),password);
            }else {
                sendAlert(Alert.AlertType.WARNING,
                        "Warning",
                        "Empty Password");
            }
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
