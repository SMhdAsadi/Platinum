package main.auxiliary;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MyMessage
{
    private final int id;
    private final String username;
    private final StringProperty name = new SimpleStringProperty(this, "name", "");
    private final String time;
    private final BooleanProperty isSeen = new SimpleBooleanProperty(this, "isSeen", false);
    private final StringProperty message = new SimpleStringProperty(this, "message", "");

    public MyMessage(Message message)
    {
        id = message.getId();
        username = message.getUsername();
        name.set(message.getName());
        time = message.getTime();
        isSeen.set(message.isSeen());
        this.message.set(message.getMessage());
    }

    public MyMessage(int id, String username, String name, String time, boolean isSeen, String message)
    {
        this.id = id;
        this.username = username;
        this.name.set(name);
        this.time = time;
        this.isSeen.set(isSeen);
        this.message.set(message);
    }

    public int getId()
    {
        return id;
    }
    public String getUsername()
    {
        return username;
    }
    public String getName()
    {
        return name.get();
    }
    public String getTime()
    {
        return time;
    }
    public boolean isSeen()
    {
        return isSeen.get();
    }
    public String getMessage()
    {
        return message.get();
    }

    public StringProperty nameProperty()
    {
        return name;
    }
    public BooleanProperty isSeenProperty()
    {
        return isSeen;
    }
    public StringProperty messageProperty()
    {
        return message;
    }

    public void setName(String name)
    {
        this.name.set(name);
    }
    public void setIsSeen(boolean isSeen)
    {
        this.isSeen.set(isSeen);
    }
    public void setMessage(String message)
    {
        this.message.set(message);
    }
}
