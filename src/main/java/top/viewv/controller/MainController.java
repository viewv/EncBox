package top.viewv.controller;

import com.jfoenix.controls.*;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainController {

    // UI part
    public AnchorPane basePane;
    public AnchorPane paneSide;
    public AnchorPane paneMainfunction;

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
    public JFXComboBox<String> cboxLength;
    public JFXComboBox<String> cboxMode;
    public JFXComboBox<String> cboxPadding;

    public JFXTextField textPassword;
    public JFXToggleButton togAead;
    public JFXTextField textAEAD;

    public JFXSlider sldNumSpecial;
    public JFXSlider sldNumUpper;
    public JFXSlider sldPasswordLength;

    public Label labProcess;

    public JFXButton btnChooseDir;
    public Label labFilepath;
    public JFXButton btnStart;


}