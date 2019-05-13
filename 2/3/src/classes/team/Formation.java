package classes.team;

import classes.player.Player;
import classes.player.role.Defender;
import classes.player.role.Goalkeeper;
import classes.player.role.Midfielder;
import classes.player.role.Striker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class Formation {
    public static final int NUMBER_OF_MAIN_GOALKEEPERS = 1;
    public static final int NUMBER_OF_MAIN_DEFENDERS = 4;
    public static final int NUMBER_OF_MAIN_MIDFIELDERS = 3;
    public static final int NUMBER_OF_MAIN_STRIKERS = 3;
    private static final int STRIKER_BOOST = 5;
    private static final int STRIKER_WEAKENING = 3;
    private static final int GOOD_PASSING_AVERAGE = 85;
    private static final int GOOD_PASSING = 90;
    private static final int GOOD_STRENGTH_AVERAGE = 85;
    private static final int GOOD_STRENGTH = 90;
    private Queue<Goalkeeper> goalkeepers = new LinkedList<>();
    private Queue<Defender> defenders = new LinkedList<>();
    private Queue<Midfielder> midfielders = new LinkedList<>();
    private Queue<Striker> strikers = new LinkedList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private Team team;

    Formation(Team team) {
        this.team = team;
    }

    Formation(Formation formation) {
        for (Goalkeeper goalkeeper : formation.goalkeepers) {
            Goalkeeper newGoalkeeper = new Goalkeeper(goalkeeper);
            goalkeepers.add(newGoalkeeper);
            players.add(newGoalkeeper);
        }
        for (Defender defender : formation.defenders) {
            Defender newDefender = new Defender(defender);
            defenders.add(newDefender);
            players.add(newDefender);
        }
        for (Midfielder midfielder : formation.midfielders) {
            Midfielder newMidfielder = new Midfielder(midfielder);
            midfielders.add(newMidfielder);
            players.add(newMidfielder);
        }
        for (Striker striker : formation.strikers) {
            Striker newStriker = new Striker(striker);
            strikers.add(newStriker);
            players.add(newStriker);
        }
        team = formation.team;
    }

    static ArrayList<Player> listAllPlayers(Queue<Goalkeeper> goalkeepers, Queue<Defender> defenders, Queue<Midfielder>
            midfielders, Queue<Striker> strikers) {
        ArrayList<Player> players = new ArrayList<>();
        players.addAll(goalkeepers);
        players.addAll(defenders);
        players.addAll(midfielders);
        players.addAll(strikers);
        Collections.sort(players, Player.comparator());
        return players;
    }

    public Formation applyAwayHandicap() {
        for (Player player : players)
            player.applyAwayHandicap();
        return this;
    }

    public void boostStrikers() {
        if (getMidfieldersPassingAverage() > GOOD_PASSING_AVERAGE)
            applyStrikerBoost();
        if (existsAGoodMidfielder())
            applyStrikerBoost();
    }

    private double getMidfieldersPassingAverage() {
        if (midfielders.size() == 0)
            return 0;
        double midfieldersPassingSum = 0;
        for (Midfielder midfielder : midfielders)
            midfieldersPassingSum += midfielder.getPassing();
        return midfieldersPassingSum / midfielders.size();
    }

    private boolean existsAGoodMidfielder() {
        for (Midfielder midfielder : midfielders)
            if (midfielder.getPassing() > GOOD_PASSING)
                return true;
        return false;
    }

    private void applyStrikerBoost() {
        for (Striker striker : strikers)
            striker.setFinishing(striker.getFinishing() + STRIKER_BOOST);
    }

    public void weakenStrikers(Formation opponentFormation) {
        if (opponentFormation.getDefendersStrengthAverage() > GOOD_STRENGTH_AVERAGE)
            applyStrikerWeakening();
        if (opponentFormation.existsAGoodDefender())
            applyStrikerWeakening();
    }

    private double getDefendersStrengthAverage() {
        if (defenders.size() == 0)
            return 0;
        double defendersStrengthSum = 0;
        for (Defender defender : defenders)
            defendersStrengthSum += defender.getStrength();
        return defendersStrengthSum / defenders.size();
    }

    private boolean existsAGoodDefender() {
        for (Defender defender : defenders)
            if (defender.getStrength() > GOOD_STRENGTH)
                return true;
        return false;
    }

    private void applyStrikerWeakening() {
        for (Striker striker : strikers)
            striker.setFinishing(striker.getFinishing() - STRIKER_WEAKENING);
    }

    public ArrayList<Striker> getStrikers() {
        return new ArrayList<>(strikers);
    }

    public ArrayList<Midfielder> getMidfielders() {
        return new ArrayList<>(midfielders);
    }

    public ArrayList<Defender> getDefenders() {
        return new ArrayList<>(defenders);
    }

    public ArrayList<Goalkeeper> getGoalkeepers() {
        return new ArrayList<>(goalkeepers);
    }

    public double getStrikersFinishingAverage() {
        if (strikers.size() == 0)
            return 0;
        double strikersFinishingSum = 0;
        for (Striker striker : strikers)
            strikersFinishingSum += striker.getFinishing();
        return strikersFinishingSum / strikers.size();
    }

    public double getMidfieldersCrossingAverage() {
        if (midfielders.size() == 0)
            return 0;
        double midfieldersCrossingSum = 0;
        for (Midfielder midfielder : midfielders)
            midfieldersCrossingSum += midfielder.getCrossing();
        return midfieldersCrossingSum / midfielders.size();
    }

    public double getDefendersAggressionAverage() {
        if (defenders.size() == 0)
            return 0;
        double defendersAggressionSum = 0;
        for (Defender defender : defenders)
            defendersAggressionSum += defender.getAggression();
        return defendersAggressionSum / defenders.size();
    }

    public double getStrikersMaxPenalties() {
        if (strikers.size() == 0)
            return 0;
        int strikersMaxPenalties = Integer.MIN_VALUE;
        for (Striker striker : strikers)
            strikersMaxPenalties = Integer.max(strikersMaxPenalties, striker.getPenalties());
        return strikersMaxPenalties;
    }

    public void remove(Player player) {
        if (player instanceof Striker)
            strikers.remove(player);
        else if (player instanceof Midfielder)
            midfielders.remove(player);
        else if (player instanceof Defender)
            defenders.remove(player);
        else
            goalkeepers.remove(player);
        players.remove(player);
    }

    void print() {
        ArrayList<Player> players = listAllPlayers(goalkeepers, defenders, midfielders, strikers);
        for (int i = 0; i < players.size(); i++)
            System.out.println((i + 1) + ". " + players.get(i).toStringTeamStats());
    }

    boolean hasPlayer(Player player) {
        if (players.contains(player))
            return true;
        return false;
    }

    void addPlayer(Player player) {
        if (player instanceof Striker) {
            strikers.add((Striker) player);
            Queue<Striker> reservedStrikers = team.getReservedStrikers();
            if (strikers.size() > NUMBER_OF_MAIN_STRIKERS) {
                Striker extraStriker = strikers.remove();
                reservedStrikers.add(extraStriker);
                players.remove(extraStriker);
            }
        } else if (player instanceof Midfielder) {
            midfielders.add((Midfielder) player);
            Queue<Midfielder> reservedMidfielders = team.getReservedMidfielders();
            if (midfielders.size() > NUMBER_OF_MAIN_MIDFIELDERS) {
                Midfielder extraMidfielder = midfielders.remove();
                reservedMidfielders.add(extraMidfielder);
                players.remove(extraMidfielder);
            }
        } else if (player instanceof Defender) {
            defenders.add((Defender) player);
            Queue<Defender> reservedDefenders = team.getReservedDefenders();
            if (defenders.size() > NUMBER_OF_MAIN_DEFENDERS) {
                Defender extraDefender = defenders.remove();
                reservedDefenders.add(extraDefender);
                players.remove(extraDefender);
            }
        } else {
            goalkeepers.add((Goalkeeper) player);
            Queue<Goalkeeper> reservedGoalkeepers = team.getReservedGoalkeepers();
            if (goalkeepers.size() > NUMBER_OF_MAIN_GOALKEEPERS) {
                Goalkeeper extraGoalkeeper = goalkeepers.remove();
                reservedGoalkeepers.add(extraGoalkeeper);
                players.remove(extraGoalkeeper);
            }
        }
        players.add(player);
    }

    public boolean isNotComplete() {
        if (strikers.size() != NUMBER_OF_MAIN_STRIKERS)
            return true;
        if (midfielders.size() != NUMBER_OF_MAIN_MIDFIELDERS)
            return true;
        if (defenders.size() != NUMBER_OF_MAIN_DEFENDERS)
            return true;
        if (goalkeepers.size() != NUMBER_OF_MAIN_GOALKEEPERS)
            return true;
        return false;
    }
}
