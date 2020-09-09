package main.notifications;

public class GroupKicked implements Notification
{
    private final String username;
    private final String groupName;

    public GroupKicked(String username, String groupName)
    {
        this.username = username;
        this.groupName = groupName;
    }

    public String getUsername()
    {
        return username;
    }

    public String getGroupName()
    {
        return groupName;
    }
}
