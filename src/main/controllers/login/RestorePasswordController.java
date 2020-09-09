package main.controllers.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import main.Main;
import main.Server;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.showAlert;
import static main.Main.soundManager;

public class RestorePasswordController implements Initializable
{
    @FXML private JFXTextField username;
    @FXML private JFXComboBox<String> question;
    @FXML private JFXTextField answer;
    @FXML private Label password;
    @FXML private JFXButton restore;
    @FXML private Label server;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // add data to comboBox
        question.getItems().add("What is your favorite food?");
        question.getItems().add("Who is your best friend?");
        question.getItems().add("Where was your first home?");
        question.getItems().add("What is your special ability?");
        question.getItems().add("What is your favorite number?");
        question.getItems().add("Who is your favorite celebrity?");

        bindComponents();
    }

    private void bindComponents()
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
        question.valueProperty().addListener((observable, oldValue, newValue) -> setButtonDisable());
        answer.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            setButtonDisable();
        });
    }

    private void setButtonDisable()
    {
        if (username.getText().length() >= 4 && username.getText().substring(0, 1).matches("[A-Za-z_]"))
            if (!question.getSelectionModel().isEmpty())
                if (!answer.getText().isEmpty())
                {
                    restore.setDisable(false);
                    return;
                }
        restore.setDisable(true);
    }

    @FXML
    private void backPressed()
    {
        soundManager.playButtonClick();
        Main.loadFXML("ui/login/Login.fxml");
    }

    @FXML
    private void restorePressed()
    {
        soundManager.playButtonClick();
        password.setText("Your password will be here");

        // connect to server to check input validation
        new Thread(() ->
        {
            String result = Server.restorePassword(username.getText(), question.getSelectionModel().getSelectedItem(),
                    answer.getText(), server);

            if(result == null)
                return;

            switch (result)
            {
                case ":user_not_found":
                    showAlert(Alert.AlertType.WARNING, "Warning", "MyUser Not Found",
                            "We have no user with that username");
                    break;
                case ":q/a_wrong":
                    showAlert(Alert.AlertType.WARNING, "Warning","Q/A Wrong",
                            "Either Question or Answer is wrong");
                    break;
                default:
                    Platform.runLater(() -> password.setText(result));
                    break;
            }
        }).start();
    }
}
