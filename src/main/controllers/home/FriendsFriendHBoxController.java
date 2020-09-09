package main.controllers.home;

import customComponents.AutoScrollLabel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.shape.Circle;
import main.Database;
import main.Main;
import main.Server;
import main.auxiliary.MyUser;

import static main.Main.homeController;
import static main.constants.StatusConstants.OFFLINE;
import static main.constants.StatusConstants.ONLINE;

public class FriendsFriendHBoxController
{
    @FXML private AutoScrollLabel name;
    @FXML private AutoScrollLabel bio;
    @FXML private Circle avatar;
    @FXML private Circle status;

    private String username;

    public void initialize(MyUser user)
    {
        username = user.getUsername();

        // load name
        Platform.runLater(() ->
        {
            name.setText(user.getName());
            name.moveText();
        });
        user.nameProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() ->
                {
                    name.setText(newValue);
                    name.moveText();
                }));

        // load avatar
        Platform.runLater(() -> avatar.setFill(Database.getAvatarImagePattern(user.getAvatar())));
        user.avatarProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> avatar.setFill(Database.getAvatarImagePattern(newValue.intValue()))));

        // load bio
        Platform.runLater(() -> bio.setText(user.getBio()));
        user.bioProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() ->
                {
                    bio.setText(newValue);
                    bio.moveText();
                }));

        // load status
        Platform.runLater(() -> fillStatus(user.getStatus()));
        user.statusProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> fillStatus(newValue.intValue())));

        Platform.runLater(() ->
        {
            name.moveText();
            bio.moveText();
        });
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
    void deleteClicked()
    {
        boolean result = Server.deleteFriend(Database.getMe().getUsername(), username);
        if(result)
        {
            homeController.removeFriend(username);
            Database.removeFriend(username);
        }
        else
            Main.showAlert(Alert.AlertType.WARNING, "Warning", "Cannot Delete Friendship",
                    "Couldn't remove " + username + " from your friends");
    }

    @FXML
    void sendMessageClicked()
    {
        System.out.println("Send Clicked");
    }
}
