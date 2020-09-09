package main.notifications;

import main.Database;

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
//        if(notification instanceof ChatCleared) chatCleared();
//            else if(notification instanceof ChatWasSeen) chatWasSeen();
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
//            else if(notification instanceof MessageAdded) messageAdded();
//            else if(notification instanceof MessageDeleted) messageDeleted();
//            else if(notification instanceof MessageEdited) messageEdited();
        else if(notification instanceof UserChanged) userChanged((UserChanged) notification);
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
        }

        homeController.removeRequest(username);
    }

    private static void friendShipDeleted(FriendshipDeleted notification)
    {
        soundManager.playNotification();

        String username = notification.getUsername();

        homeController.removeFriend(username);
        Database.removeFriend(username);
    }

    private static void userAdded(UserAdded notification)
    {
        Database.addUser(notification.getNewUser());
    }

    private static void userChanged(UserChanged notification)
    {
        Database.changeUser(notification.getNewUser());
    }
}
