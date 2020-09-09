package main.controllers.loading;

import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.util.Duration;
import main.Database;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.homeController;
import static main.Main.soundManager;

public class Loading implements Initializable
{
    @FXML private JFXProgressBar progressBar;
    @FXML private Label tips;

    private String path;
    private Parent nextPage;

    private static final String HOME = "ui/home/Home.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Platform.runLater(() -> tips.setText(Database.getTip()));
    }

    public void start(String music, String path)
    {
        this.path = path;
        switch (music)
        {
            case "menu":
                soundManager.play("menu");
                break;
            case "game":
                soundManager.play("game");
                break;
        }

        loadNextPage();
        firstMove();
    }

    private void loadNextPage()
    {
        new Thread(() ->
        {
            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(path));
                nextPage = fxmlLoader.load();
                if(path.equals(HOME))
                    homeController = fxmlLoader.getController();
            } catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }).start();
    }

    private void firstMove()
    {
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 0.3);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(4), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.seconds(0));
        timeline.setOnFinished(event -> secondMove());
        timeline.play();
    }

    private void secondMove()
    {
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 0.45);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.millis(800));
        timeline.setOnFinished(event -> thirdMove());
        timeline.play();
    }

    private void thirdMove()
    {
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 0.9);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.millis(800));
        timeline.setOnFinished(event -> forthMove());
        timeline.play();
    }

    private void forthMove()
    {
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(800), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.millis(2000));
        timeline.setOnFinished(event ->
        {
            Main.loadFXML(nextPage);
            if(path.equals(HOME))
                homeController.showHome();
        });
        timeline.play();
    }
}
