package classes.player.role;

import classes.player.Player;

import java.util.Scanner;

public class Midfielder extends Player {

    private static final int PASSING = 0;
    private static final int SHOOTING = 1;
    private static final int CROSSING = 2;

    public Midfielder(Midfielder midfielder) {
        super(midfielder);
    }

    public Midfielder(String firstName, String lastName, int age, Scanner scanner) {
        super(firstName, lastName, age);
        ABILITIES_NAME[PASSING] = "Passing";
        ABILITIES_NAME[SHOOTING] = "Shooting";
        ABILITIES_NAME[CROSSING] = "Crossing";
        for (int i = 0; i < NUMBER_OF_ABILITIES; i++) {
            System.out.println(ABILITIES_NAME[i] + ":");
            abilities[i] = Integer.parseInt(scanner.nextLine());
        }
    }

    public int getPassing() {
        return abilities[PASSING];
    }

    public int getShooting() {
        return abilities[SHOOTING];
    }

    public int getCrossing() {
        return abilities[CROSSING];
    }
}
