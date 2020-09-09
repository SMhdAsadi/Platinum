package main.notifications;

import main.auxiliary.Message;

public class MessageAdded implements Notification
{
    private final String chatID;
    private final Message message;

    public MessageAdded(String chatID, Message message)
    {
        this.chatID = chatID;
        this.message = message;
    }

    public String getChatID()
    {
        return chatID;
    }

    public Message getMessage()
    {
        return message;
    }
}
