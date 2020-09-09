package main.notifications;

import main.auxiliary.User;

public class UserAdded implements Notification
{
    private final User newUser;

    public UserAdded(User newUser)
    {
        this.newUser = newUser;
    }

    public User getNewUser()
    {
        return newUser;
    }
}
