package main.notifications;

public class MessageDeleted implements Notification
{
    private final String chatID;
    private final int messageID;

    public MessageDeleted(String chatID, int messageID)
    {
        this.chatID = chatID;
        this.messageID = messageID;
    }

    public String getChatID()
    {
        return chatID;
    }

    public int getMessageID()
    {
        return messageID;
    }
}
