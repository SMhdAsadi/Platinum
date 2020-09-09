package main.controllers.entrance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EntranceController implements Initializable
{
    @FXML
    private MediaView mediaView;

    private Parent nextPane;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        new Thread(() ->
        {
            try
            {
                nextPane = FXMLLoader.load(Main.class.getResource("ui/login/Login.fxml"));
            } catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }).start();

        Media media = new Media(Main.class.getResource("resources/videos/intro.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnEndOfMedia(() -> Main.loadFXML(nextPane));

        mediaView.setMediaPlayer(mediaPlayer);
    }
}
