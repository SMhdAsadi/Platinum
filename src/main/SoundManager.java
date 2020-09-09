package main;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Random;

import static main.Main.setting;

public class SoundManager
{
    /**
     *  a class for using music and audioClips in the game
     */
    private String currentMusic;

    private final MediaPlayer menu;
    private final MediaPlayer game;

    private final AudioClip buttonClickSound;
    private final AudioClip toggleButtonSound;
    private final AudioClip connectingToServerSound;
    private final AudioClip connectedToServerSound;
    private final AudioClip selectSound;
    private final AudioClip information;
    private final AudioClip warning;
    private final AudioClip error;
    private final AudioClip swipe;
    private final AudioClip notification;
    private final AudioClip newMessage;

    private final MediaPlayer[] mediaPlayers;
    private final AudioClip[] soundFXs;
    private final AudioClip[] types;

    public SoundManager()
    {
        currentMusic = "menu";
        Media media1 = new Media(Main.class.getResource("resources/sounds/menu.mp3").toExternalForm());
        Media media2 = new Media(Main.class.getResource("resources/sounds/game.mp3").toExternalForm());
        menu = new MediaPlayer(media1);
        menu.setCycleCount(Animation.INDEFINITE);

        game = new MediaPlayer(media2);
        game.setCycleCount(Animation.INDEFINITE);

        buttonClickSound = new AudioClip(Main.class.getResource("resources/sounds/button_click.wav").toExternalForm());
        toggleButtonSound = new AudioClip(Main.class.getResource("resources/sounds/toggle_button.wav").toExternalForm());
        connectingToServerSound = new AudioClip(Main.class.getResource("resources/sounds/connecting_to_server.wav").toExternalForm());
        connectedToServerSound = new AudioClip(Main.class.getResource("resources/sounds/connected_to_server.wav").toExternalForm());
        selectSound = new AudioClip(Main.class.getResource("resources/sounds/select.wav").toExternalForm());
        information = new AudioClip(Main.class.getResource("resources/sounds/information.wav").toExternalForm());
        warning = new AudioClip(Main.class.getResource("resources/sounds/warning.wav").toExternalForm());
        error = new AudioClip(Main.class.getResource("resources/sounds/error.wav").toExternalForm());
        swipe = new AudioClip(Main.class.getResource("resources/sounds/swipe.wav").toExternalForm());
        notification = new AudioClip(Main.class.getResource("resources/sounds/notification.wav").toExternalForm());
        newMessage = new AudioClip(Main.class.getResource("resources/sounds/newMessage.wav").toExternalForm());

        AudioClip type1 = new AudioClip(Main.class.getResource("resources/sounds/type1.wav").toExternalForm());
        AudioClip type2 = new AudioClip(Main.class.getResource("resources/sounds/type2.wav").toExternalForm());
        AudioClip type3 = new AudioClip(Main.class.getResource("resources/sounds/type3.wav").toExternalForm());
        AudioClip type4 = new AudioClip(Main.class.getResource("resources/sounds/type4.wav").toExternalForm());

        mediaPlayers = new MediaPlayer[]{menu, game};
        soundFXs = new AudioClip[]{buttonClickSound, toggleButtonSound, connectingToServerSound, connectedToServerSound,
                selectSound, information, warning, error, swipe, notification, newMessage, type1, type2, type3, type4};
        types = new AudioClip[]{type1, type2, type3, type4};
    }

    public void play()
    {
        play(currentMusic);
    }

    public void play(String musicName)
    {
        currentMusic = musicName;
        stopAll();

        switch (musicName)
        {
            case "menu":
                menu.setVolume(0);
                menu.play();
                new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(menu.volumeProperty(), 1))).play();
                break;
            case "game":
                game.setVolume(0);
                game.play();
                new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(game.volumeProperty(), 1))).play();
                break;
        }
    }

    public void stopAll()
    {
        for(MediaPlayer mediaPlayer: mediaPlayers)
            mediaPlayer.stop();
    }

    public void setVolume(Double value)
    {
        for(MediaPlayer mediaPlayer: mediaPlayers)
            mediaPlayer.setVolume(value);

        for(AudioClip soundFx: soundFXs)
            soundFx.setVolume(value);

    }

    public void playButtonClick() { if(setting.getSfx()) buttonClickSound.play(); }
    public void playToggleClick() { if(setting.getSfx()) toggleButtonSound.play(); }
    public void playConnectingToServer() { if(setting.getSfx()) connectingToServerSound.play(); }
    public void playConnectedToServer() { if(setting.getSfx()) connectedToServerSound.play(); }
    public void playSelect() { if(setting.getSfx()) selectSound.play(); }
    public void playType() { if(setting.getSfx()) types[new Random().nextInt(types.length)].play(); }
    public void playInformation() { if(setting.getSfx()) information.play(); }
    public void playWarning() { if(setting.getSfx()) warning.play(); }
    public void playError() { if(setting.getSfx()) error.play(); }
    public void playSwipe() { if(setting.getSfx()) swipe.play(); }
    public void playNotification() { if(setting.getSfx()) notification.play(); }
    public void playNewMessage() { if(setting.getSfx()) newMessage.play(); }
}
