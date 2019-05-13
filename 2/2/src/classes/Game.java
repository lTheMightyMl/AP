package classes;

import java.util.ArrayList;
import java.util.Arrays;

public class Game {
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
            for (char cell : row)
                System.out.print(cell + "|");
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