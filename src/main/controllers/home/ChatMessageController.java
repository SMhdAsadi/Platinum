package main.controllers.home;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.Database;
import main.auxiliary.MyMessage;
import main.auxiliary.MyUser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Main.homeController;
import static main.Main.soundManager;

public class ChatMessageController
{
    @FXML private VBox vBox;
    @FXML private Label name;
    @FXML private TextFlow textFlow;
    @FXML private MaterialDesignIconView tick;
    @FXML private Label time;
    @FXML private Circle profile;

    private int id;
    private String text;
    private boolean isRightMessage;

    public void initialize(MyMessage message)
    {
        id = message.getId();
        text = message.getMessage();
        isRightMessage = message.getUsername().equals(Database.getMe().getUsername());

        MyUser user = Database.getUser(message.getUsername());
        if(user == null)
            return;

        if(!isRightMessage)
        {
            tick.setVisible(false);
        } else
        {
            if(message.isSeen())
                tick.setGlyphName("CHECK_ALL");
            else
                tick.setGlyphName("CHECK");
            message.isSeenProperty().addListener((observable, oldValue, newValue) ->
                    Platform.runLater(() ->
                    {
                        if(newValue) tick.setGlyphName("CHECK_ALL");
                    })
            );
        }

        name.setText(user.getName());
        user.nameProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> name.setText(newValue)));

        fillTextFlow(message.getMessage());
        message.messageProperty().addListener((observable, oldValue, newValue) ->
        {
            Platform.runLater(() -> fillTextFlow(newValue));
            text = newValue;
        });

        int timeLength = message.getTime().length();
        time.setText(message.getTime().substring(timeLength - 8, timeLength - 3));

        profile.setFill(Database.getAvatarImagePattern(user.getAvatar()));
        user.avatarProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> profile.setFill(Database.getAvatarImagePattern(newValue.intValue()))));
    }

    private void fillTextFlow(String message)
    {
        textFlow.getChildren().clear();

        List<String> messageArray = new ArrayList<>();
        Matcher m = Pattern.compile("\\P{M}\\p{M}*+").matcher(message);
        while (m.find())
            messageArray.add(m.group());

        String[] emojiSmileys = Database.getEmojiSmileys();
        for(String character: messageArray)
        {
            // this character is an emoji
            if(contains(emojiSmileys, character))
            {
                ImageView imageView = Database.getEmojiImageView(character, 20, 20);
                textFlow.getChildren().add(imageView);
            } else // this is not an emoji
            {
                ObservableList<Node> children = textFlow.getChildren();
                // we need to add a new Text node
                if(children.isEmpty() || children.get(children.size() - 1) instanceof ImageView)
                {
                    Text text = new Text(character);
                    text.setFont(new Font(15));
                    text.setFill(Color.WHITE);
                    children.add(text);
                } else // we need to use the last Text
                {
                    Text text = (Text) children.get(children.size() - 1);
                    text.setText(text.getText() + character);
                }
            }
        }
    }

    @FXML
    void messageContextRequested(ContextMenuEvent event)
    {
        JFXPopup contextPopup = new JFXPopup();
        VBox content = new VBox();

        if (isRightMessage)
        {
            JFXButton deleteMessage = new JFXButton("Delete Message");
            deleteMessage.setFocusTraversable(false);
            deleteMessage.getStyleClass().add("context-button");

            JFXButton editMessage = new JFXButton("Edit Message");
            editMessage.setFocusTraversable(false);
            editMessage.getStyleClass().add("context-button");

            content.getChildren().addAll(deleteMessage, editMessage);

            deleteMessage.setOnAction(event2 ->
            {
                soundManager.playButtonClick();

                homeController.deleteMessage(id);
                contextPopup.hide();
            });

            editMessage.setOnAction(event3 ->
            {
                soundManager.playButtonClick();

                contextPopup.hide();
                JFXPopup editPopup = new JFXPopup();

                TextArea textArea = new TextArea(text);
                textArea.getStyleClass().add("context-text-area");
                textArea.focusedProperty().addListener((observable, oldValue, newValue) ->
                {
                    if(newValue)
                        textArea.end();
                });
                textArea.setOnKeyPressed(event1 ->
                {
                    if(event1.isControlDown() && event1.getCode() == KeyCode.ENTER)
                    {
                        textArea.appendText("\n");
                        return;
                    }

                    if(event1.getCode() == KeyCode.ENTER)
                    {
                        homeController.editMessage(id, textArea.getText());
                        editPopup.hide();
                    }
                });

                JFXButton button = new JFXButton("Update");
                button.getStyleClass().add("edit-button");
                button.setOnAction(event1 ->
                {
                    soundManager.playButtonClick();

                    homeController.editMessage(id, textArea.getText());
                    editPopup.hide();
                });

                editPopup.setPopupContent(new VBox(textArea, button));
                editPopup.show(vBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
            });
        }

        JFXButton copyText = new JFXButton("Copy Text");
        copyText.setFocusTraversable(false);
        copyText.getStyleClass().add("context-button");

        copyText.setOnAction(event1 ->
        {
            soundManager.playButtonClick();

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(text);
            clipboard.setContent(clipboardContent);
            contextPopup.hide();
        });

        content.getChildren().add(0, copyText);

        contextPopup.setPopupContent(content);
        contextPopup.show(vBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());

    }

    public int getId()
    {
        return id;
    }

    private boolean contains(String[] array, String key)
    {
        for (String string : array)
        {
            if (string.equals(key))
                return true;
        }
        return false;
    }
}
