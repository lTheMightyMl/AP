package sample.classes;

import sample.Main;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private static final int[] dx = {1, 0, -1, 0};
    private static final int[] dy = {0, 1, 0, -1};
    private int dimensions;
    private int[][] cells;
    private int[][] previousCells;
    private boolean[][] merged;
    private int score = 0;
    private Random random = new Random();

    public int[][] getPreviousCells() {
        return previousCells;
    }

    public Game(int dimensions) {
        this.dimensions = dimensions;
        cells = new int[dimensions][dimensions];
        previousCells = new int[dimensions][dimensions];
        merged = new boolean[dimensions][dimensions];
        fillARandomCell();
    }

    private void emptyMerged() {
        for (boolean[] row : merged)
            for (int i = 0; i < row.length; i++)
                row[i] = false;
    }

    public int[][] getCells() {
        return cells;
    }

    public int getScore() {
        return score;
    }

    public void fillARandomCell() {
        int numberOfEmptyCells = 0;
        for (int[] row : cells)
            for (int cell : row)
                if (cell == 0)
                    numberOfEmptyCells++;
        if (numberOfEmptyCells == 0)
            return;
        int number = random.nextInt(numberOfEmptyCells);
        numberOfEmptyCells = 0;
        for (int[] row : cells)
            for (int i = 0; i < row.length; i++)
                if (row[i] == 0)
                    if (number == numberOfEmptyCells) {
                        row[i] = (random.nextInt(2) > 0 ? 4 : 2);
                        return;
                    } else
                        numberOfEmptyCells++;
    }

    public void moveUp() {
        emptyMerged();
        copyCells();
        ArrayList<ArrayList<Coordinates>> destinations = getEmptyDestinations();
        boolean hasMoved = false;
        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++) {
                int column = i;
                int row = j;
                if (cells[row][column] == 0)
                    continue;
                while (row > 0 && cells[row - 1][column] == 0) {
                    cells[row - 1][column] = cells[row][column];
                    cells[row][column] = 0;
                    row--;
                    hasMoved = true;
                }
                if ((row > 0) && (cells[row - 1][column] == cells[row][column]) && (!merged[row - 1][column])) {
                    cells[row - 1][column] = 2 * cells[row][column];
                    score += cells[row - 1][column];
                    cells[row][column] = 0;
                    hasMoved = true;
                    merged[row - 1][column] = true;
                    row--;
                }
                destinations.get(j).set(i, new Coordinates(row, column));
            }
        }
        if (hasMoved)
            fillARandomCell();
        Main.setDestinations(destinations);
    }

    public void moveDown() {
        emptyMerged();
        copyCells();
        ArrayList<ArrayList<Coordinates>> destinations = getEmptyDestinations();
        boolean hasMoved = false;
        for (int i = 0; i < dimensions; i++) {
            for (int j = dimensions - 1; j > -1; j--) {
                int column = i;
                int row = j;
                if (cells[row][column] == 0)
                    continue;
                while (row < dimensions - 1 && cells[row + 1][column] == 0) {
                    cells[row + 1][column] = cells[row][column];
                    cells[row][column] = 0;
                    row++;
                    hasMoved = true;
                }
                if ((row < dimensions - 1) && (cells[row + 1][column] == cells[row][column]) && (!merged[row + 1][column
                        ])) {
                    cells[row + 1][column] = 2 * cells[row][column];
                    score += cells[row + 1][column];
                    cells[row][column] = 0;
                    hasMoved = true;
                    merged[row + 1][column] = true;
                    row++;
                }
                destinations.get(j).set(i, new Coordinates(row, column));
            }
        }
        if (hasMoved)
            fillARandomCell();
        Main.setDestinations(destinations);
    }

    public void moveRight() {
        emptyMerged();
        copyCells();
        ArrayList<ArrayList<Coordinates>> destinations = getEmptyDestinations();
        boolean hasMoved = false;
        for (int i = 0; i < dimensions; i++) {
            for (int j = dimensions - 1; j > -1; j--) {
                int row = i;
                int column = j;
                if (cells[row][column] == 0)
                    continue;
                while (column < dimensions - 1 && cells[row][column + 1] == 0) {
                    cells[row][column + 1] = cells[row][column];
                    cells[row][column] = 0;
                    column++;
                    hasMoved = true;
                }
                if ((column < dimensions - 1) && (cells[row][column + 1] == cells[row][column]) && (!merged[row][column
                        + 1]))
                {
                    cells[row][column + 1] = 2 * cells[row][column];
                    score += cells[row][column + 1];
                    cells[row][column] = 0;
                    hasMoved = true;
                    merged[row][column + 1] = true;
                    column++;
                }
                destinations.get(i).set(j, new Coordinates(row, column));
            }
        }
        if (hasMoved)
            fillARandomCell();
        Main.setDestinations(destinations);
    }

    public void moveLeft() {
        emptyMerged();
        copyCells();
        ArrayList<ArrayList<Coordinates>> destinations = getEmptyDestinations();
        boolean hasMoved = false;
        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++) {
                int row = i;
                int column = j;
                if (cells[row][column] == 0)
                    continue;
                while (column > 0 && cells[row][column - 1] == 0) {
                    cells[row][column - 1] = cells[row][column];
                    cells[row][column] = 0;
                    column--;
                    hasMoved = true;
                }
                if ((column > 0) && (cells[row][column - 1] == cells[row][column]) && (!merged[row][column - 1])) {
                    cells[row][column - 1] = 2 * cells[row][column];
                    score += cells[row][column - 1];
                    cells[row][column] = 0;
                    hasMoved = true;
                    merged[row][column - 1] = true;
                    column--;
                }
                destinations.get(i).set(j, new Coordinates(row, column));
            }
        }
        if (hasMoved)
            fillARandomCell();
        Main.setDestinations(destinations);
    }

    private void copyCells() {
        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < cells[i].length; j++)
                previousCells[i][j] = cells[i][j];
    }

    public boolean hasEnded() {
        for (int[] row : cells)
            for (int cell : row)
                if (cell == 0)
                    return false;
        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < cells[i].length; j++)
                for (int k = 0; k < dx.length; k++)
                    for (int l = 0; l < dy.length; l++) {
                        int xp = i + dx[k];
                        int yp = j + dy[k];
                        if (isInMap(xp, yp) && cells[xp][yp] == cells[i][j])
                            return false;
                    }
        return true;
    }

    private boolean isInMap(int x, int y) {
        return x >= 0 && x < dimensions && y >= 0 && y < dimensions;
    }

    private ArrayList<ArrayList<Coordinates>> getEmptyDestinations() {
        ArrayList<ArrayList<Coordinates>> destination = new ArrayList<>();
        for (int i = 0; i < dimensions; i++) {
            ArrayList<Coordinates> row = new ArrayList<>();
            for (int j = 0; j < dimensions; j++) {
                row.add(new Coordinates(i, j));
            }
            destination.add(row);
        }
        return destination;
    }
}
