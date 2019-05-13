import classes.Game;
import classes.Player;

import java.util.Scanner;

enum State {
    MAIN_MENU, RESUME_MENU, SCOREBOARD, IN_GAME
}

public class Interactor {
    private static final String NAME = "\\w+";
    private static final String NEW_GAME = "new game( " + NAME + "){";
    private static final String GENERIC_NEW_GAME = NEW_GAME + "0,2}";
    private static final String PROPER_NEW_GAME = NEW_GAME + "2}";
    private static final String RESUME = "resume";
    private static final String NUMBER = "\\d+";
    private static final String PROPER_SET_TABLE = "set table( " + NUMBER + "\\*" + NUMBER + ")";
    private static final String GENERIC_SET_TABLE = PROPER_SET_TABLE + "?";
    private static final String SCOREBOARD = "scoreboard";
    private static final String BACK = "back";
    private static final String QUIT = "quit";
    private static final String PUT = "put\\(" + NUMBER + "," + NUMBER + "\\)";
    private static final String UNDO = "undo";
    private static final String PAUSE = "pause";
    private static final String STOP = "stop";
    private static final String INVALID_COMMAND = "Invalid command";
    private static final String INVALID_PLAYERS = "Invalid Players";
    private static final String INVALID_NUMBER = "Invalid number";
    private static Game currentGame = null;
    private static State state = State.MAIN_MENU;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        while (true) {
            command = command.trim();
            if (state == State.MAIN_MENU) {
                if (actMainMenu(command))
                    return;
            } else if (state == State.RESUME_MENU) {
                actResumeMenu(command);
            } else if (state == State.SCOREBOARD) {
                if (actScoreboard(command))
                    return;
            } else
                actInGame(command);
            command = scanner.nextLine();
        }
    }

    private static void actInGame(String command) {
        if (command.matches(PUT))
            putToken(command);
        else if (command.matches(UNDO)) {
            if (currentGame != null)
                currentGame.undo();
        } else if (command.matches(PAUSE))
            pauseGame();
        else if (command.matches(STOP))
            stopGame();
        else if (currentGame != null)
            currentGame.showTable();
    }

    private static boolean actScoreboard(String command) {
        if (command.matches(BACK))
            state = State.MAIN_MENU;
        else if (command.matches(QUIT))
            return true;
        else
            System.out.println(INVALID_COMMAND);
        return false;
    }

    private static void actResumeMenu(String command) {
        if (command.matches(NUMBER))
            resumeGame(Integer.parseInt(command));
        else if (command.matches(BACK))
            state = State.MAIN_MENU;
        else
            System.out.println(INVALID_COMMAND);
    }

    private static boolean actMainMenu(String command) {
        if (command.matches(GENERIC_NEW_GAME))
            if (command.matches(PROPER_NEW_GAME))
                currentGame = makeNewGame(command);
            else
                System.out.println(INVALID_PLAYERS);
        else if (command.matches(RESUME))
            goToResumeMenu();
        else if (command.matches(GENERIC_SET_TABLE))
            if (command.matches(PROPER_SET_TABLE))
                setTable(command);
            else
                Game.resetDimensions();
        else if (command.matches(SCOREBOARD))
            goToScoreboard();
        else if (command.matches(QUIT))
            return true;
        else
            System.out.println(INVALID_COMMAND);
        return false;
    }

    private static void stopGame() {
        state = State.MAIN_MENU;
        Game.stop(currentGame);
        currentGame = null;
    }

    private static void pauseGame() {
        state = State.MAIN_MENU;
        if (currentGame != null)
            currentGame.pause();
        currentGame = null;
    }

    private static void putToken(String command) {
        String[] commandSplit = command.split(",");
        if (currentGame == null)
            return;
        if (!currentGame.put(Integer.parseInt(commandSplit[0].split("\\(")[1]), Integer.parseInt(commandSplit[1].split("\\)")[0]))) {
            currentGame = null;
            state = State.MAIN_MENU;
        }
    }

    private static void resumeGame(int index) {
        if (index < 1 || index > Game.numberOfGamesInProgress()) {
            System.out.println(INVALID_NUMBER);
        } else {
            currentGame = Game.getGame(index);
            if (currentGame != null)
                currentGame.showTable();
            state = State.IN_GAME;
        }
    }

    private static void goToResumeMenu() {
        state = State.RESUME_MENU;
        Game.showGamesInProgress();
    }

    private static void goToScoreboard() {
        Player.showScoreboard();
        state = State.SCOREBOARD;
    }

    private static void setTable(String command) {
        String[] dimensions = command.split(" ")[2].split("\\*");
        Game.setDimensions(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
    }

    private static Game makeNewGame(String command) {
        String[] commandSplit = command.split(" ");
        state = State.IN_GAME;
        return new Game(commandSplit[2], commandSplit[3]);
    }
}