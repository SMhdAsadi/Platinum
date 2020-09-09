package main.controllers.login;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.soundManager;

public class Page3Controller implements Initializable
{
    @FXML private JFXComboBox<String> question;
    @FXML private JFXTextField answer;
    @FXML private JFXToggleButton terms;

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

        answer.textProperty().addListener(((observable, oldValue, newValue) -> soundManager.playType()));
    }

    @FXML
    void termsPressed()
    {
        soundManager.playToggleClick();
    }

    public boolean canProceed() {
        return !question.getSelectionModel().isEmpty() && !answer.getText().isEmpty() && terms.isSelected();
    }
    public String getQuestion()
    {
        return question.getSelectionModel().getSelectedItem();
    }
    public String getAnswer()
    {
        return answer.getText();
    }
}
