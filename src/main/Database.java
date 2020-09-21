package main;

import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import main.auxiliary.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Database
{
    private final static String[] tips = new String[]{"see your friend requests in friends tab",
            "Enter sends message, user ctrl + enter for next line", "you are able to delete or edit any message",
            "You can change your password in the settings tab", "You can change your profile in the settings tab",
            "click on me", "You can create group with your friends", "Online games have more points than offline games",
            "Each game has points, even if you loose"};

    private final static String avatarPath = "main/resources/pictures/avatar";

    private static MyUser me;
    private static MyUser[] users;
    private static String[] friends;
    private static String[] friendRequests;
    private static Group[] groups;
    private static Map<String, LinkedList<MyMessage>> messages = new HashMap<>();
    private static GameStatistics gameStatistics;
    private static MyUser[] topPlayers = new MyUser[3];

    public static final int EMOJI_COUNT = 176;
    private static final String[] emojiFileNames = new String[EMOJI_COUNT];
    private static final String[] emojiSmileys = new String[EMOJI_COUNT];

    public static void loadInfo() {
        Server.getData(me.getUsername());
        loadEmojis();
    }
    private static void loadEmojis() {
        File emojiDir = new File(Main.class.getResource("resources/pictures/emoji").getPath());
        File[] emojiFiles = emojiDir.listFiles();
        if(emojiFiles == null)
        {
            Main.showAlert(Alert.AlertType.ERROR, "Error", "Cannot Load Emojis",
                    "cannot load emojis maybe they are deleted from the app");
            return;
        }

        int i = 0;
        for(File emoji: emojiFiles)
        {
            emojiFileNames[i] = emoji.getName();
            String emojiName = new String(Character.toChars(
                    Integer.parseInt(emoji.getName().replace(".png", "").substring(1), 16)));
            emojiSmileys[i] = emojiName;
            i++;
        }
    }

    public static String[] getEmojiSmileys()
    {
        return emojiSmileys;
    }
    public static ImageView getEmojiImageView(String emojiSmiley, int width, int height) {
        int index = indexOf(emojiSmileys, emojiSmiley);
        ImageView imageView = new ImageView(new Image(
                "file:" + Main.class.getResource("resources/pictures/emoji/" + emojiFileNames[index]).getPath()));
        imageView.setFitHeight(width);
        imageView.setFitWidth(height);
        imageView.setSmooth(true);
        imageView.setEffect(new DropShadow());
        imageView.getStyleClass().add("emoji");
        return imageView;
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

        LinkedList<MyMessage> initialMessages = new LinkedList<>();

        MyUser me = Database.getMe();
        MyUser friendUser = Database.getUser(friend);
        String time = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));
        if(friendUser == null)
            return;

        initialMessages.add(
                new MyMessage(1, friend, friendUser.getName(), time, false,
                        friend + " has joined the chat"));
        initialMessages.add(new MyMessage(2, me.getUsername(), me.getName(), time, false,
                Database.getMe().getUsername() + " has joined the chat"));

        messages.put("pv" + friend, initialMessages);
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

    public static void removeMessage(String chatID, int id)
    {
        LinkedList<MyMessage> chatMessages = messages.get(chatID);
        for(int i = chatMessages.size() - 1; i >= 0; i--)
        {
            if(chatMessages.get(i).getId() == id)
            {
                chatMessages.remove(i);
                break;
            }
        }
    }

    public static void editMessage(String chatID, int id, String newText)
    {
        LinkedList<MyMessage> chatMessages = messages.get(chatID);
        for(int i = chatMessages.size() - 1; i >= 0; i--)
        {
            if(chatMessages.get(i).getId() == id)
            {
                chatMessages.get(i).setMessage(newText);
                break;
            }
        }
    }

    public static ImagePattern getAvatarImagePattern(int avatarNumber)
    {
        return new ImagePattern(new Image(avatarPath + avatarNumber + ".png"));
    }

    // getters
    public static String getTip() { return tips[new Random().nextInt(tips.length)]; }
    public static MyUser getMe()
    {
        return me;
    }
    public static String[] getFriends()
    {
        return friends;
    }
    public static Group[] getGroups()
    {
        return groups;
    }
    public static LinkedList<MyMessage> getMessages(String chat)
    {
        return messages.get(chat);
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
    public static void setMessages(Map<String, LinkedList<MyMessage>> messages)
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
    private static int indexOf(String[] array, String key) {
        for(int i = 0; i < array.length; i++)
            if(array[i].equals(key))
                return i;

        return -1;
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
