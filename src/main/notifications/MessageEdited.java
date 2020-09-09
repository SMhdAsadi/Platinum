package main.notifications;

public class MessageEdited implements Notification
{
    private final String chatID;
    private final int messageID;
    private final String newMessageText;

    public MessageEdited(String chatID, int messageID, String newMessageText)
    {
        this.chatID = chatID;
        this.messageID = messageID;
        this.newMessageText = newMessageText;
    }

    public String getChatID()
    {
        return chatID;
    }

    public int getMessageID()
    {
        return messageID;
    }

    public String getNewMessageText()
    {
        return newMessageText;
    }
}
