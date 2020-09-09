package main.controllers.login;

import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideInRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import main.Database;
import main.Main;
import main.Server;
import main.auxiliary.MyUser;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.*;
import static main.constants.StatusConstants.ONLINE;

public class SignUpController implements Initializable
{

    @FXML VBox vBox;
    @FXML
    private VBox page1;
    private VBox page2;
    private VBox page3;
    private Page2Controller page2Controller;
    private Page3Controller page3Controller;
    @FXML private JFXTextField username;
    @FXML private JFXTextField phone;
    @FXML private JFXPasswordField password;
    @FXML private JFXButton previousPage;
    @FXML private JFXButton nextPage;
    @FXML private Label server;

    private int currentPage;

    private String myUsername, myPassword, myPhone, myName, myBio, myQuestion, myAnswer;
    private int myAvatar;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        currentPage = 1;

        username.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            if(newValue.length() < 4 || !newValue.substring(0, 1).matches("[A-Za-z_]") || newValue.contains(" "))
                username.setStyle("-jfx-focus-color: red");
            else
                username.setStyle("-fx-focus-color: #3498db");
        });

        phone.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            if(isAnyNonDigit(newValue) || newValue.length() != 11)
                phone.setStyle("-jfx-focus-color: red");
            else
                phone.setStyle("-fx-focus-color: #3498db");
        });

        password.textProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playType();
            if(newValue.length() < 8)
                password.setStyle("-jfx-focus-color: red");
            else
                password.setStyle("-fx-focus-color: #3498db");
        });
    }

    @FXML
    void nextPagePressed()
    {
        soundManager.playButtonClick();

        switch (currentPage)
        {
            case 1:
                if(canProceed())
                {
                    myUsername = username.getText();
                    myPhone = phone.getText();
                    myPassword = password.getText();
                    page1NextPressed();
                }
                else
                    showAlert(Alert.AlertType.WARNING, "Warning", "Cannot Proceed",
                            "You should enter at least 4 characters for username, " +
                                    "8 character for password and exactly 11 digits for your phone");
                break;
            case 2:
                if(page2Controller.canProceed())
                {
                    myName = page2Controller.getName();
                    myBio = page2Controller.getBio();
                    myAvatar = page2Controller.getAvatar();

                    goToPage3();
                } else
                    showAlert(Alert.AlertType.WARNING, "Warning", "Cannot Proceed",
                            "You have to enter at least 3 characters for name");
                break;
            case 3:
                if(page3Controller.canProceed())
                {
                    myQuestion = page3Controller.getQuestion();
                    myAnswer = page3Controller.getAnswer();

                    submit();
                } else
                    showAlert(Alert.AlertType.WARNING, "Warning", "Cannot Proceed",
                            "You have to choose a question and answer it, also accept our terms of services");
                break;
        }
    }

    @FXML
    void previousPagePressed()
    {
        soundManager.playButtonClick();

        switch (currentPage)
        {
            case 2:
                backToPage1();
                break;
            case 3:
                backToPage2();
                break;
        }
    }

    @FXML
    private void backPressed()
    {
        soundManager.playButtonClick();
        Main.loadFXML("ui/login/Login.fxml");
    }

    private boolean canProceed()
    {
        if(username.getText().length() < 4 ||
                !username.getText().substring(0, 1).matches("[A-Za-z_]") ||
                username.getText().contains(" "))
            return false;

        if(isAnyNonDigit(phone.getText()) || phone.getText().length() != 11)
            return false;

        return password.getText().length() >= 8;
    }

    private void page1NextPressed()
    {
        new Thread(() ->
        {
            int result = Server.checkUsernameAndPhone(username.getText(), phone.getText(), server);

            switch (result)
            {
                case 0:
                    showAlert(Alert.AlertType.WARNING, "Warning", "User Exists",
                            "The username you've entered is already taken");
                    break;
                case 1:
                    showAlert(Alert.AlertType.WARNING, "Warning", "Phone Exists",
                            "The phone number you've entered is already taken");
                    break;
                case 2:
                    goToPage2();
                    break;
            }
        }).start();
    }

    private void goToPage2()
    {
        Platform.runLater(() ->
        {
            vBox.getChildren().remove(page1);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/login/page2.fxml"));
            try
            {
                page2 = fxmlLoader.load();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            page2Controller = fxmlLoader.getController();

            vBox.getChildren().add(1, page2);
            new SlideInRight(page2).play();

            previousPage.setDisable(false);
            currentPage = 2;
        });
    }

    private void goToPage3()
    {
        Platform.runLater(() ->
        {
            vBox.getChildren().remove(page2);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/login/page3.fxml"));
            try
            {
                page3 = fxmlLoader.load();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            page3Controller = fxmlLoader.getController();

            vBox.getChildren().add(1, page3);
            new SlideInRight(page3).play();

            currentPage = 3;
            nextPage.setText("Submit");
        });
    }

    private void backToPage1()
    {
        Platform.runLater(() ->
        {
            vBox.getChildren().remove(page2);
            vBox.getChildren().add(1, page1);
            new SlideInLeft(page1).play();

            previousPage.setDisable(true);
            currentPage = 1;
        });
    }

    private void backToPage2()
    {
        Platform.runLater(() ->
        {
            vBox.getChildren().remove(page3);
            vBox.getChildren().add(1, page2);
            new SlideInLeft(page2).play();

            currentPage = 2;
            nextPage.setText("Next Page");
        });
    }

    private void submit()
    {
        MyUser me = new MyUser(myUsername.toLowerCase(), myPassword, myName, myBio, myPhone, myQuestion, myAnswer, ONLINE,
                System.currentTimeMillis(), myAvatar, 0);

        new Thread(() ->
        {
            if(Server.signUp(me, server))
            {
                Database.setMe(me);
                Main.loadTo("ui/home/Home.fxml");
            }
        }).start();
    }

    private boolean isAnyNonDigit(String string)
    {
        String digits = "0123456789";
        for(int i = 0; i < string.length(); i++)
        {
            if(!digits.contains(string.charAt(i) + ""))
                return true;
        }
        return false;
    }
}
