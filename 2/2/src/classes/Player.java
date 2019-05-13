package classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Player {
    private static ArrayList<Player> players = new ArrayList<>();
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private String name;

    private Player(String name) {
        this.name = name;
        players.add(this);
    }

    static Comparator<Player> playerComparator() {
        return (o1, o2) -> {
            if (o1.wins < o2.wins)
                return 1;
            if (o1.wins > o2.wins)
                return -1;
            if (o1.losses > o2.losses)
                return 1;
            if (o1.losses < o2.losses)
                return -1;
            if (o1.draws > o2.draws)
                return 1;
            if (o1.draws < o2.draws)
                return -1;
            return o1.name.compareTo(o2.name);
        };
    }

    public static Player addPlayer(String name) {
        Player currentPlayer = getPlayer(name);
        if (currentPlayer == null)
            currentPlayer = new Player(name);
        return currentPlayer;
    }

    private static Player getPlayer(String name) {
        for (Player player : players)
            if (player.name.equals(name))
                return player;
        return null;
    }

    public static void showScoreboard() {
        ArrayList<Player> scoreboard = new ArrayList<>(players);
        Collections.sort(scoreboard, playerComparator());
        for (Player player : scoreboard)
            System.out.println(player);
    }

    public void addWin() {
        wins++;
    }

    public void addDraw() {
        draws++;
    }

    public void addLoss() {
        losses++;
    }

    @Override
    public String toString() {
        return name + " " + wins + " " + losses + " " + draws;
    }

    public String getName() {
        return name;
    }
}