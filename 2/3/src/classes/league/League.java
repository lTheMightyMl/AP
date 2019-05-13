package classes.league;

import classes.player.Player;
import classes.player.role.Defender;
import classes.player.role.Goalkeeper;
import classes.player.role.Midfielder;
import classes.player.role.Striker;
import classes.team.Formation;
import classes.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class League {
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;
    private static final int AGE_THRESHOLD = 40;
    private static final String STRIKER = "ST";
    private static final String MIDFIELDER = "MF";
    private static final String DEFENDER = "DF";
    private static final String GOALKEEPER = "GK";
    private static final String INVALID_TEAM = "invalid team";
    private static final String INVALID_PLAYER = "invalid player";
    private static final String INVALID_SOURCE_TEAM = "invalid source team";
    private static final String INVALID_DESTINATION_TEAM = "invalid destination team";
    private static final int LOAN_FEE = 0;
    private ArrayList<Team> teams = new ArrayList<>();
    private ArrayList<Player> freeAgents = new ArrayList<>();
    private ArrayList<Player> agents = new ArrayList<>();

    public static void transfer(Team source, Team destination, Player player, int fee) {
        source.removePlayer(player);
        destination.addPlayer(player);
        source.receive(fee);
        destination.pay(fee);
    }

    private void fixture(Team home, Team away) {
        Formation homeFormation = home.getFormation().applyAwayHandicap();
        Formation awayFormation = away.getFormation();
        Match match = new Match(homeFormation, awayFormation);
        int homeGoals = match.getNumberOfTeam1Goals();
        int awayGoals = match.getNumberOfTeam2Goals();
        home.applyFixture(homeGoals, awayGoals);
        away.applyFixture(awayGoals, homeGoals);
    }

    private void endSeason() {
        addAge();
        returnLoans();
        freeAgents();
        retire();
        resetTeams();
    }

    private void resetTeams() {
        for (Team team : teams)
            team.reset();
    }

    private void rewardTeams(ArrayList<Team> teams) {
        if (teams.size() > 0)
            teams.get(FIRST).receive(100);
        if (teams.size() > 1)
            teams.get(SECOND).receive(50);
        if (teams.size() > 2)
            teams.get(THIRD).receive(20);
    }

    private void freeAgents() {
        for (Team team : teams)
            for (Player player : team.freeAgents()) {
                freeAgents.add(player);
                agents.remove(player);
                player.resetContract();
            }
    }

    private void returnLoans() {
        for (Team team : teams)
            team.returnLoans();
    }

    private void addAge() {
        for (Player player : freeAgents)
            player.addAge();
        for (Player player : agents)
            player.addAge();
    }

    private void retire() {
        for (Team team : teams)
            team.retire();
        ArrayList<Player> oldPlayers = new ArrayList<>();
        for (Player player : freeAgents)
            if (player.getAge() >= AGE_THRESHOLD)
                oldPlayers.add(player);
        for (Player player : agents)
            if (player.getAge() >= AGE_THRESHOLD)
                oldPlayers.add(player);
        for (Player player : oldPlayers) {
            freeAgents.remove(player);
            agents.remove(player);
        }
    }

    public void addTeam(String teamName) {
        if (getTeam(teamName) == null) {
            teams.add(new Team(teamName));
            System.out.println("team created");
        } else
            System.out.println("a team exists with this name");
    }

    private Team getTeam(String teamName) {
        for (Team team : teams)
            if (team.getName().equals(teamName))
                return team;
        return null;
    }

    public void deleteTeam(String teamName) {
        Team team = getTeam(teamName);
        if (team == null)
            System.out.println(INVALID_TEAM);
        else {
            ArrayList<Player> newFreeAgents = new ArrayList<>();
            for (Player player : agents)
                if (player.getOriginalTeam() == team) {
                    player.resetContract();
                    freeAgents.add(player);
                    newFreeAgents.add(player);
                }
            for (Player player : newFreeAgents)
                agents.remove(player);
        }
        teams.remove(team);
    }

    public void addPlayer(String firstName, String lastName, int age, String role, Scanner scanner) {
        if (getPlayer(firstName, lastName) != null) {
            System.out.println("a player exists with this name");
            return;
        } else if (role.equals(STRIKER))
            freeAgents.add(new Striker(firstName, lastName, age, scanner));
        else if (role.equals(MIDFIELDER))
            freeAgents.add(new Midfielder(firstName, lastName, age, scanner));
        else if (role.equals(DEFENDER))
            freeAgents.add(new Defender(firstName, lastName, age, scanner));
        else
            freeAgents.add(new Goalkeeper(firstName, lastName, age, scanner));
        System.out.println("player created");
    }

    private Player getPlayer(String firstName, String lastName) {
        Player freeAgent = getFreeAgent(firstName, lastName);
        if (freeAgent != null)
            return freeAgent;
        for (Player player : agents)
            if (player.equals(firstName, lastName))
                return player;
        return null;
    }

    private Player getFreeAgent(String firstName, String lastName) {
        for (Player player : freeAgents)
            if (player.equals(firstName, lastName))
                return player;
        return null;
    }

    public ArrayList<Player> getFreeAgents() {
        return new ArrayList<>(freeAgents);
    }

    public void moveFreeAgent(String firstName, String lastName, String teamName, int contractYears) {
        Team team = getTeam(teamName);
        if (team == null) {
            System.out.println(INVALID_TEAM);
            return;
        }
        Player player = getFreeAgent(firstName, lastName);
        if (player == null) {
            System.out.println("invalid free agent");
            return;
        }
        player.setContract(team, contractYears);
        team.addPlayer(player);
        freeAgents.remove(player);
        agents.add(player);
        System.out.println("free agent moved");
    }

    public void printTeamStats(String teamName) {
        Team team = getTeam(teamName);
        if (team == null) {
            System.out.println(INVALID_TEAM);
            return;
        }
        team.printStats();
    }

    public void printPlayerStats(String firstName, String lastName) {
        Player player = getPlayer(firstName, lastName);
        if (player == null) {
            System.out.println(INVALID_PLAYER);
            return;
        }
        System.out.print(player.toStringPlayerStats());
    }

    public void renewContract(String firstName, String lastName, int duration) {
        Player player = getPlayer(firstName, lastName);
        if (player == null) {
            System.out.println(INVALID_PLAYER);
            return;
        }
        Contract contract = player.getContract();
        if (contract == null) {
            System.out.println("invalid renewal command");
            return;
        }
        contract.renew(duration);
    }

    public void terminateContract(String firstName, String lastName) {
        Player player = getPlayer(firstName, lastName);
        if (player == null) {
            System.out.println(INVALID_PLAYER);
            return;
        }
        Contract contract = player.getContract();
        if (contract == null) {
            System.out.println("invalid termination command");
            return;
        }
        contract.getCurrentTeam().removePlayer(player);
        player.terminateContract();
        if (player.getContract() != null) {
            Team team = player.getCurrentTeam();
            if (team != null)
                team.addPlayer(player);
        } else {
            freeAgents.add(player);
            agents.remove(player);
        }
    }

    public void sell(String firstName, String lastName, String source, String destination, int price, int duration) {
        Team sourceTeam = getSourceTeam(source);
        if (sourceTeam == null) return;
        Player player = getPlayer(firstName, lastName);
        if (player == null || player.getCurrentTeam() != sourceTeam) {
            System.out.println(INVALID_PLAYER);
            return;
        }
        Team destinationTeam = getDestinationTeam(destination);
        if (destinationTeam == null) return;
        if (destinationTeam.getBudget() < price) {
            System.out.println("insufficient budget");
            return;
        }
        player.setContract(destinationTeam, duration);
        transfer(sourceTeam, destinationTeam, player, price);
    }

    private Team getDestinationTeam(String destination) {
        Team destinationTeam = getTeam(destination);
        if (destinationTeam == null) {
            System.out.println(INVALID_DESTINATION_TEAM);
            return null;
        }
        return destinationTeam;
    }

    private Team getSourceTeam(String source) {
        Team sourceTeam = getTeam(source);
        if (sourceTeam == null) {
            System.out.println(INVALID_SOURCE_TEAM);
            return null;
        }
        return sourceTeam;
    }

    public void loan(String firstName, String lastName, String source, String destination, int duration) {
        Team sourceTeam = getTeam(source);
        if (sourceTeam == null) {
            System.out.println(INVALID_SOURCE_TEAM);
            return;
        }
        Player player = getPlayer(firstName, lastName);
        if (player == null || player.getCurrentTeam() != sourceTeam) {
            System.out.println(INVALID_PLAYER);
            return;
        }
        Team destinationTeam = getDestinationTeam(destination);
        if (destinationTeam == null) return;
        player.setLoan(destinationTeam, duration);
        if (player.getContract().getRemainingDuration() < duration)
            return;
        transfer(sourceTeam, destinationTeam, player, LOAN_FEE);
    }

    public void putPlayer(String firstName, String lastName, String teamName) {
        Team team = getTeam(teamName);
        if (team == null) {
            System.out.println(INVALID_TEAM);
            return;
        }
        Player player = getPlayer(firstName, lastName);
        if (player == null || player.getCurrentTeam() != team) {
            System.out.println(INVALID_PLAYER);
            return;
        }
        team.putPlayer(player);
    }

    public void holdFriendly(String teamName1, String teamName2) {
        Team team1 = getTeam(teamName1);
        Team team2 = getTeam(teamName2);
        if (team1 == null || team2 == null) {
            System.out.println(INVALID_TEAM);
            return;
        }
        Formation team1Formation = team1.getFormation();
        Formation team2Formation = team2.getFormation();
        boolean formationNotComplete = isNotComplete(teamName1, team1Formation);
        formationNotComplete |= isNotComplete(teamName2, team2Formation);
        if (formationNotComplete)
            return;
        Match match = new Match(team1Formation, team2Formation);
        System.out.println(teamName1 + " " + match.getNumberOfTeam1Goals() + " - " + match.getNumberOfTeam2Goals() + " "
                + teamName2);
    }

    private boolean isNotComplete(String teamName, Formation teamFormation) {
        if (teamFormation.isNotComplete()) {
            doesNotHaveCompleteSquad(teamName);
            return true;
        }
        return false;
    }

    private void doesNotHaveCompleteSquad(String teamName1) {
        System.out.println(teamName1 + " squad isn't complete");
    }

    public void forwardSeason() {
        ArrayList<Team> incompleteTeams = new ArrayList<>();
        ArrayList<Team> completeTeams = new ArrayList<>();
        for (Team team : teams)
            if (team.getFormation().isNotComplete())
                incompleteTeams.add(team);
            else
                completeTeams.add(team);
        Collections.sort(incompleteTeams, Team.nameComparator());
        for (Team team : incompleteTeams)
            doesNotHaveCompleteSquad(team.getName());
        if (!incompleteTeams.isEmpty())
            return;
        for (Team home : completeTeams)
            for (Team away : completeTeams)
                if (home != away)
                    fixture(home, away);
        Collections.sort(completeTeams, Team.leagueComparator());
        for (int i = 0; i < completeTeams.size(); i++)
            System.out.println((i + 1) + ". " + completeTeams.get(i));
        rewardTeams(completeTeams);
        endSeason();
    }
}
