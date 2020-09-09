package main.auxiliary;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable
{
    private int id;
    private final String username;
    private final String name;
    private final String time;
    private boolean isSeen;
    private String message;

    public Message(int id, String username, String name, long time, boolean isSeen, String message)
    {
        this.id = id;
        this.username = username;
        this.name = name;
        this.time = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(time));
        this.isSeen = isSeen;
        this.message = message;
    }

    public Message(String username, String name, long time, boolean isSeen, String message)
    {
        this.username = username;
        this.name = name;
        this.message = message;
        this.time = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(time));
        this.isSeen = isSeen;
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
        return name;
    }
    public String getTime()
    {
        return time;
    }
    public boolean isSeen()
    {
        return isSeen;
    }
    public String getMessage()
    {
        return message;
    }

    public void setSeen(boolean seen)
    {
        isSeen = seen;
    }
    public void setMessage(String message)
    {
        this.message = message;
    }
}
