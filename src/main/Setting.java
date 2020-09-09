package main;

import javafx.beans.property.*;

public class Setting
{
    private final BooleanProperty sound = new SimpleBooleanProperty(this, "sound", true);
    private final BooleanProperty sfx = new SimpleBooleanProperty(this, "sfx", true);
    private final DoubleProperty volume = new SimpleDoubleProperty(this, "volume", 100.0);

    public final BooleanProperty soundProperty() { return sound; }

    public final boolean getSfx() { return sfx.get(); }
    public final BooleanProperty sfxProperty() { return sfx; }

    public final DoubleProperty volumeProperty() { return  volume; }
}
