package main.controllers.home;

import customComponents.AutoScrollLabel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import main.Database;
import main.Server;
import main.auxiliary.MyUser;

import static main.Main.homeController;
import static main.constants.StatusConstants.OFFLINE;
import static main.constants.StatusConstants.ONLINE;

public class FriendsReceivedReqHBoxController
{
    @FXML private HBox hBox;
    @FXML private AutoScrollLabel username;
    @FXML private Circle avatar;
    @FXML private Circle status;

    public void initialize(MyUser user)
    {
        // load username
        username.setText(user.getUsername());

        // load status
        fillStatus(user.getStatus());
        user.statusProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> fillStatus(newValue.intValue())));

        // load avatar
        avatar.setFill(Database.getAvatarImagePattern(user.getAvatar()));
        user.avatarProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> avatar.setFill(Database.getAvatarImagePattern(newValue.intValue()))));

        username.moveText();
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

    @FXML
    private void acceptClicked()
    {
        boolean result = Server.respondFriendRequest(Database.getMe().getUsername(), username.getText(), true);
        if(result)
        {
            Database.removeFriendRequest(username.getText());
            Database.addFriend(username.getText());
            homeController.removeRequest(username.getText());
            homeController.addFriend(username.getText());
        }
    }

    @FXML
    private void declineClicked()
    {
        boolean result = Server.respondFriendRequest(Database.getMe().getUsername(), username.getText(), false);
        if(result)
        {
            Database.removeFriendRequest(username.getText());
            homeController.removeRequest(username.getText());
        }
    }
}
