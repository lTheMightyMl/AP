package classes.player.role;

import classes.player.Player;

import java.util.Scanner;

public class Striker extends Player {

    private static final int HEADING = 0;
    private static final int FINISHING = 1;
    private static final int PENALTIES = 2;

    public Striker(Striker striker) {
        super(striker);
    }

    public Striker(String firstName, String lastName, int age, Scanner scanner) {
        super(firstName, lastName, age);
        ABILITIES_NAME[HEADING] = "Heading";
        ABILITIES_NAME[FINISHING] = "Finishing";
        ABILITIES_NAME[PENALTIES] = "Penalties";
        for (int i = 0; i < NUMBER_OF_ABILITIES; i++) {
            System.out.println(ABILITIES_NAME[i] + ":");
            abilities[i] = Integer.parseInt(scanner.nextLine());
        }
    }

    public int getHeading() {
        return abilities[HEADING];
    }

    public int getFinishing() {
        return abilities[FINISHING];
    }

    public void setFinishing(int finishing) {
        abilities[FINISHING] = finishing;
    }

    public int getPenalties() {
        return abilities[PENALTIES];
    }
}
