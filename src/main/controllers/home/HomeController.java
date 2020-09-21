package main.controllers.home;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import customComponents.AutoCompletionTextField;
import customComponents.AutoScrollLabel;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import main.Database;
import main.Main;
import main.Server;
import main.auxiliary.GameStatistics;
import main.auxiliary.MyMessage;
import main.auxiliary.MyUser;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    // chat components
    @FXML private Tab chatTab;
    @FXML private VBox chatVBox;
    @FXML private Label chatLabel;
    @FXML private ScrollPane chatMessagesScrollPane;
    @FXML private VBox chatMessageVBox;
    @FXML private JFXTextArea chatMessageTextField;
    @FXML private MaterialDesignIconView chatEmojiIcon;
    @FXML private ScrollPane chatEmojiScrollPane;
    @FXML private GridPane chatEmojiGridPane;
    private String chatSelected; // for controlling the selected chat
    private final Map<String, HBox> chatVBoxMembers = new HashMap<>(); // for adding and removing chat hBoxes
    private final ArrayList<ChatMessageController> messageControllers = new ArrayList<>(); // for accessing to messages

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
        loadChat();
        loadFriends();
        loadSettings();

        // tab change action
        tabPane.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
        {
            soundManager.playSwipe();

            // add visual effects when selected tab is shown
            if(newValue == homeTab)
                showHome();
            else if(newValue == chatTab)
                showChat();
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
    }

    public void showHome()
    {
        homeScore.setText("0");

        homeOnlineGamesChart.setProgress(0);
        homeOfflineGamesChart.setProgress(0);
        homeGamesWonChart.setProgress(0);
        homeGamesDrawnChart.setProgress(0);
        homeGamesLostChart.setProgress(0);

        // animate the charts
        JFXSpinner[] spinners = new JFXSpinner[]{homeOnlineGamesChart, homeOfflineGamesChart,
                homeGamesWonChart, homeGamesDrawnChart, homeGamesLostChart};
        GameStatistics GA = Database.getGameStatistics();
        double[] progresses = new double[]{GA.getOnlineGamesPercentage(), GA.getOfflineGamesPercentage(),
                GA.getGamesWonPercentage(), GA.getGamesDrawnPercentage(), GA.getGamesLostPercentage()};

        for(int i = 0; i < spinners.length; i++)
        {
            KeyValue keyValue = new KeyValue(spinners[i].progressProperty(), progresses[i], Interpolator.EASE_OUT);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), keyValue);
            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        }

        // animate the score
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

        // animate the tip
        new FadeIn(homeTips).setSpeed(0.5).play();
    }

    @FXML
    private void homeTipsClicked()
    {
        homeTips.setText(Database.getTip());
        new ZoomIn(homeTips).play();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //                                                                                //
    //                               Chat METHODS                                     //
    //                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////

    private void loadChat()
    {
        for(String friend: Database.getFriends())
            addChatHBox("pv" + friend);

//        for(String group: Database.getGroups())
//            addChatHBox("gp" + group);

        loadEmojis();

        chatTab.setOnSelectionChanged(event ->
        {
            if(chatSelected != null)
            {
                // reset the color of selected chat
                chatVBoxMembers.get(chatSelected).setStyle("-fx-background-color: rgba(0, 0, 0, 0.2)");
                chatSelected = null;
            }
            cleanMessages();

            // reset the label text
            chatLabel.setText("Please select a chat");
        });
    }

    private void showChat()
    {
        for(Node children: chatVBox.getChildren())
            new FadeIn(children).setSpeed(0.5).play();
    }

    private void loadEmojis()
    {
        String[] emojiNames = Database.getEmojiSmileys();
        int j = 0;
        for(String emoji: emojiNames)
        {
            ImageView emojiImageView = Database.getEmojiImageView(emoji, 30, 30);
            emojiImageView.setOnMouseClicked(event ->
            {
                chatMessageTextField.appendText(emoji);
                chatMessageTextField.requestFocus();
            });
            int row = j / 8;
            int column = j % 8;
            chatEmojiGridPane.getChildren().add(emojiImageView);
            GridPane.setConstraints(emojiImageView, column, row);
            j++;
        }
    }


    void chatSelected(String chatName)
    {
        if(!chatName.equals(chatSelected))
        {
            // reset the color of former selected chat
            if(chatSelected != null)
                chatVBoxMembers.get(chatSelected).setStyle("-fx-background-color: rgba(0, 0, 0, 0.2)");

            chatSelected = chatName;
            cleanMessages();
            loadMessages(chatSelected);
            new Thread(() -> Server.chatWasSeen(Database.getMe().getUsername(), chatSelected)).start();

            // changing toolbar
            chatLabel.setText(chatSelected.substring(2));

            // changing color of the new selected chat
            chatVBoxMembers.get(chatSelected).setStyle("-fx-background-color: #34495e");

            // make user able to write immediately
            chatMessageTextField.requestFocus();
        }
    }


    @FXML
    void clearHistoryClicked()
    {
        if(chatSelected != null)
            if(Main.showConfirmation())
            {
                boolean result = Server.clearHistory(Database.getMe().getUsername(), chatSelected);
                if(result)
                    clearHistory(chatSelected);
            }
    }

    @FXML
    void chatSendButtonClicked()
    {
        if(!chatMessageTextField.getText().isEmpty() && !chatMessageTextField.getText().matches("\\s+"))
            sendMessage();
    }

    @FXML
    void chatTextAreaKeyPressed(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ESCAPE)
        {
            hideEmojiScrollPane();
            return;
        }
        if(event.isControlDown() && event.getCode() == KeyCode.ENTER)
        {
            chatMessageTextField.appendText("\n");
            return;
        }
        if(event.getCode() == KeyCode.ENTER)
        {
            if(!chatMessageTextField.getText().isEmpty() && !chatMessageTextField.getText().matches("\\s+"))
            {
                sendMessage();
                hideEmojiScrollPane();
            }
        }
    }

    @FXML
    void emojiIconClicked()
    {
        boolean emojiScrollPaneIsShown = chatEmojiScrollPane.isVisible();
        if(emojiScrollPaneIsShown)
            hideEmojiScrollPane();
        else
            showEmojiScrollPane();

        chatMessageTextField.requestFocus();
    }

    @FXML
    void emojiScrollPaneMouseExited()
    {
        hideEmojiScrollPane();
    }

    private void showEmojiScrollPane()
    {
        chatEmojiScrollPane.setVisible(true);
        chatEmojiIcon.setEffect(new Glow(1.0));
    }
    private void hideEmojiScrollPane()
    {
        chatEmojiScrollPane.setVisible(false);
        chatEmojiIcon.setEffect(null);
    }

    private void loadMessages(String chatName)
    {
        for(MyMessage message: Database.getMessages(chatName))
            addMessageHBox(message);
    }

    // send the written text
    private void sendMessage()
    {
        String text = chatMessageTextField.getText();

        new Thread(() ->
        {
            MyMessage finalMessage = Server.sendMessage(Database.getMe().getUsername(), chatSelected, text);

            if(finalMessage != null)
            {
                Database.getMessages(chatSelected).add(finalMessage);
                Platform.runLater(() ->
                {
                    addMessageHBox(finalMessage);
                    chatMessageTextField.setText("");
                });
            }

        }).start();
    }

    // new message is received
    public void newMessage(String chatID, MyMessage message)
    {
        if(chatID.equals(chatSelected))
        {
            Platform.runLater(() -> addMessageHBox(message));
            new Thread(() -> Server.chatWasSeen(Database.getMe().getUsername(), chatSelected)).start();
        }
    }

    public void goToChat(String username)
    {
        String chatID = "pv" + username;
        tabPane.getSelectionModel().select(chatTab);
        chatSelected(chatID);
    }

    // this method is used when client deletes a message
    public void deleteMessage(int id)
    {
        if(Server.deleteMessage(Database.getMe().getUsername(), chatSelected, id))
        {
            removeMessageHBox(id);
            Database.removeMessage(chatSelected, id);
        }
    }

    // this method is used when someone else deleted a message
    public void deletedMessage(String chatID, int id)
    {
        if(chatSelected != null && chatSelected.equals(chatID))
            Platform.runLater(() -> removeMessageHBox(id));
    }

    public void editMessage(int id, String newText)
    {
        if(Server.editMessage(Database.getMe().getUsername(), chatSelected, id, newText))
            Database.editMessage(chatSelected, id, newText);
    }

    public void clearHistory(String chatID)
    {
        Platform.runLater(this::cleanMessages);
        Database.getMessages(chatID).clear();
    }

    private void cleanMessages()
    {
        messageControllers.clear();
        chatMessageVBox.getChildren().clear();
    }

    public void addChatHBox(String chatName)
    {
        if(chatName.startsWith("pv"))
        {
            String username = chatName.substring(2);
            MyUser user = Database.getUser(username);
            if(user == null)
                return;

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/home/ChatHBox.fxml"));
            try
            {
                HBox hbox = fxmlLoader.load();
                Platform.runLater(() -> chatVBox.getChildren().add(hbox));
                chatVBoxMembers.put(chatName, hbox);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            ChatHBoxController controller = fxmlLoader.getController();
            controller.initializeUser(user);
        } // else if(chatName.startsWith("gp"))
        //{
        // to be added
        //}
    }

    public void removeChatHBox(String chatName)
    {
        if(chatVBoxMembers.containsKey(chatName))
        {
            HBox hbox = chatVBoxMembers.get(chatName);
            chatVBoxMembers.remove(chatName);
            Platform.runLater(() -> chatVBox.getChildren().remove(hbox));

            // if this was the selected chat, messages should disappear
            if(chatSelected != null && chatSelected.equals(chatName))
                Platform.runLater(this::cleanMessages);
        }
    }

    private void addMessageHBox(MyMessage message)
    {
        boolean isMyMessage = message.getUsername().equals(Database.getMe().getUsername());

        FXMLLoader fxmlLoader;
        if(isMyMessage)
            fxmlLoader = new FXMLLoader(Main.class.getResource("ui/home/ChatRightMessage.fxml"));
        else
            fxmlLoader = new FXMLLoader(Main.class.getResource("ui/home/ChatLeftMessage.fxml"));

        try
        {
            HBox messageHBox = fxmlLoader.load();
            chatMessageVBox.getChildren().add(messageHBox);

            ChatMessageController controller = fxmlLoader.getController();
            controller.initialize(message);

            messageControllers.add(controller);

            KeyValue keyValue = new KeyValue(chatMessagesScrollPane.vvalueProperty(), 1.0, Interpolator.EASE_OUT);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(1500.0), keyValue);
            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void removeMessageHBox(int id)
    {
        for(int i = messageControllers.size() - 1; i >= 0; i--)
        {
            if(messageControllers.get(i).getId() == id)
            {
                chatMessageVBox.getChildren().remove(i);
                messageControllers.remove(i);
                break;
            }
        }
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
