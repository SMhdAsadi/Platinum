package main.controllers.login;

import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import main.Database;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.soundManager;

public class Page2Controller implements Initializable
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

    private Circle[] circles;
    private int selectedCircle;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        circles = new Circle[]{avatarCircle0, avatarCircle1, avatarCircle2, avatarCircle3, avatarCircle4,
                avatarCircle5, avatarCircle6, avatarCircle7, avatarCircle8, avatarCircle9, avatarCircle10, avatarCircle11,
                avatarCircle12, avatarCircle13, avatarCircle14, avatarCircle15, avatarCircle16, avatarCircle17,
                avatarCircle18, avatarCircle19};

        for(int i = 0; i < circles.length; i++)
        {
            circles[i].setFill(Database.getAvatarImagePattern(i));
        }

        selectCircle(selectedCircle = 0);

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
        Circle selectedCircle = (Circle) event.getSource();
        int index = 0;

        for(int i = 0; i < circles.length; i++)
        {
            if(circles[i] == selectedCircle)
            {
                index = i;
                break;
            }
        }
        selectCircle(index);
    }

    private void selectCircle(int index)
    {
        if(index < 0 || index >= circles.length)
            return;

        soundManager.playSelect();
        selectedCircle = index;

        for(int i = 0; i < circles.length; i++)
        {
            if(i == index)
            {
                circles[i].setStyle("-fx-stroke: red");
                new ZoomIn(circles[selectedCircle]).play();
            }
            else
                circles[i].setStyle("-fx-stroke: white");
        }
    }

    public boolean canProceed() {
        return name.getText().length() >= 3;
    }
    public String getName() {
        return name.getText();
    }
    public String getBio() {
        return bio.getText();
    }
    public int getAvatar() {
        return selectedCircle;
    }
}
