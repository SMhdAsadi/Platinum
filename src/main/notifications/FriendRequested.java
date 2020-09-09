package main.notifications;

public class FriendRequested implements Notification
{
    private final String username;

    public FriendRequested(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }
}
