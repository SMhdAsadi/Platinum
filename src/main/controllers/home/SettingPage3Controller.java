package main.controllers.home;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.setting;

public class SettingPage3Controller implements Initializable
{
    @FXML private JFXToggleButton music;
    @FXML private JFXToggleButton soundFX;
    @FXML private JFXSlider volume;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setting.soundProperty().bind(music.selectedProperty());
        setting.sfxProperty().bind(soundFX.selectedProperty());
        setting.volumeProperty().bind(volume.valueProperty());
    }
}
