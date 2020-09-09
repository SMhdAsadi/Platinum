package main.notifications;

public class GameRequested implements Notification
{
    private final String username;
    private final String game;

    public GameRequested(String username, String game)
    {
        this.username = username;
        this.game = game;
    }

    public String getUsername()
    {
        return username;
    }

    public String getGame()
    {
        return game;
    }
}
