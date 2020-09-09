package main.controllers.home;

import com.jfoenix.controls.JFXPasswordField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import main.Database;
import main.Main;
import main.Server;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.soundManager;

public class SettingPage2Controller implements Initializable
{
    @FXML private JFXPasswordField currentPassword;
    @FXML private JFXPasswordField newPassword1;
    @FXML private JFXPasswordField newPassword2;
    @FXML private Label server;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        currentPassword.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            if(newValue.length() < 8)
                currentPassword.setStyle("-jfx-focus-color: red");
            else
                currentPassword.setStyle("-fx-focus-color: #3498db");
        });

        newPassword1.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            if(newValue.length() < 8)
                newPassword1.setStyle("-jfx-focus-color: red");
            else
                newPassword1.setStyle("-fx-focus-color: #3498db");
        });

        newPassword2.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            if(newValue.length() < 8)
                newPassword2.setStyle("-jfx-focus-color: red");
            else
                newPassword2.setStyle("-fx-focus-color: #3498db");
        });
    }

    @FXML
    void savePressed()
    {
        soundManager.playButtonClick();

        if(!Database.getMe().getPassword().equals(currentPassword.getText()))
        {
            Main.showAlert(Alert.AlertType.WARNING, "Warning", "Password Incorrect",
                    "Your current password is wrong");
            return;
        }

        if(!newPassword1.getText().equals(newPassword2.getText()))
        {
            Main.showAlert(Alert.AlertType.WARNING, "Warning", "Passwords Aren't The Same",
                    "Please make sure you have entered your new password right in both fields");
            return;
        }

        if(newPassword1.getText().length() < 8 || newPassword2.getText().length() < 8)
        {
            Main.showAlert(Alert.AlertType.WARNING, "Warning", "Wrong Format",
                    "You have to enter at least 8 character for your password");
            return;
        }

        Database.getMe().setPassword(newPassword1.getText());
        new Thread(() ->
        {
            boolean result = Server.updateProfile(Database.getMe(), server);
            if(result)
                Main.showAlert(Alert.AlertType.INFORMATION, "Success", "Updated Successfully",
                        "Your profile has been updated");
        }).start();
    }
}
