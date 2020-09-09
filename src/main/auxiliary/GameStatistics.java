package main.auxiliary;

import java.io.Serializable;

public class GameStatistics implements Serializable
{
    private final int onePlayerGamesWon;
    private final int onePlayerGamesDrawn;
    private final int onePlayerGamesLost;
    private final int twoPlayerGamesWon;
    private final int twoPlayerGamesDrawn;
    private final int twoPlayerGamesLost;

    public GameStatistics(int onePlayerGamesWon, int onePlayerGamesDrawn, int onePlayerGamesLost, int twoPlayerGamesWon, int twoPlayerGamesDrawn, int twoPlayerGamesLost)
    {
        this.onePlayerGamesWon = onePlayerGamesWon;
        this.onePlayerGamesDrawn = onePlayerGamesDrawn;
        this.onePlayerGamesLost = onePlayerGamesLost;
        this.twoPlayerGamesWon = twoPlayerGamesWon;
        this.twoPlayerGamesDrawn = twoPlayerGamesDrawn;
        this.twoPlayerGamesLost = twoPlayerGamesLost;
    }

    private int getGames()
    {
        return getOnlineGames() + getOfflineGames();
    }

    public int getOnlineGames()
    {
        return twoPlayerGamesWon + twoPlayerGamesDrawn + twoPlayerGamesLost;
    }
    public int getOfflineGames()
    {
        return onePlayerGamesWon + onePlayerGamesDrawn + onePlayerGamesLost;
    }

    public int getGamesWon()
    {
        return onePlayerGamesWon + twoPlayerGamesWon;
    }
    public int getGamesDrawn()
    {
        return onePlayerGamesDrawn + onePlayerGamesDrawn;
    }
    public int getGamesLost()
    {
        return onePlayerGamesLost + twoPlayerGamesLost;
    }

    public double getOnlineGamesPercentage()
    {
        if (getGames() == 0)
            return 0.5;
        return ((double) getOnlineGames()) / getGames();
    }
    public double getOfflineGamesPercentage()
    {
        if (getGames() == 0)
            return 0.5;
        return ((double) getOfflineGames()) / getGames();
    }

    public double getGamesWonPercentage()
    {
        if(getGames() == 0)
            return 0.33;
        return ((double) getGamesWon()) / getGames();
    }
    public double getGamesDrawnPercentage()
    {
        if(getGames() == 0)
            return 0.33;
        return ((double) getGamesDrawn()) / getGames();
    }
    public double getGamesLostPercentage()
    {
        if(getGames() == 0)
            return 0.33;
        return ((double) getGamesLost()) / getGames();
    }
}
