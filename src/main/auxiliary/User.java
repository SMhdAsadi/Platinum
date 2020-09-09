package main.auxiliary;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements Serializable
{
    private final String username;
    private String password;
    private String name;
    private String bio;
    private String phone;
    private String question;
    private String answer;
    private int status;
    private String lastSeen;
    private int avatar;
    private int score;

    public User(String username, String password, String name, String bio, String phone, String question, String answer, int avatar)
    {
        this.username = username;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.phone = phone;
        this.question = question;
        this.answer = answer;
        this.avatar = avatar;
    }

    public User(String username, String password, String name, String bio, String phone, String question,
                String answer, int status, long lastSeen, int avatar, int score)
    {
        this.username = username;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.phone = phone;
        this.question = question;
        this.answer = answer;
        this.status = status;
        this.lastSeen = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(lastSeen));
        this.avatar = avatar;
        this.score = score;
    }

    public User(String username, String name, int status, long lastSeen, int avatar)
    {
        this.username = username;
        this.name = name;
        this.status = status;
        this.lastSeen = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(lastSeen));
        this.avatar = avatar;
    }

    public String getUsername()
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }
    public String getName()
    {
        return name;
    }
    public String getBio()
    {
        return bio;
    }
    public String getPhone()
    {
        return phone;
    }
    public String getQuestion()
    {
        return question;
    }
    public String getAnswer()
    {
        return answer;
    }
    public int getStatus()
    {
        return status;
    }
    public String getLastSeen()
    {
        return lastSeen;
    }
    public int getAvatar()
    {
        return avatar;
    }
    public int getScore()
    {
        return score;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setBio(String bio)
    {
        this.bio = bio;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }
    public void setLastSeen(String lastSeen)
    {
        this.lastSeen = lastSeen;
    }
    public void setAvatar(int avatar)
    {
        this.avatar = avatar;
    }
    public void setScore(int score)
    {
        this.score = score;
    }
}
