package main.controllers.home;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTabPane;
import customComponents.AutoCompletionTextField;
import customComponents.AutoScrollLabel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import main.Database;
import main.Main;
import main.Server;
import main.auxiliary.GameStatistics;
import main.auxiliary.MyUser;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static main.Main.soundManager;

public class HomeController implements Initializable
{
    @FXML private JFXTabPane tabPane;

    // home components
    @FXML private Tab homeTab;
    @FXML private Label homeScore;
    @FXML private Label homeLeaderboard;
    @FXML private Circle homeAvatar;
    @FXML private AutoScrollLabel homeName;
    @FXML private Label homeBio;
    @FXML private JFXSpinner homeOnlineGamesChart;
    @FXML private JFXSpinner homeOfflineGamesChart;
    @FXML private JFXSpinner homeGamesWonChart;
    @FXML private JFXSpinner homeGamesDrawnChart;
    @FXML private JFXSpinner homeGamesLostChart;
    @FXML private Label homeTips;

    @FXML private Tab gamesTab;
    @FXML private Tab chatTab;

    // friends components
    @FXML private Tab friendsTab;
    @FXML private AutoCompletionTextField friendsAddFriend;
    @FXML private Label friendsServer;
    @FXML private VBox friendsVBox;
    @FXML private VBox friendsRequestsVBox;
    private final Map<String, HBox> friendsVBoxMembers = new HashMap<>();
    private final Map<String, HBox> friendsRequestsVBoxMembers = new HashMap<>();

    // settings components
    @FXML private Tab settingsTab;
    @FXML private JFXListView<Label> settingsListView;
    @FXML private HBox settingsHBox;
    private VBox settingsPage1;
    private VBox settingsPage2;
    private VBox settingsPage3;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loadHome();
        loadFriends();
        loadSettings();

        tabPane.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
        {
            soundManager.playSwipe();
            if(newValue == homeTab)
                showHome();
            else if(newValue == friendsTab)
                showFriend();
        }));
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //                                                                                //
    //                               HOME METHODS                                     //
    //                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////

    private void loadHome()
    {
        MyUser me = Database.getMe();

        // name
        homeName.setText(me.getName());
        homeName.moveText();
        me.nameProperty().addListener((observable, oldValue, newValue) ->
        {
            homeName.setText(newValue);
            homeName.moveText();
        });

        // bio
        homeBio.textProperty().bind(me.bioProperty());

        // avatar
        homeAvatar.setFill(Database.getAvatarImagePattern(me.getAvatar()));
        me.avatarProperty().addListener((observable, oldValue, newValue) ->
                homeAvatar.setFill(Database.getAvatarImagePattern(newValue.intValue())));

        // tips
        homeTips.setText(Database.getTip());
        new FadeIn(homeTips).setSpeed(0.5).play();
    }

    public void showHome()
    {
        homeScore.setText("0");

        homeOnlineGamesChart.setProgress(0);
        homeOfflineGamesChart.setProgress(0);
        homeGamesWonChart.setProgress(0);
        homeGamesDrawnChart.setProgress(0);
        homeGamesLostChart.setProgress(0);

        // animation the charts
        JFXSpinner[] spinners = new JFXSpinner[]{homeOnlineGamesChart, homeOfflineGamesChart,
                homeGamesWonChart, homeGamesDrawnChart, homeGamesLostChart};
        GameStatistics GA = Database.getGameStatistics();
        double[] progresses = new double[]{GA.getOnlineGamesPercentage(), GA.getOfflineGamesPercentage(),
                GA.getGamesWonPercentage(), GA.getGamesDrawnPercentage(), GA.getGamesLostPercentage()};

        for(int i = 0; i < spinners.length; i++)
        {
            KeyValue keyValue = new KeyValue(spinners[i].progressProperty(), progresses[i], Interpolator.EASE_BOTH);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), keyValue);
            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        }

        // animation the score
        new Thread(() ->
        {
            int score = Database.getMe().getScore();

            long delay;
            if(score == 0)
                delay = 0;
            else
                delay = 3000 / score;

            for(int i = 1; i < score + 1; i++)
            {
                try
                {
                    Thread.sleep(delay);
                } catch (InterruptedException exception)
                {
                    exception.printStackTrace();
                }
                int finalI = i;
                Platform.runLater(() -> homeScore.setText(finalI + ""));
            }
        }).start();
    }

    @FXML
    private void homeTipsClicked()
    {
        homeTips.setText(Database.getTip());
        new ZoomIn(homeTips).play();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //                                                                                //
    //                              FRIENDS METHODS                                   //
    //                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////

    private void loadFriends()
    {
        friendsAddFriend.getEntries().addAll(Arrays.asList(Database.getUsernames()));
        friendsServer.setVisible(false);

        // load friends
        for(String username: Database.getFriends())
            addFriend(username);

        // load requests
        for(String username: Database.getFriendRequests())
            addFriendRequestReceived(username);
    }

    private void showFriend()
    {
        for(Node children: friendsVBox.getChildren())
            new FadeIn(children).setSpeed(0.5).play();

        for(Node children: friendsRequestsVBox.getChildren())
            new FadeIn(children).setSpeed(0.5).play();
    }

    @FXML
    private void friendsAddFriendPressed()
    {
        soundManager.playButtonClick();

        String username = friendsAddFriend.getText();
        if(username.length() == 0)
        {
            Main.showAlert(Alert.AlertType.WARNING, "Warning", "No Input",
                    "Please enter a username first");
            return;
        } else if(username.equals(Database.getMe().getUsername()))
        {
            Main.showAlert(Alert.AlertType.WARNING, "Warning", "This Is You",
                    "You cannot send request to yourself");
            return;
        }

        MyUser user = Database.getUser(username);

        if(user == null)
        {
            Main.showAlert(Alert.AlertType.WARNING, "Warning", "No User Found",
                    "Please enter a valid username");
            return;
        }
        else if(Database.hasFriendRequest(username))
        {
            Main.showAlert(Alert.AlertType.WARNING, "Warning", "He/She Requested Already",
                    "Please answer the user's request");
            return;
        }

        new Thread(() ->
        {
            int result = Server.newFriendRequest(Database.getMe().getUsername(), username, friendsServer);

            switch (result)
            {
                case 2: // if they are already friends
                    Main.showAlert(Alert.AlertType.WARNING, "Warning", "You Are Friend",
                            "The user you want to send friend request is your friend");
                    break;
                case 3: // if this is duplicate request
                    Main.showAlert(Alert.AlertType.WARNING, "Warning", "Duplicate Request",
                            "Please Wait for the user to respond to your previous request");
                    break;
                case 4: // if requested successfully
                    addFriendRequestSent(user);
                    break;
            }
        }).start();

    }

    public void addFriendRequestReceived(String username)
    {
        MyUser user = Database.getUser(username);
        if(user == null)
            return;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/home/FriendsReceivedReqHBox.fxml"));
        try
        {
            HBox requestHBox = fxmlLoader.load();
            Platform.runLater(() -> friendsRequestsVBox.getChildren().add(requestHBox));
            friendsRequestsVBoxMembers.put(username, requestHBox);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        FriendsReceivedReqHBoxController controller = fxmlLoader.getController();
        controller.initialize(user);
    }

    private void addFriendRequestSent(MyUser user)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/home/FriendsSentReqHBox.fxml"));
        try
        {
            HBox requestHBox = fxmlLoader.load();
            Platform.runLater(() ->
            {
                friendsRequestsVBox.getChildren().add(requestHBox);
                new FadeIn(requestHBox).play();
            });
            friendsRequestsVBoxMembers.put(user.getUsername(), requestHBox);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        FriendsSentReqHBoxController controller = fxmlLoader.getController();
        controller.initialize(user);
    }

    public void removeRequest(String username)
    {
        if(friendsRequestsVBoxMembers.containsKey(username))
        {
            HBox hBox = friendsRequestsVBoxMembers.get(username);
            friendsRequestsVBoxMembers.remove(username);
            FadeOut fadeOut = new FadeOut(hBox);
            fadeOut.play();
            fadeOut.setOnFinished(event -> friendsRequestsVBox.getChildren().remove(hBox));
        }
    }

    public void addFriend(String username)
    {
        MyUser user = Database.getUser(username);
        if(user == null)
            return;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/home/FriendsFriendHBox.fxml"));
        try
        {
            HBox friendHBox = fxmlLoader.load();
            Platform.runLater(() ->
            {
                friendsVBox.getChildren().add(friendHBox);
                new FadeIn(friendHBox).play();
            });
            friendsVBoxMembers.put(username, friendHBox);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        FriendsFriendHBoxController controller = fxmlLoader.getController();
        controller.initialize(user);
    }

    public void removeFriend(String username)
    {
        HBox hBox = friendsVBoxMembers.get(username);
        FadeOut fadeOut = new FadeOut(hBox);
        fadeOut.play();
        fadeOut.setOnFinished(event ->
                {
                    friendsVBox.getChildren().remove(hBox);
                    friendsVBoxMembers.remove(username);
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //                                                                                //
    //                            SETTING METHODS                                     //
    //                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////

    private void loadSettings()
    {
        Label editProfile = new Label("Edit Profile");
        Label changePassword = new Label("Change Password");
        Label sound = new Label("Sound");
        Label logout = new Label("Logout");
        Label exit = new Label("Exit");

        Label[] labels = new Label[]{editProfile, changePassword, sound, logout, exit};
        for(Label label: labels)
            label.getStyleClass().add("setting-list-cell-label");

        settingsListView.getItems().addAll(labels);

        settingsListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
        {
            soundManager.playButtonClick();
            switch (newValue.intValue())
            {
                case 0:
                    loadSettingPage1();
                    break;
                case 1:
                    loadSettingPage2();
                    break;
                case 2:
                    loadSettingPage3();
                    break;
                case 3:
                    if(Main.showConfirmation())
                    {
                        Server.logout(Database.getMe());
                        Server.disconnect();
                        soundManager.stopAll();
                        Main.loadFXML("ui/login/Login.fxml");
                    }
                    break;
                case 4:
                    if(Main.showConfirmation())
                        Main.closeProgram();
                    break;
            }
        });
        settingsListView.getSelectionModel().select(editProfile);
    }

    private void loadSettingPage1()
    {
        // load the page if it is not
        if(settingsPage1 == null)
        {
            try
            {
                settingsPage1 = FXMLLoader.load(Main.class.getResource("ui/home/SettingPage1.fxml"));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        // show the page if it is not
        if(!settingsHBox.getChildren().contains(settingsPage1))
        {
            settingsHBox.getChildren().remove(settingsPage2);
            settingsHBox.getChildren().remove(settingsPage3);

            settingsHBox.getChildren().add(settingsPage1);
            new FadeIn(settingsPage1).play();
        }
    }

    private void loadSettingPage2()
    {
        // load the page if it is not
        if(settingsPage2 == null)
        {
            try
            {
                settingsPage2 = FXMLLoader.load(Main.class.getResource("ui/home/SettingPage2.fxml"));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        // show the page if it is not
        if(!settingsHBox.getChildren().contains(settingsPage2))
        {
            settingsHBox.getChildren().remove(settingsPage1);
            settingsHBox.getChildren().remove(settingsPage3);

            settingsHBox.getChildren().add(settingsPage2);
            new FadeIn(settingsPage2).play();
        }
    }

    private void loadSettingPage3()
    {
        // load the page if it is not
        if(settingsPage3 == null)
        {
            try
            {
                settingsPage3 = FXMLLoader.load(Main.class.getResource("ui/home/SettingPage3.fxml"));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        // show the page if it is not
        if(!settingsHBox.getChildren().contains(settingsPage3))
        {
            settingsHBox.getChildren().remove(settingsPage1);
            settingsHBox.getChildren().remove(settingsPage2);

            settingsHBox.getChildren().add(settingsPage3);
            new FadeIn(settingsPage3).play();
        }
    }
}
