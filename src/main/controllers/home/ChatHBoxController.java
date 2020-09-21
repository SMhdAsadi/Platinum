package main.controllers.home;

import customComponents.AutoScrollLabel;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import main.Database;
import main.auxiliary.MyUser;

import static main.Main.homeController;
import static main.constants.StatusConstants.OFFLINE;
import static main.constants.StatusConstants.ONLINE;

public class ChatHBoxController
{
    @FXML private Circle profile;
    @FXML private Circle status;
    @FXML private AutoScrollLabel name;
    @FXML private FontAwesomeIconView icon;
    @FXML private AutoScrollLabel description;

    private String chatName;

    public void initializeUser(MyUser user)
    {
        chatName = "pv" + user.getUsername();

        profile.setFill(Database.getAvatarImagePattern(user.getAvatar()));
        user.avatarProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> profile.setFill(Database.getAvatarImagePattern(newValue.intValue()))));

        statusFill(user.getStatus());
        user.statusProperty().addListener((observable, oldValue, newValue) ->
        {
            Platform.runLater(() ->
            {
                statusFill(newValue.intValue());
                if(newValue.intValue() == OFFLINE)
                    description.setText(user.getLastSeen());
                else if(newValue.intValue() == ONLINE)
                    description.setText(user.getBio());
            });
        });

        name.setText(user.getName());
        name.moveText();
        user.nameProperty().addListener((observable, oldValue, newValue) ->
        {
            Platform.runLater(() ->
            {
                name.setText(newValue);
                name.moveText();
            });
        });

        if(user.getStatus() == ONLINE)
            description.setText(user.getBio());
        else
            description.setText(user.getLastSeen());
        description.moveText();
        user.bioProperty().addListener((observable, oldValue, newValue) ->
        {
            Platform.runLater(() ->
            {
                if(user.getStatus() == ONLINE)
                {
                    description.setText(newValue);
                    description.moveText();
                }
            });
        });

        icon.setGlyphName("USER");
    }

    private void statusFill(int status)
    {
        if(status == ONLINE)
            this.status.setStyle("-fx-fill: green");
        else if(status == OFFLINE)
            this.status.setStyle("-fx-fill: red");
        else
            this.status.setStyle("-fx-fill: yellow");
    }


    @FXML
    void chatHBoxClicked()
    {
        homeController.chatSelected(chatName);
    }
}
