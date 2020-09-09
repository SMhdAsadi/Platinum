package main.auxiliary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Group
{
    private final String groupName;
    private String name;
    private String bio;
    private int avatar;
    private final String dateCreated;
    private final String creatorUsername;
    private LinkedList<MyUser> usersList;
    private String[] users;

    public Group(String groupName, String name, String bio, int avatar, long dateCreated, String creatorUsername, LinkedList<MyUser> usersList)
    {
        this.groupName = groupName;
        this.name = name;
        this.bio = bio;
        this.avatar = avatar;
        this.dateCreated = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(dateCreated));
        this.creatorUsername = creatorUsername;
        this.usersList = usersList;
    }

    public Group(String groupName, String name, String bio, int avatar, long dateCreated, String creatorUsername, String[] users)
    {
        this.groupName = groupName;
        this.name = name;
        this.bio = bio;
        this.avatar = avatar;
        this.dateCreated = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(dateCreated));
        this.creatorUsername = creatorUsername;
        this.users = users;
    }

    public String getGroupName()
    {
        return groupName;
    }
    public String getDateCreated()
    {
        return dateCreated;
    }
    public String getCreatorUsername()
    {
        return creatorUsername;
    }
    public String getName()
    {
        return name;
    }
    public String getBio()
    {
        return bio;
    }
    public int getAvatar()
    {
        return avatar;
    }
    public LinkedList<MyUser> getUsersList()
    {
        return usersList;
    }
    public String[] getUsers()
    {
        return users;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public void setBio(String bio)
    {
        this.bio = bio;
    }
    public void setAvatar(int avatar)
    {
        this.avatar = avatar;
    }
}
