package main.notifications;

public class ChatWasSeen implements Notification
{
    private final String chatID;

    public ChatWasSeen(String chatID)
    {
        this.chatID = chatID;
    }

    public String getChatID()
    {
        return chatID;
    }
}
