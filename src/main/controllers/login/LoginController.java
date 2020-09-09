package main.controllers.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import main.Main;
import main.Server;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.showAlert;
import static main.Main.soundManager;

public class LoginController implements Initializable
{
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;
    @FXML private JFXButton login;
    @FXML private Label server;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        username.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            setButtonDisable();
            if(newValue.length() < 4 || !newValue.substring(0, 1).matches("[A-Za-z_]"))
            {
                username.setStyle("-jfx-focus-color: red");
            }
            else
            {
                username.setStyle("-fx-focus-color: #3498db");
            }
        });

        password.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            setButtonDisable();
            if(newValue.length() < 8)
            {
                password.setStyle("-jfx-focus-color: red");
            }
            else
            {
                password.setStyle("-fx-focus-color: #3498db");
            }
        });

        password.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER)
                loginButtonPressed();
        });
    }

    private void setButtonDisable()
    {
        if(username.getText().length() >= 4 && username.getText().substring(0, 1).matches("[A-Za-z_]"))
            if(password.getText().length() >= 8)
            {
                login.setDisable(false);
                return;
            }

        login.setDisable(true);
    }

    @FXML
    private void signUpButtonPressed()
    {
        soundManager.playButtonClick();
        Main.loadFXML("ui/login/SignUp.fxml");
    }

    @FXML
    private void restorePressed()
    {
        Main.loadFXML("ui/login/RestorePassword.fxml");
    }

    @FXML
    private void loginButtonPressed()
    {
        soundManager.playButtonClick();

        // connect to server for data validation
        new Thread(() ->
        {
            int result = Server.login(username.getText(), password.getText(), server);

            switch (result)
            {
                case 0:
                    showAlert(Alert.AlertType.WARNING, "Warning", "User Not Found",
                            "We have no user with that username");
                    break;
                case 1:
                    showAlert(Alert.AlertType.WARNING, "Warning", "User Is Online",
                            "MyUser is online now, logout on other device first");
                    break;
                case 2:
                    showAlert(Alert.AlertType.WARNING, "Warning", "Password Incorrect",
                            "The password you've entered is incorrect");
                    break;
                case 3:
                    // user can login
                    Main.loadTo("ui/home/Home.fxml");
                    break;
            }
        }).start();
    }
}
