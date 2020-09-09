package main.notifications;

import main.auxiliary.User;

public class UserChanged implements Notification
{
    private final User newUser;

    public UserChanged(User newUser)
    {
        this.newUser = newUser;
    }

    public User getNewUser()
    {
        return newUser;
    }
}
