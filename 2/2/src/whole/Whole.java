/*
package whole;

import java.util.*;

enum State {
    MAIN_MENU, RESUME_MENU, SCOREBOARD, IN_GAME
}

public class Whole {
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
            actScoreboard();
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

    private static void actScoreboard() {
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


class Game {
    private static final int NUMBER_OF_PLAYERS = 2;
    private static final int DEFAULT_DIMENSION = 3;
    private static final char EMPTY_CELL = '_';
    private static final char[] PLAYER_TOKEN = {'X', 'O'};
    private static int n = 3;
    private static int m = 3;
    private static ArrayList<Game> gamesInProgress = new ArrayList<>();
    private int rows;
    private int columns;
    private char[][] table;
    private int winningNumber;
    private int winningLength;
    private boolean[] hasUndone = {false, false};
    private int playerTurn = 0;
    private Player[] players = new Player[2];
    private ArrayList<Move> moves = new ArrayList<>();

    public Game(String player1Name, String player2Name) {
        rows = n;
        columns = m;
        table = new char[n][m];
        for (char[] row : table)
            Arrays.fill(row, EMPTY_CELL);
        if (n == DEFAULT_DIMENSION || m == DEFAULT_DIMENSION)
            winningNumber = 3;
        else
            winningNumber = 4;
        winningLength = winningNumber - 1;
        players[0] = Player.addPlayer(player1Name);
        players[1] = Player.addPlayer(player2Name);
        gamesInProgress.add(this);
        showTable();
    }

    public static void setDimensions(int newN, int newM) {
        n = newN;
        m = newM;
    }

    public static void resetDimensions() {
        n = m = DEFAULT_DIMENSION;
    }

    public static void showGamesInProgress() {
        for (int i = gamesInProgress.size() - 1; i > -1; i--) {
            int index = gamesInProgress.size() - i;
            Game game = gamesInProgress.get(i);
            if (game != null)
                System.out.println(index + ". " + game.players[0].getName() + " " + game.players[1].getName());
        }
    }

    public static int numberOfGamesInProgress() {
        return gamesInProgress.size();
    }

    public static Game getGame(int index) {
        return gamesInProgress.get(gamesInProgress.size() - index);
    }

    public static void stop(Game game) {
        gamesInProgress.remove(game);
    }

    private int determineWinner() {
        for (int rowDirection = -1; rowDirection < 2; rowDirection++)
            for (int columnDirection = -1; columnDirection < 2; columnDirection++)
                if (rowDirection != 0 || columnDirection != 0)
                    if (check(rowDirection, columnDirection, PLAYER_TOKEN[0]))
                        return 0;
                    else if (check(rowDirection, columnDirection, PLAYER_TOKEN[1]))
                        return 1;
        return -1;
    }

    private boolean check(int rowDirection, int columnDirection, char token) {
        int columnMargin = -winningLength * columnDirection;
        int firstColumn = Integer.max(0, columnMargin);
        int lastColumn = columns + columnMargin - firstColumn;
        int rowMargin = -winningLength * rowDirection;
        int firstRow = Integer.max(0, rowMargin);
        int lastRow = rows + rowMargin - firstRow;
        for (int row = firstRow; row < lastRow; row++) {
            for (int column = firstColumn; column < lastColumn; column++) {
                boolean found = true;
                for (int i = 0; i < winningNumber && found; i++)
                    if (table[row + rowDirection * i][column + columnDirection * i] != token)
                        found = false;
                if (found)
                    return true;
            }
        }
        return false;
    }

    public void undo() {
        int theOtherPlayer = NUMBER_OF_PLAYERS - 1 - playerTurn;
        if (moves.size() < 2 || hasUndone[theOtherPlayer]) {
            System.out.println("Invalid undo");
            showTable();
            return;
        }
        Move lastMove = moves.get(moves.size() - 1);
        table[lastMove.row][lastMove.column] = EMPTY_CELL;
        playerTurn = theOtherPlayer;
        moves.remove(lastMove);
        showTable();
        hasUndone[theOtherPlayer] = true;
    }

    public void showTable() {
        for (char[] row : table) {
            for (int i = 0; i < columns - 1; i++)
                System.out.print(row[i] + "|");
            System.out.println(row[columns - 1]);
        }
        System.out.println(players[playerTurn].getName());
    }

    public boolean put(int row, int column) {
        if (row < 1 || row > rows || column < 1 || column > columns || table[row - 1][column - 1] != EMPTY_CELL) {
            System.out.println("Invalid coordination");
            showTable();
            return true;
        }
        row--;
        column--;
        moves.add(new Move(row, column));
        table[row][column] = PLAYER_TOKEN[playerTurn];
        int winner = determineWinner();
        int theOtherPlayer = NUMBER_OF_PLAYERS - 1 - playerTurn;
        if (winner > -1) {
            System.out.println("Player " + players[playerTurn].getName() + " won");
            players[playerTurn].addWin();
            players[theOtherPlayer].addLoss();
            gamesInProgress.remove(this);
            return false;
        } else if (isFull()) {
            System.out.println("Draw");
            players[playerTurn].addDraw();
            players[theOtherPlayer].addDraw();
            gamesInProgress.remove(this);
            return false;
        }
        playerTurn = theOtherPlayer;
        showTable();
        return true;
    }

    private boolean isFull() {
        for (char[] row : table)
            for (char cell : row)
                if (cell == EMPTY_CELL)
                    return false;
        return true;
    }

    public void pause() {
        gamesInProgress.remove(this);
        gamesInProgress.add(this);
    }
}

class Move {
    public int row;
    public int column;

    public Move(int row, int column) {
        this.row = row;
        this.column = column;
    }
}


class Player {
    static ArrayList<Player> players = new ArrayList<>();
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private String name;

    private Player(String name) {
        this.name = name;
        players.add(this);
    }

    static Comparator<Player> playerComparator() {
        return (o1, o2) -> {
            if (o1.wins < o2.wins)
                return 1;
            if (o1.wins > o2.wins)
                return -1;
            if (o1.losses > o2.losses)
                return 1;
            if (o1.losses < o2.losses)
                return -1;
            if (o1.draws > o2.draws)
                return 1;
            if (o1.draws < o2.draws)
                return -1;
            return o1.name.compareTo(o2.name);
        };
    }

    public static Player addPlayer(String name) {
        Player currentPlayer = getPlayer(name);
        if (currentPlayer == null)
            currentPlayer = new Player(name);
        return currentPlayer;
    }

    private static Player getPlayer(String name) {
        for (Player player : players)
            if (player.name.equals(name))
                return player;
        return null;
    }

    public static void showScoreboard() {
        ArrayList<Player> scoreboard = new ArrayList<>(players);
        Collections.sort(scoreboard, playerComparator());
        for (Player player : scoreboard)
            System.out.println(player);
    }

    public void addWin() {
        wins++;
    }

    public void addDraw() {
        draws++;
    }

    public void addLoss() {
        losses++;
    }

    @Override
    public String toString() {
        return name + " " + wins + " " + losses + " " + draws;
    }

    public String getName() {
        return name;
    }
}*/
