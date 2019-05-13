import classes.league.League;
import classes.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Interactor {
    private static final String END = "end";
    private static final String CREATE = "create";
    private static final String TEAM = "team";
    private static final String NAME = "\\w+";
    private static final String TEAM_NAME = NAME;
    private static final String TEAM_WITH_NAME = TEAM + " " + TEAM_NAME;
    private static final String CREATE_TEAM = CREATE + " " + TEAM_WITH_NAME;
    private static final String DELETE = "delete";
    private static final String DELETE_TEAM = DELETE + " " + TEAM_WITH_NAME;
    private static final String PLAYER = "player";
    private static final String PLAYER_NAME = NAME + " " + NAME;
    private static final String NUMBER = "\\d+";
    private static final String AGE = NUMBER;
    private static final String POSITION = "((GK)|(DF)|(MF)|(ST))";
    private static final String PLAYER_WITH_NAME = PLAYER + " " + PLAYER_NAME;
    private static final String CREATE_PLAYER = CREATE + " " + PLAYER_WITH_NAME + " " + AGE + " " + POSITION;
    private static final String PRINT = "print";
    private static final String FREE_AGENT = "free agent";
    private static final String FREE_AGENTS = FREE_AGENT + "s";
    private static final String PRINT_FREE_AGENTS = PRINT + " " + FREE_AGENTS;
    private static final String MOVE = "move";
    private static final String TO = "to";
    private static final String WITH = "with";
    private static final String YEARS = "years";
    private static final String NUMBER_OF_YEARS = NUMBER + " " + YEARS;
    private static final String CONTRACT = "contract";
    private static final String CONTRACT_NUMBER_OF_YEARS = WITH + " " + NUMBER_OF_YEARS + " " + CONTRACT;
    private static final String TO_TEAM = TO + " " + TEAM_NAME;
    private static final String MOVE_FREE_AGENT = MOVE + " " + FREE_AGENT + " " + PLAYER_NAME + " " + TO_TEAM + " " +
            CONTRACT_NUMBER_OF_YEARS;
    private static final String STATS = "stats";
    private static final String OF = "of";
    private static final String STATS_OF = STATS + " " + OF;
    private static final String PRINT_STATS = PRINT + " " + STATS_OF;
    private static final String PRINT_TEAM_STATS = PRINT_STATS + " " + TEAM_WITH_NAME;
    private static final String PRINT_PLAYER_STATS = PRINT_STATS + " " + PLAYER_WITH_NAME;
    private static final String RENEW = "renew";
    private static final String FOR = "for";
    private static final String OF_PLAYER = OF + " " + PLAYER_NAME;
    private static final String RENEW_CONTRACT = RENEW + " " + CONTRACT + " " + OF_PLAYER + " " + FOR + " "
            + NUMBER_OF_YEARS;
    private static final String TERMINATE = "terminate";
    private static final String TERMINATE_CONTRACT = TERMINATE + " " + CONTRACT + " " + OF_PLAYER;
    private static final String SELL = "sell";
    private static final String FROM = "from";
    private static final String PRICE = NUMBER + "\\$";
    private static final String FROM_TEAM = FROM + " " + TEAM_NAME;
    private static final String FROM_TEAM_TO_TEAM = FROM_TEAM + " " + TO_TEAM;
    private static final String SELL_PLAYER = SELL + " " + PLAYER_WITH_NAME + " " + FROM_TEAM_TO_TEAM + " " + FOR + " "
            + PRICE + " " + CONTRACT_NUMBER_OF_YEARS;
    private static final String LOAN = "loan";
    private static final String LOAN_PLAYER = LOAN + " " + PLAYER_WITH_NAME + " " + FROM_TEAM_TO_TEAM + " " +
            CONTRACT_NUMBER_OF_YEARS;
    private static final String PUT = "put";
    private static final String IN_MAIN_SQUAD = "in main squad";
    private static final String PUT_PLAYER = PUT + " " + PLAYER_WITH_NAME + " " + FROM_TEAM + " " + IN_MAIN_SQUAD;
    private static final String FRIENDLY = "friendly match between";
    private static final String AND = "and";
    private static final String FRIENDLY_MATCH = FRIENDLY + " " + TEAM_NAME + " " + AND + " " + TEAM_NAME;
    private static final String NEXT_SEASON = "next season";
    private static final String INVALID_COMMAND = "invalid command";
    private static League league = new League();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        while (!command.equals(END)) {
            command = command.trim();
            if (command.matches(CREATE_TEAM))
                league.addTeam(command.split(" ")[2]);
            else if (command.matches(DELETE_TEAM))
                league.deleteTeam(command.split(" ")[2]);
            else if (command.matches(CREATE_PLAYER))
                createPlayer(command, scanner);
            else if (command.matches(PRINT_FREE_AGENTS))
                printFreeAgents();
            else if (command.matches(MOVE_FREE_AGENT))
                moveFreeAgent(command);
            else if (command.matches(PRINT_TEAM_STATS))
                league.printTeamStats(command.split(" ")[4]);
            else if (command.matches(PRINT_PLAYER_STATS))
                printPlayerStats(command);
            else if (command.matches(RENEW_CONTRACT))
                renewContract(command);
            else if (command.matches(TERMINATE_CONTRACT))
                terminateContract(command);
            else if (command.matches(SELL_PLAYER))
                sellPlayer(command);
            else if (command.matches(LOAN_PLAYER))
                loanPlayer(command);
            else if (command.matches(PUT_PLAYER))
                putPlayer(command);
            else if (command.matches(FRIENDLY_MATCH))
                holdFriendly(command);
            else if (command.matches(NEXT_SEASON))
                league.forwardSeason();
            else
                System.out.println(INVALID_COMMAND);
            command = scanner.nextLine();
        }
        scanner.close();
    }

    private static void createPlayer(String command, Scanner scanner) {
        String[] commandSplit = command.split(" ");
        league.addPlayer(commandSplit[2], commandSplit[3], Integer.parseInt(commandSplit[4]), commandSplit[5], scanner);
    }

    private static void printFreeAgents() {
        ArrayList<Player> freeAgents = league.getFreeAgents();
        Collections.sort(freeAgents, Player.comparator());
        for (int i = 0; i < freeAgents.size(); i++)
            System.out.println((i + 1) + ". " + freeAgents.get(i).toStringFreeAgent());
    }

    private static void moveFreeAgent(String command) {
        String[] commandSplit = command.split(" ");
        league.moveFreeAgent(commandSplit[3], commandSplit[4], commandSplit[6], Integer.parseInt(commandSplit[8]));
    }

    private static void printPlayerStats(String command) {
        String[] commandSplit = command.split(" ");
        league.printPlayerStats(commandSplit[4], commandSplit[5]);
    }

    private static void renewContract(String command) {
        String[] commandSplit = command.split(" ");
        league.renewContract(commandSplit[3], commandSplit[4], Integer.parseInt(commandSplit[6]));
    }

    private static void terminateContract(String command) {
        String[] commandSplit = command.split(" ");
        league.terminateContract(commandSplit[3], commandSplit[4]);
    }

    private static void sellPlayer(String command) {
        String[] commandSplit = command.split(" ");
        league.sell(commandSplit[2], commandSplit[3], commandSplit[5], commandSplit[7],
                Integer.parseInt(commandSplit[9].substring(0, commandSplit[9].length() - 1)), Integer.parseInt(commandSplit[11]));
    }

    private static void loanPlayer(String command) {
        String[] commandSplit = command.split(" ");
        league.loan(commandSplit[2], commandSplit[3], commandSplit[5], commandSplit[7],
                Integer.parseInt(commandSplit[9]));
    }

    private static void putPlayer(String command) {
        String[] commandSplit = command.split(" ");
        league.putPlayer(commandSplit[2], commandSplit[3], commandSplit[5]);
    }

    private static void holdFriendly(String command) {
        String[] commandSplit = command.split(" ");
        league.holdFriendly(commandSplit[3], commandSplit[5]);
    }
}
