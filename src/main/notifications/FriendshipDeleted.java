package main.notifications;

public class FriendshipDeleted implements Notification
{
    private final String username;

    public FriendshipDeleted(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }
}
