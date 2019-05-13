package classes.player;

import classes.league.Contract;
import classes.player.role.Defender;
import classes.player.role.Goalkeeper;
import classes.player.role.Midfielder;
import classes.player.role.Striker;
import classes.team.Team;

import java.util.Comparator;

public abstract class Player {
    protected static final int NUMBER_OF_ABILITIES = 3;
    protected String[] ABILITIES_NAME = new String[NUMBER_OF_ABILITIES];
    protected int[] abilities = new int[NUMBER_OF_ABILITIES];
    private String firstName;
    private String lastName;
    private int age;
    private Contract contract = null;

    public Player(Player player) {
        firstName = player.firstName;
        lastName = player.lastName;
        age = player.age;
        contract = player.contract;
        for (int i = 0; i < NUMBER_OF_ABILITIES; i++)
            abilities[i] = player.abilities[i];
    }

    public Player(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public static Comparator<Player> comparator() {
        return (o1, o2) -> {
            if (o1 instanceof Goalkeeper)
                if (!(o2 instanceof Goalkeeper))
                    return -1;
                else
                    return nameComparator(o1, o2);
            if (o2 instanceof Goalkeeper)
                return 1;
            if (o1 instanceof Defender)
                if (!(o2 instanceof Defender))
                    return -1;
                else
                    return nameComparator(o1, o2);
            if (o2 instanceof Defender)
                return 1;
            if (o1 instanceof Midfielder)
                if (!(o2 instanceof Midfielder))
                    return -1;
                else
                    return nameComparator(o1, o2);
            if (o2 instanceof Midfielder)
                return 1;
            return nameComparator(o1, o2);
        };
    }

    private static int nameComparator(Player o1, Player o2) {
        if (o1.lastName.equals(o2.lastName))
            return o1.firstName.compareTo(o2.firstName);
        return o1.lastName.compareTo(o2.lastName);
    }

    public void applyAwayHandicap() {
        for (int i = 0; i < NUMBER_OF_ABILITIES; i++)
            abilities[i] -= 5;
    }

    public Contract getContract() {
        return contract;
    }

    public Team getCurrentTeam() {
        if (contract == null)
            return null;
        return contract.getCurrentTeam();
    }

    public void addAge() {
        age++;
        if (contract != null)
            contract.passYear();
    }

    public int getAge() {
        return age;
    }

    public void resetLoan() {
        if (contract != null)
            contract.resetLoan();
    }

    public Team getOriginalTeam() {
        if (contract == null)
            return null;
        return contract.getOriginalTeam();
    }

    public void resetContract() {
        contract = null;
    }

    public boolean equals(String firstName, String lastName) {
        if (this.firstName.equals(firstName) && this.lastName.equals(lastName))
            return true;
        return false;
    }

    public String toStringFreeAgent() {
        String toStringFreeAgent = firstName + " " + lastName + " ";
        if (this instanceof Striker)
            return toStringFreeAgent + "ST";
        else if (this instanceof Midfielder)
            return toStringFreeAgent + "MF";
        else if (this instanceof Defender)
            return toStringFreeAgent + "DF";
        else
            return toStringFreeAgent + "GK";
    }

    public void setContract(Team team, int contractYears) {
        contract = new Contract(team, contractYears);
    }

    public String toStringTeamStats() {
        if (contract != null)
            return toStringFreeAgent() + " Age: " + age + " Contract: " + contract.getRemainingDuration() + " years";
        return "";
    }

    public String toStringPlayerStats() {
        String toStringPlayerStats = toStringFreeAgent() + "\n" + "Age: " + age + "\n";
        for (int i = 0; i < NUMBER_OF_ABILITIES; i++)
            toStringPlayerStats += ABILITIES_NAME[i] + ": " + abilities[i] + "\n";
        return toStringPlayerStats;
    }

    public void terminateContract() {
        if (contract != null)
            contract = contract.terminate();
    }

    public void setLoan(Team team, int duration) {
        if (duration > contract.getRemainingDuration()) {
            System.out.println("invalid loan contract");
            return;
        }
        contract.setLoan(team, duration);
    }
}
