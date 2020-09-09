package main.notifications;

public class GroupAdded implements Notification
{
    private final String username;
    private final String groupName;

    public GroupAdded(String username, String groupName)
    {
        this.username = username;
        this.groupName = groupName;
    }

    public String getUsername()
    {
        return username;
    }

    public String getGroup()
    {
        return groupName;
    }
}
