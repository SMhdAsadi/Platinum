package main.notifications;

public class FriendRequestResponded implements Notification
{
    private final String username;
    private final boolean respond;

    public FriendRequestResponded(String username, boolean respond)
    {
        this.username = username;
        this.respond = respond;
    }

    public String getUsername()
    {
        return username;
    }

    public boolean getRespond()
    {
        return respond;
    }
}
