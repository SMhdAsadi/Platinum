package main.notifications;

import main.auxiliary.Group;

public class GroupCreated implements Notification
{
    private final Group group;

    public GroupCreated(Group group)
    {
        this.group = group;
    }

    public Group getGroup()
    {
        return group;
    }
}
