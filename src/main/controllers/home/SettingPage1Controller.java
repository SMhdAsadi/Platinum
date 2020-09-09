package main.controllers.home;

import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import main.Database;
import main.Main;
import main.Server;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.soundManager;

public class SettingPage1Controller implements Initializable
{
    @FXML private JFXTextField name;
    @FXML private JFXTextArea bio;

    @FXML private Circle avatarCircle0;
    @FXML private Circle avatarCircle1;
    @FXML private Circle avatarCircle2;
    @FXML private Circle avatarCircle3;
    @FXML private Circle avatarCircle4;
    @FXML private Circle avatarCircle5;
    @FXML private Circle avatarCircle6;
    @FXML private Circle avatarCircle7;
    @FXML private Circle avatarCircle8;
    @FXML private Circle avatarCircle9;
    @FXML private Circle avatarCircle10;
    @FXML private Circle avatarCircle11;
    @FXML private Circle avatarCircle12;
    @FXML private Circle avatarCircle13;
    @FXML private Circle avatarCircle14;
    @FXML private Circle avatarCircle15;
    @FXML private Circle avatarCircle16;
    @FXML private Circle avatarCircle17;
    @FXML private Circle avatarCircle18;
    @FXML private Circle avatarCircle19;

    @FXML private Label server;
    private Circle[] circles;
    private int selectedCircleNumber;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        circles = new Circle[]{avatarCircle0, avatarCircle1, avatarCircle2, avatarCircle3, avatarCircle4,
                avatarCircle5, avatarCircle6, avatarCircle7, avatarCircle8, avatarCircle9, avatarCircle10, avatarCircle11,
                avatarCircle12, avatarCircle13, avatarCircle14, avatarCircle15, avatarCircle16, avatarCircle17,
                avatarCircle18, avatarCircle19};

        selectedCircleNumber = Database.getMe().getAvatar();
        circles[Database.getMe().getAvatar()].setStyle("-fx-stroke: red");
        name.setText(Database.getMe().getName());
        bio.setText(Database.getMe().getBio());

        for(int i = 0; i < circles.length; i++)
            circles[i].setFill(Database.getAvatarImagePattern(i));

        name.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            if(newValue.length() < 3)
                name.setStyle("-jfx-focus-color: red");
            else
                name.setStyle("-fx-focus-color: #3498db");
        });

        bio.textProperty().addListener(((observable, oldValue, newValue) -> soundManager.playType()));
    }

    @FXML
    void avatarCircleClicked(MouseEvent event)
    {
        soundManager.playSelect();
        circles[selectedCircleNumber].setStyle("-fx-stroke: white");

        Circle selectedCircle = (Circle) event.getSource();
        selectedCircle.setStyle("-fx-stroke: red");
        new ZoomIn(selectedCircle).play();

        int index = 0;
        for(int i = 0; i < circles.length; i++)
        {
            if(circles[i] == selectedCircle)
            {
                index = i;
                break;
            }
        }

        selectedCircleNumber = index;
    }

    @FXML
    void applyPressed()
    {
        soundManager.playButtonClick();
        Database.getMe().setAvatar(selectedCircleNumber);
        Database.getMe().setName(name.getText());
        Database.getMe().setBio(bio.getText());

        new Thread(() ->
        {
            boolean result = Server.updateProfile(Database.getMe(), server);
            if(result)
            {
                Main.showAlert(Alert.AlertType.INFORMATION, "Success", "Updated Successfully",
                        "Your profile has been updated");
            }
        }).start();
    }
}
