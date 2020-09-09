package main.notifications;

public class ChatCleared implements Notification
{
    private final String chatID;

    public ChatCleared(String chatID)
    {
        this.chatID = chatID;
    }

    public String getChatID()
    {
        return chatID;
    }
}
