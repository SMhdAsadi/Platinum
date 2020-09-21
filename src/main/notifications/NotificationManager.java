package main.notifications;

import main.Database;
import main.auxiliary.MyMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import static main.Main.homeController;
import static main.Main.soundManager;

public class NotificationManager
{
    private static Socket connection;
    private static boolean flag = true;

    public static void start(Socket socket)
    {
        connection = socket;
        flag = true;

        try(ObjectInputStream fromServer = new ObjectInputStream(connection.getInputStream());
            DataOutputStream toServer = new DataOutputStream(connection.getOutputStream()))
        {
            toServer.writeUTF(Database.getMe().getUsername());

            while (flag)
            {
                toServer.writeBoolean(true);
                int notificationCount = fromServer.readInt();

                for (int i = 0; i < notificationCount; i++)
                {
                    handleNotification((Notification) fromServer.readObject());
                }
                Thread.sleep(500);
            }
            toServer.writeBoolean(false);

            connection = null;
        } catch (IOException | ClassNotFoundException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void stop()
    {
        if(connection != null)
        {
            flag = false;
        }
    }

    private static void handleNotification(Notification notification)
    {
        if(notification instanceof UserAdded) userAdded((UserAdded) notification);
        if(notification instanceof ChatCleared) chatCleared((ChatCleared) notification);
        else if(notification instanceof ChatWasSeen) chatWasSeen((ChatWasSeen) notification);
        else if(notification instanceof FriendRequested) friendRequested((FriendRequested) notification);
        else if(notification instanceof FriendRequestResponded) friendRequestResponded((FriendRequestResponded) notification);
        else if(notification instanceof FriendshipDeleted) friendShipDeleted((FriendshipDeleted) notification);
//            else if(notification instanceof GameRequested) gameRequested();
//            else if(notification instanceof GameRequestResponded) gameRequestResponded();
//            else if(notification instanceof GroupAdded) groupAdded();
//            else if(notification instanceof GroupChanged) groupChanged();
//            else if(notification instanceof GroupCreated) groupCreated();
//            else if(notification instanceof GroupDeleted) groupDeleted();
//            else if(notification instanceof GroupKicked) groupKicked();
        else if(notification instanceof MessageAdded) messageAdded((MessageAdded) notification);
            else if(notification instanceof MessageDeleted) messageDeleted((MessageDeleted) notification);
            else if(notification instanceof MessageEdited) messageEdited((MessageEdited) notification);
        else if(notification instanceof UserChanged) userChanged((UserChanged) notification);
    }

    private static void chatCleared(ChatCleared notification)
    {
        soundManager.playNotification();
        String chatID = notification.getChatID();

        homeController.clearHistory(chatID);
    }

    private static void chatWasSeen(ChatWasSeen notification)
    {
        String chatID = notification.getChatID();

        for(MyMessage message: Database.getMessages(chatID))
            message.setIsSeen(true);
    }

    private static void friendRequested(FriendRequested notification)
    {
        soundManager.playNotification();

        String username = notification.getUsername();
        homeController.addFriendRequestReceived(username);
        Database.addFriendRequest(notification.getUsername());
    }

    private static void friendRequestResponded(FriendRequestResponded notification)
    {
        soundManager.playNotification();

        String username = notification.getUsername();
        boolean respond = notification.getRespond();

        if(respond)
        {
            Database.addFriend(username);
            homeController.addFriend(username);
            homeController.addChatHBox("pv" + username);
        }

        homeController.removeRequest(username);
    }

    private static void friendShipDeleted(FriendshipDeleted notification)
    {
        soundManager.playNotification();

        String username = notification.getUsername();

        homeController.removeFriend(username);
        homeController.removeChatHBox("pv" + username);
        Database.removeFriend(username);
    }

    private static void userAdded(UserAdded notification)
    {
        Database.addUser(notification.getNewUser());
    }

    private static void messageAdded(MessageAdded notification)
    {
        soundManager.playNewMessage();

        String chatID = notification.getChatID();
        MyMessage message = new MyMessage(notification.getMessage());
        Database.getMessages(chatID).add(message);

        homeController.newMessage(chatID, message);
    }

    private static void messageDeleted(MessageDeleted notification)
    {
        String chatID = notification.getChatID();
        int id = notification.getMessageID();
        Database.removeMessage(chatID, id);

        homeController.deletedMessage(chatID, id);
    }

    private static void messageEdited(MessageEdited notification)
    {
        String chatID = notification.getChatID();
        int id = notification.getMessageID();
        String newText = notification.getNewMessageText();

        Database.editMessage(chatID, id, newText);
    }

    private static void userChanged(UserChanged notification)
    {
        Database.changeUser(notification.getNewUser());
    }
}
