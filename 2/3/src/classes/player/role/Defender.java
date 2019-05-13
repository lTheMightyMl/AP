package classes.player.role;

import classes.player.Player;

import java.util.Scanner;

public class Defender extends Player {

    private static final int STRENGTH = 0;
    private static final int AGGRESSION = 1;
    private static final int HEADING = 2;

    public Defender(Defender defender) {
        super(defender);
    }

    public Defender(String firstName, String lastName, int age, Scanner scanner) {
        super(firstName, lastName, age);
        ABILITIES_NAME[STRENGTH] = "Strength";
        ABILITIES_NAME[AGGRESSION] = "Aggression";
        ABILITIES_NAME[HEADING] = "Heading";
        for (int i = 0; i < NUMBER_OF_ABILITIES; i++) {
            System.out.println(ABILITIES_NAME[i] + ":");
            abilities[i] = Integer.parseInt(scanner.nextLine());
        }
    }

    public int getStrength() {
        return abilities[STRENGTH];
    }

    public int getAggression() {
        return abilities[AGGRESSION];
    }

    public int getHeading() {
        return abilities[HEADING];
    }
}
