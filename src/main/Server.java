package main;

import animatefx.animation.FadeIn;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import main.auxiliary.*;
import main.notifications.*;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static main.Main.*;
import static main.constants.PortConstants.LOGIN;

public class Server
{
    public static Socket getSocketWithServerLabel(int port, Label server)
    {
        Socket socket = null;

        soundManager.playConnectingToServer();
        Platform.runLater(() ->
        {
            server.setVisible(true);
            new FadeIn(server).setSpeed(0.5).play();
        });

        try
        {
            socket = new Socket("localhost", port);
            Thread.sleep(1000);

            Platform.runLater(() -> server.setText("Connected to server"));
            soundManager.playConnectedToServer();
            Thread.sleep(500);

            Platform.runLater(() ->
            {
                server.setVisible(false);
                server.setText("connecting to server");
            });
        } catch (ConnectException exception)
        {
            connectionErrorWithServerLabel(server);
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
        return socket;
    }

    public static Socket getSocket(int port)
    {
        Socket socket = null;

        try
        {
            socket = new Socket("localhost", port);
        } catch (ConnectException exception)
        {
            showAlert(Alert.AlertType.ERROR, "Error", "Connection Error",
                    "Couldn't connect to server, please make sure you've run PlatinumServer");
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return socket;
    }

    private static void connectionErrorWithServerLabel(Label server)
    {
        // react to user for connection problem
        Platform.runLater(() -> server.setText("couldn't connect to server!"));
        try
        {
            Thread.sleep(1500);
        } catch (InterruptedException exception2)
        {
            exception2.printStackTrace();
        }

        // reset the server label and spinner
        Platform.runLater(() ->
        {
            server.setVisible(false);
            server.setText("connecting to server");
        });

        showAlert(Alert.AlertType.ERROR, "Error", "Connection Error",
                "Couldn't connect to server, please make sure you've run PlatinumServer");
    }

    public static int login(String username, String password, Label server)
    {
        Socket socket = getSocketWithServerLabel(LOGIN, server);
        if (socket == null)
            return -1;

        try (DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(username);
            toServer.writeUTF(password);

            int result = fromServer.readInt();
            if (result == 3)
            {
                Database.setMe(new MyUser((User) fromServer.readObject()));
            }

            return result;
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return -1;
    }

    public static void logout(MyUser me)
    {
        if (me == null)
            return;

        // make the user offline and update lastSeen
        Socket socket = getSocket(LOGOUT);
        if (socket == null)
            return;

        try (DataOutputStream toServer = new DataOutputStream(socket.getOutputStream()))
        {
            toServer.writeUTF(me.getUsername());
        } catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public static String restorePassword(String username, String question, String answer, Label server)
    {
        Socket socket = getSocketWithServerLabel(RESTORE_PASSWORD, server);
        if (socket == null)
            return null;

        try (DataInputStream fromServer = new DataInputStream(socket.getInputStream());
             DataOutputStream toServer = new DataOutputStream(socket.getOutputStream()))
        {
            toServer.writeUTF(username);
            toServer.writeUTF(question);
            toServer.writeUTF(answer);

            return fromServer.readUTF();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static int checkUsernameAndPhone(String username, String phone, Label server)
    {
        Socket socket = getSocketWithServerLabel(SIGN_UP, server);
        if (socket == null)
            return -1;

        try (DataInputStream fromServer = new DataInputStream(socket.getInputStream());
             ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream()))
        {
            toServer.writeBoolean(true);
            toServer.writeUTF(username);
            toServer.writeUTF(phone);
            toServer.flush();

            return fromServer.readInt();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean signUp(MyUser me, Label server)
    {
        Socket socket = getSocketWithServerLabel(SIGN_UP, server);
        if (socket == null)
            return false;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             DataInputStream fromServer = new DataInputStream(socket.getInputStream()))
        {
            toServer.writeBoolean(false);
            toServer.writeObject(me.getUser());
            toServer.flush();

            return fromServer.readBoolean();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static void getData(String username)
    {
        Socket socket = getSocket(GET_DATA);
        if (socket == null)
            return;

        try (ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
             DataOutputStream toServer = new DataOutputStream(socket.getOutputStream()))
        {
            toServer.writeUTF(username);

            User[] users = (User[]) fromServer.readObject();
            MyUser[] myUsers = new MyUser[users.length];
            for (int i = 0; i < users.length; i++)
                myUsers[i] = new MyUser(users[i]);
            Database.setUsers(myUsers);

            Database.setFriends((String[]) fromServer.readObject());

            User[] topPlayers = (User[]) fromServer.readObject();
            MyUser[] myTopPlayers = new MyUser[topPlayers.length];
            for (int i = 0; i < topPlayers.length; i++)
            {
                if(topPlayers[i] == null)
                    myTopPlayers[i] = null;
                else
                    myTopPlayers[i] = new MyUser(topPlayers[i]);
            }
            Database.setTopPlayers(myTopPlayers);

            Database.setGroups((Group[]) fromServer.readObject());
            Database.setGameStatistics((GameStatistics) fromServer.readObject());
            Database.setFriendRequests((String[]) fromServer.readObject());

            Map<String, LinkedList<MyMessage>> allMessages = new HashMap<>();
            int messageSize = fromServer.readInt();
            for (int i = 0; i < messageSize; i++)
            {
                String id = fromServer.readUTF();
                Message[] messages = (Message[]) fromServer.readObject();

                // convert Message array to MyMessage linked list
                LinkedList<MyMessage> myMessages = new LinkedList<>();
                for (Message message : messages) myMessages.add(new MyMessage(message));

                allMessages.put(id, myMessages);
            }
            Database.setMessages(allMessages);
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean updateProfile(MyUser me, Label server)
    {
        Socket socket = getSocketWithServerLabel(MAIN_MENU, server);
        if (socket == null)
            return false;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(me.getUsername());
            Notification userChangedNotification = new UserChanged(me.getUser());
            toServer.writeObject(userChangedNotification);
            toServer.flush();

            return fromServer.readBoolean();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static int newFriendRequest(String myUsername, String friendUsername, Label server)
    {
        Socket socket = getSocketWithServerLabel(MAIN_MENU, server);
        if (socket == null)
            return -1;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(myUsername);
            Notification friendRequested = new FriendRequested(friendUsername);
            toServer.writeObject(friendRequested);
            toServer.flush();

            return fromServer.readInt();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean respondFriendRequest(String myUsername, String friendUsername, boolean respond)
    {
        Socket socket = getSocket(MAIN_MENU);
        if(socket == null)
            return false;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(myUsername);
            Notification friendRequestResponded = new FriendRequestResponded(friendUsername, respond);
            toServer.writeObject(friendRequestResponded);
            toServer.flush();

            return fromServer.readBoolean();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteFriend(String myUsername, String friendUsername)
    {
        Socket socket = getSocket(MAIN_MENU);
        if(socket == null)
            return false;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(myUsername);
            Notification deleteFriend = new FriendshipDeleted(friendUsername);
            toServer.writeObject(deleteFriend);
            toServer.flush();

            return fromServer.readBoolean();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static MyMessage sendMessage(String username, String chatID, String text)
    {
        MyUser user = Database.getUser(username);
        if(user == null)
            return null;

        Message message = new Message(username, user.getName(), System.currentTimeMillis(), false, text);

        Socket socket = getSocket(MAIN_MENU);
        if(socket == null)
            return null;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(username);
            Notification messageAdded = new MessageAdded(chatID, message);
            toServer.writeObject(messageAdded);
            toServer.flush();

            Message finalMessage = (Message) fromServer.readObject();
            return new MyMessage(finalMessage);
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static void chatWasSeen(String username, String chatID)
    {
        Socket socket = getSocket(MAIN_MENU);
        if(socket == null)
            return;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(username);
            Notification chatWasSeen = new ChatWasSeen(chatID + ";" + username);
            toServer.writeObject(chatWasSeen);
            toServer.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean deleteMessage(String username, String chatID, int id)
    {
        Socket socket = getSocket(MAIN_MENU);
        if(socket == null)
            return false;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(username);
            Notification deleteMessage = new MessageDeleted(chatID + ";" + username, id);
            toServer.writeObject(deleteMessage);
            toServer.flush();

            return fromServer.readBoolean();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean editMessage(String username, String chatID, int id, String newText)
    {
        Socket socket = getSocket(MAIN_MENU);
        if(socket == null)
            return false;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(username);

            Notification editMessage = new MessageEdited(chatID + ";" + username, id, newText);
            toServer.writeObject(editMessage);
            toServer.flush();

            return fromServer.readBoolean();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean clearHistory(String username, String chatID)
    {
        Socket socket = getSocket(MAIN_MENU);
        if(socket == null)
            return false;

        try (ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
        {
            toServer.writeUTF(username);
            Notification clearHistory = new ChatCleared(chatID + ";" + username);
            toServer.writeObject(clearHistory);
            toServer.flush();

            return fromServer.readBoolean();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static void connect()
    {
        Socket connection = getSocket(CONNECTION);
        if(connection == null)
            return;

        NotificationManager.start(connection);
    }

    public static void disconnect()
    {
        NotificationManager.stop();
    }
}
