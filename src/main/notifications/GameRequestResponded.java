package main.notifications;

public class GameRequestResponded implements Notification
{
    private final String username;
    private final String game;
    private final boolean response;

    public GameRequestResponded(String username, String game, boolean response)
    {
        this.username = username;
        this.game = game;
        this.response = response;
    }

    public String getUsername()
    {
        return username;
    }

    public String getGame()
    {
        return game;
    }

    public boolean isResponse()
    {
        return response;
    }
}
