package classes.team;

import classes.league.Contract;
import classes.league.League;
import classes.player.Player;
import classes.player.role.Defender;
import classes.player.role.Goalkeeper;
import classes.player.role.Midfielder;
import classes.player.role.Striker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

public class Team {
    private static final int LOAN_FEE = 0;
    private static final int AGE_THRESHOLD = 40;
    private String name;
    private int budget = 100;
    private Formation formation = new Formation(this);
    private Queue<Goalkeeper> reservedGoalkeepers = new LinkedList<>();
    private Queue<Defender> reservedDefenders = new LinkedList<>();
    private Queue<Midfielder> reservedMidfielders = new LinkedList<>();
    private Queue<Striker> reservedStrikers = new LinkedList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private int points = 0;
    private int goalsFor = 0;
    private int goalsAgainst = 0;
    private int numberOfWins = 0;
    private int numberOfLosses = 0;
    private int numberOfDraws = 0;

    public Team(String name) {
        this.name = name;
    }

    public static Comparator<Team> leagueComparator() {
        return (o1, o2) -> {
            if (o1.points > o2.points)
                return -1;
            if (o1.points < o2.points)
                return 1;
            if (o1.getGoalDifference() > o2.getGoalDifference())
                return -1;
            if (o1.getGoalDifference() < o2.getGoalDifference())
                return 1;
            if (o1.goalsFor > o2.goalsFor)
                return -1;
            if (o1.goalsFor < o2.goalsFor)
                return 1;
            return o1.name.compareTo(o2.name);
        };
    }

    public static Comparator<Team> nameComparator() {
        return Comparator.comparing(o -> o.name);
    }

    private int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public ArrayList<Player> freeAgents() {
        ArrayList<Player> freeAgents = new ArrayList<>();
        for (Player player : players) {
            if (player.getOriginalTeam() == this && (player.getContract() == null ||
                    player.getContract().getRemainingDuration() == 0))
                freeAgents.add(player);
        }
        for (Player player : freeAgents)
            removePlayer(player);
        return freeAgents;
    }

    public void removePlayer(Player player) {
        formation.remove(player);
        removeReserved(player);
        players.remove(player);
    }

    private void removeReserved(Player player) {
        reservedGoalkeepers.remove(player);
        reservedDefenders.remove(player);
        reservedMidfielders.remove(player);
        reservedStrikers.remove(player);
    }

    public void pay(int sum) {
        budget -= sum;
    }

    public void receive(int sum) {
        budget += sum;
    }

    public Formation getFormation() {
        return new Formation(formation);
    }

    public void applyFixture(int goalsFor, int goalsAgainst) {
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
        if (goalsFor > goalsAgainst) {
            numberOfWins++;
            points += 3;
        } else if (goalsAgainst > goalsFor)
            numberOfLosses++;
        else {
            numberOfDraws++;
            points += 1;
        }
    }

    Queue<Striker> getReservedStrikers() {
        return reservedStrikers;
    }

    Queue<Midfielder> getReservedMidfielders() {
        return reservedMidfielders;
    }

    Queue<Defender> getReservedDefenders() {
        return reservedDefenders;
    }

    Queue<Goalkeeper> getReservedGoalkeepers() {
        return reservedGoalkeepers;
    }

    public void returnLoans() {
        ArrayList<Player> endLoans = new ArrayList<>();
        for (Player player : players) {
            Contract contract = player.getContract();
            if (contract == null)
                continue;
            if (contract.isLoan() && contract.getRemainingDuration() == 0)
                endLoans.add(player);
        }
        for (Player player : endLoans) {
            League.transfer(this, player.getContract().getOriginalTeam(), player, LOAN_FEE);
            player.resetLoan();
            removePlayer(player);
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
        if (player instanceof Striker)
            reservedStrikers.add((Striker) player);
        else if (player instanceof Midfielder)
            reservedMidfielders.add((Midfielder) player);
        else if (player instanceof Defender)
            reservedDefenders.add((Defender) player);
        else
            reservedGoalkeepers.add((Goalkeeper) player);
    }

    public void retire() {
        ArrayList<Player> oldPlayers = new ArrayList<>();
        for (Player player : players)
            if (player.getAge() >= AGE_THRESHOLD)
                oldPlayers.add(player);
        for (Player player : oldPlayers)
            removePlayer(player);
    }

    public String getName() {
        return name;
    }

    public void printStats() {
        System.out.println("Team: " + name);
        System.out.println("Budget: " + budget);
        System.out.println("Squad players:");
        formation.print();
        System.out.println("Reserve players:");
        ArrayList<Player> reservedPlayers = Formation.listAllPlayers(reservedGoalkeepers, reservedDefenders,
                reservedMidfielders, reservedStrikers);
        for (int i = 0; i < reservedPlayers.size(); i++)
            System.out.println((i + 1) + ". " + reservedPlayers.get(i).toStringTeamStats());
    }

    public int getBudget() {
        return budget;
    }

    public void putPlayer(Player player) {
        if (formation.hasPlayer(player)) {
            System.out.println("player is currently in the main squad");
            return;
        }
        removeReserved(player);
        formation.addPlayer(player);
    }

    public String toString() {
        return name + " " + points + " points W: " + numberOfWins + " D: " + numberOfDraws + " L: " + numberOfLosses +
                " GF: " + goalsFor + " GA: " + goalsAgainst + " GD: " + sign(getGoalDifference());
    }

    private String sign(int number) {
        if (number > 0)
            return "+" + number;
        return String.valueOf(number);
    }

    public void reset() {
        points = 0;
        goalsFor = 0;
        goalsAgainst = 0;
        numberOfWins = 0;
        numberOfDraws = 0;
        numberOfLosses = 0;
    }
}
