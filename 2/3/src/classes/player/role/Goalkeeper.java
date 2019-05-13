package classes.player.role;

import classes.player.Player;

import java.util.Scanner;

public class Goalkeeper extends Player {

    private static final int SHOT_SAVING = 0;
    private static final int REACTIONS = 1;
    private static final int PENALTY_SAVING = 2;

    public Goalkeeper(Goalkeeper goalkeeper) {
        super(goalkeeper);
    }

    public Goalkeeper(String firstName, String lastName, int age, Scanner scanner) {
        super(firstName, lastName, age);
        ABILITIES_NAME[SHOT_SAVING] = "Shot Saving";
        ABILITIES_NAME[REACTIONS] = "Reactions";
        ABILITIES_NAME[PENALTY_SAVING] = "Penalty Saving";
        for (int i = 0; i < NUMBER_OF_ABILITIES; i++) {
            System.out.println(ABILITIES_NAME[i] + ":");
            abilities[i] = Integer.parseInt(scanner.nextLine());
        }
    }

    public int getShotSaving() {
        return abilities[SHOT_SAVING];
    }

    public void setShotSaving(int shotSaving) {
        abilities[SHOT_SAVING] = shotSaving;
    }

    public int getReactions() {
        return abilities[REACTIONS];
    }

    public void setReactions(int reactions) {
        abilities[REACTIONS] = reactions;
    }

    public int getPenaltySaving() {
        return abilities[PENALTY_SAVING];
    }

    public void setPenaltySaving(int penaltySaving) {
        abilities[PENALTY_SAVING] = penaltySaving;
    }
}
