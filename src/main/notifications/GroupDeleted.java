package main.notifications;

public class GroupDeleted implements Notification
{
    private final String groupName;

    public GroupDeleted(String groupName)
    {
        this.groupName = groupName;
    }

    public String getGroupName()
    {
        return groupName;
    }
}
