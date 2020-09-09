package main.controllers.home;

import customComponents.AutoScrollLabel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import main.Database;
import main.auxiliary.MyUser;

import static main.constants.StatusConstants.OFFLINE;
import static main.constants.StatusConstants.ONLINE;

public class FriendsSentReqHBoxController
{
    @FXML private AutoScrollLabel usernameLabel;
    @FXML private Circle avatar;
    @FXML private Circle status;

    private String username;

    public void initialize(MyUser user)
    {
        username = user.getUsername();

        // load username
        usernameLabel.setText(user.getUsername());

        // load avatar
        avatar.setFill(Database.getAvatarImagePattern(user.getAvatar()));
        user.avatarProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> avatar.setFill(Database.getAvatarImagePattern(newValue.intValue()))));

        // load status
        fillStatus(user.getStatus());
        user.statusProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> fillStatus(newValue.intValue())));

        usernameLabel.moveText();
    }

    private void fillStatus(int status)
    {
        if(status == ONLINE)
            this.status.setStyle("-fx-fill: green");
        else if(status == OFFLINE)
            this.status.setStyle("-fx-fill: red");
        else
            this.status.setStyle("-fx-fill: yellow");
    }

    public String getUsername()
    {
        return username;
    }
}
