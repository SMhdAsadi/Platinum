package main.auxiliary;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyUser implements Serializable
{
    private final String username;
    private final StringProperty password = new SimpleStringProperty(this, "password", "");
    private final StringProperty name = new SimpleStringProperty(this, "name", "");
    private final StringProperty bio = new SimpleStringProperty(this, "bio", "");
    private final IntegerProperty status = new SimpleIntegerProperty(this, "status", 0);
    private final StringProperty lastSeen = new SimpleStringProperty(this, "status", "");
    private final IntegerProperty avatar = new SimpleIntegerProperty(this, "avatar", 0);
    private final IntegerProperty score = new SimpleIntegerProperty(this, "score", 0);
    private final String phone;
    private final String question;
    private final String answer;

    public MyUser(User user)
    {
        this.username = user.getUsername();
        this.password.set(user.getPassword());
        this.name.set(user.getName());
        this.bio.set(user.getBio());
        this.phone = user.getPhone();
        this.question = user.getQuestion();
        this.answer = user.getAnswer();
        this.status.set(user.getStatus());
        this.lastSeen.set(user.getLastSeen());
        this.avatar.set(user.getAvatar());
        this.score.set(user.getScore());
    }

    public MyUser(String username, String password, String name, String bio, String phone, String question,
                  String answer, int status, long lastSeen, int avatar, int score)
    {
        this.username = username;
        this.password.set(password);
        this.name.set(name);
        this.bio.set(bio);
        this.phone = phone;
        this.question = question;
        this.answer = answer;
        this.status.set(status);
        this.lastSeen.set(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(lastSeen)));
        this.avatar.set(avatar);
        this.score.set(score);
    }

    public User getUser()
    {
        try
        {
            return new User(getUsername(), getPassword(), getName(), getBio(), getPhone(), getQuestion(), getAnswer(),
                    getStatus(), new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").parse(getLastSeen()).getTime(),
                    getAvatar(), getScore());
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String getUsername()
    {
        return username;
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

    public final String getPassword()
    {
        return password.get();
    }
    public final String getName()
    {
        return name.get();
    }
    public final String getBio()
    {
        return bio.get();
    }
    public final int getStatus()
    {
        return status.get();
    }
    public final String getLastSeen()
    {
        return lastSeen.get();
    }
    public final int getAvatar()
    {
        return avatar.get();
    }
    public final int getScore()
    {
        return score.get();
    }

    public final StringProperty passwordProperty()
    {
        return password;
    }
    public final StringProperty nameProperty()
    {
        return name;
    }
    public final StringProperty bioProperty()
    {
        return bio;
    }
    public final IntegerProperty statusProperty()
    {
        return status;
    }
    public final StringProperty lastSeenProperty()
    {
        return lastSeen;
    }
    public final IntegerProperty avatarProperty()
    {
        return avatar;
    }
    public final IntegerProperty scoreProperty()
    {
        return score;
    }

    public final void setPassword(String password)
    {
        this.password.set(password);
    }
    public final void setName(String name)
    {
        this.name.set(name);
    }
    public final void setBio(String bio)
    {
        this.bio.set(bio);
    }
    public final void setStatus(int status)
    {
        this.status.set(status);
    }
    public final void setLastSeen(String lastSeen)
    {
        this.lastSeen.set(lastSeen);
    }
    public final void setAvatar(int avatar)
    {
        this.avatar.set(avatar);
    }
    public final void setScore(int score)
    {
        this.score.set(score);
    }

}
