package top.viewv.controller;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

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

    }
}
