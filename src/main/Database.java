package main;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import main.auxiliary.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Database
{
    private final static String[] tips = new String[]{"You can add friends in friends tab",
            "You can play game in games tab", "You can chat with your friends in chat tab",
            "You can change you avatar in the setting tab", "You can change your name in the setting tab",
            "You can change your bio in the setting tab", "You can create group with your friends",
            "Online games have more points than offline games", "Each game has points, even if you loose"};

    private final static String avatarPath = "main/resources/pictures/avatar";

    private static MyUser me;
    private static MyUser[] users;
    private static String[] friends;
    private static String[] friendRequests;
    private static Group[] groups;
    private static Map<String, Message[]> messages = new HashMap<>();
    private static GameStatistics gameStatistics;
    private static MyUser[] topPlayers = new MyUser[3];

    public static void loadInfo()
    {
        Server.getData(me.getUsername());
    }

    public static boolean hasUser(String username) {
        return contains(getUsernames(), username);
    }
    public static boolean hasFriendRequest(String username) {
        return contains(getFriendRequests(), username);
    }

    public static void changeUser(User newUser) {
        MyUser oldUser = Database.getUser(newUser.getUsername());
        if(oldUser == null)
            return;

        oldUser.setName(newUser.getName());
        oldUser.setBio(newUser.getBio());
        oldUser.setPassword(newUser.getPassword());
        oldUser.setAvatar(newUser.getAvatar());
        oldUser.setLastSeen(newUser.getLastSeen());
        oldUser.setStatus(newUser.getStatus());
        oldUser.setScore(newUser.getScore());
    }
    public static void addUser(User newUser) {
        MyUser[] newUsers = new MyUser[users.length + 1];
        System.arraycopy(users, 0, newUsers, 0, users.length);
        newUsers[users.length] = new MyUser(newUser);

        users = newUsers;
    }
    public static void addFriendRequest(String newRequest) {
        friendRequests = addToArray(friendRequests, newRequest);
    }
    public static void addFriend(String friend) {
        friends = addToArray(friends, friend);
    }
    public static void removeFriendRequest(String request) {
        friendRequests = deleteFromArray(friendRequests, request);
    }
    public static void removeFriend(String friend) {
        friends = deleteFromArray(friends, friend);
    }

    public static MyUser getUser(String username) {
        for(MyUser user: users)
        {
            if(user.getUsername().equals(username))
                return user;
        }

        return null;
    }
    public static String[] getUsernames() {
        String[] usernames = new String[users.length - 1];
        for(int i = 0, j = 0; i < users.length; i++)
        {
            if(users[i].getUsername().equals(me.getUsername()))
                continue;

            usernames[j] = users[i].getUsername();
            j++;
        }

        return usernames;
    }

    public static ImagePattern getAvatarImagePattern(int avatarNumber) {
        return new ImagePattern(new Image(avatarPath + avatarNumber + ".png"));
    }

    // getters
    public static String getTip() { return tips[new Random().nextInt(tips.length)]; }
    public static MyUser getMe()
    {
        return me;
    }
    public static MyUser[] getUsers()
    {
        return users;
    }
    public static String[] getFriends()
    {
        return friends;
    }
    public static Group[] getGroups()
    {
        return groups;
    }
    public static Map<String, Message[]> getMessages()
    {
        return messages;
    }
    public static MyUser[] getTopPlayers()
    {
        return topPlayers;
    }
    public static GameStatistics getGameStatistics()
    {
        return gameStatistics;
    }
    public static String[] getFriendRequests()
    {
        return friendRequests;
    }

    // setters
    public static void setMe(MyUser me)
    {
        Database.me = me;
    }
    public static void setUsers(MyUser[] users)
    {
        Database.users = users;
    }
    public static void setFriends(String[] friends)
    {
        Database.friends = friends;
    }
    public static void setGroups(Group[] groups)
    {
        Database.groups = groups;
    }
    public static void setMessages(Map<String, Message[]> messages)
    {
        Database.messages = messages;
    }
    public static void setTopPlayers(MyUser[] topPlayers)
    {
        Database.topPlayers = topPlayers;
    }
    public static void setGameStatistics(GameStatistics gameStatistics)
    {
        Database.gameStatistics = gameStatistics;
    }
    public static void setFriendRequests(String[] friendRequests)
    {
        Database.friendRequests = friendRequests;
    }

    // helping methods
    private static boolean contains(String[] array, String key) {
        for (String s : array)
        {
            if (s.equals(key))
                return true;
        }
        return false;
    }
    private static String[] addToArray(String[] array, String key) {
        String[] temp = new String[array.length + 1];
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = key;
        array = temp;
        return array;
    }
    private static String[] deleteFromArray(String[] array, String key) {
        String[] temp = new String[array.length - 1];
        int i = 0;
        for(String userFriend : array)
        {
            if(!userFriend.equals(key))
            {
                temp[i] = userFriend;
                i++;
            }
        }
        array = temp;
        return array;
    }
}
