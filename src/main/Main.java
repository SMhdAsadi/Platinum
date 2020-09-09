package main;

import animatefx.animation.FadeIn;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.constants.PortConstants;
import main.controllers.home.HomeController;
import main.controllers.loading.Loading;

import java.io.IOException;

public class Main extends Application implements PortConstants
{
    public static Scene scene;
    public static Setting setting;
    public static SoundManager soundManager;
    public static HomeController homeController;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        settingConfiguration();

//        Parent entrance = FXMLLoader.load(getClass().getResource("ui/entrance/Entrance.fxml"));
        Parent entrance = FXMLLoader.load(getClass().getResource("ui/login/Login.fxml"));
        Scene scene = new Scene(entrance, 600, 600);
        Main.scene = scene;

        primaryStage.getIcons().add(new Image("file:src/main/resources/pictures/icon.jpg"));

        primaryStage.setMinWidth(606);
        primaryStage.setMaxWidth(606);

        primaryStage.setMinHeight(629);
        primaryStage.setMaxHeight(629);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Platinum");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> Main.closeProgram());
    }

    public void settingConfiguration()
    {
        soundManager = new SoundManager();

        // configuring settings
        setting = new Setting();
        setting.soundProperty().addListener(((observable, oldValue, newValue) ->
        {
            if(newValue) soundManager.play();
            else soundManager.stopAll();
        }));

        setting.volumeProperty().addListener(
                (observable, oldValue, newValue) -> soundManager.setVolume(newValue.doubleValue() / 100));
    }

    public static void loadTo(String path)
    {
        switch (path)
        {
            case "ui/home/Home.fxml":
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/loading/Loading.fxml"));
                try
                {
                    scene.setRoot(fxmlLoader.load());
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                Loading loading = fxmlLoader.getController();
                loading.start("menu", path);
                Database.loadInfo();
                Server.connect();
                break;
            case "ui/games/ticTacToe.fxml":
                break;
        }
    }

    public static void loadFXML(String url)
    {
        new Thread(() ->
        {
            try
            {
                Parent pane = FXMLLoader.load(Main.class.getResource(url));
                loadFXML(pane);
            } catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }).start();
    }

    public static void loadFXML(Parent pane)
    {
        new Thread(() ->
        {
            Platform.runLater(() -> scene.setRoot(pane));
            new FadeIn(pane).setSpeed(0.7).play();
        }).start();
    }

    public static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText)
    {
        Platform.runLater(() ->
        {
            Alert alert;
            if (alertType == Alert.AlertType.ERROR)
            {
                alert = new Alert(Alert.AlertType.ERROR);
                soundManager.playError();
            }
            else if (alertType == Alert.AlertType.WARNING)
            {
                alert = new Alert(Alert.AlertType.WARNING);
                soundManager.playWarning();
            }
            else
            {
                alert = new Alert(Alert.AlertType.INFORMATION);
                soundManager.playInformation();
            }

            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);

            DialogPane dialogPane1 = alert.getDialogPane();
            dialogPane1.getStylesheets().add(Main.class.getResource("ui/AlertStyle.css").toExternalForm());

            Stage stage = (Stage) dialogPane1.getScene().getWindow();
            stage.getIcons().add(new Image("file:src/main/resources/pictures/icon.jpg"));

            alert.showAndWait();
        });
    }

    public static boolean showConfirmation()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "do you really want to confirm?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are You Sure?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Main.class.getResource("ui/AlertStyle.css").toExternalForm());

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image("file:src/main/resources/pictures/icon.jpg"));

        alert.showAndWait();

        return alert.getResult() == ButtonType.YES;
    }

    public static void closeProgram()
    {
        Server.logout(Database.getMe());
        Server.disconnect();

        try
        {
            Thread.sleep(500);
        } catch (InterruptedException exception)
        {
            exception.printStackTrace();
        }

        System.exit(0);
    }
}
