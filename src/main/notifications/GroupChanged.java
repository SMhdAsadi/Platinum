package main.notifications;

import main.auxiliary.Group;

public class GroupChanged implements Notification
{
    private final Group newGroup;

    public GroupChanged(Group newGroup)
    {
        this.newGroup = newGroup;
    }

    public Group getNewGroup()
    {
        return newGroup;
    }
}
