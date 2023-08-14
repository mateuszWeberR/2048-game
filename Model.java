package com.codegym.task.task35.task3513;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    protected int score;
    protected int maxTile;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    public Model() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        score = 0;
        maxTile = 0;
        resetGameTiles();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public void resetGameTiles() {
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                gameTiles[x][y] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile() {
        List<Tile> emptyTilesList = getEmptyTiles();
        if (!emptyTilesList.isEmpty()) {
            int random = (int) (emptyTilesList.size() * Math.random());
            emptyTilesList.get(random).value = (Math.random() < 0.9 ? 2 : 4);
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTilesList = new ArrayList<>();
        for (Tile[] tiles : gameTiles) {
            for (int y = 0; y < gameTiles[0].length; y++) {
                if (tiles[y].isEmpty())
                    emptyTilesList.add(tiles[y]);
            }
        }
        return emptyTilesList;
    }

    public void left() {
        trySaveState();
        consolidateAndMerge();
    }

    public void up() {
        trySaveState();
        gameTiles = rotateGameTiles(3);
        consolidateAndMerge();
        gameTiles = rotateGameTiles(1);
    }

    public void right() {
        trySaveState();
        gameTiles = rotateGameTiles(2);
        consolidateAndMerge();
        gameTiles = rotateGameTiles(2);
    }

    public void down() {
        trySaveState();
        gameTiles = rotateGameTiles(1);
        consolidateAndMerge();
        gameTiles = rotateGameTiles(3);
    }

    private void consolidateAndMerge() {
        boolean anyChange = false;
        for (Tile[] tiles : gameTiles) {
            boolean isConsolidate = consolidateTiles(tiles);
            boolean isMerge = mergeTiles(tiles);
            if (isConsolidate | isMerge)
                anyChange = true;
        }
        if (anyChange) {
            addTile();
            isSaveNeeded = true;
        }
    }

    private boolean consolidateTiles(Tile[] tiles) {
        boolean isChange = false;
        for (int i = 0; i < 3; i++) {
            for (int x = 0; x < tiles.length -1; x++) {
                if (tiles[x].isEmpty() && !tiles[x + 1].isEmpty()) {
                    tiles[x].value = tiles[x + 1].value;
                    tiles[x + 1].value = 0;
                    isChange = true;
                }
            }
        }
        return isChange;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean isChange = false;
        for (int x = 0; x < tiles.length - 1; x++) {
            if (!tiles[x].isEmpty() && tiles[x].value == tiles[x + 1].value) {
                tiles[x].value += tiles[x + 1].value;
                score += tiles[x].value;
                isChange = true;

                if (tiles[x].value > maxTile)
                    maxTile = tiles[x].value;

                regroupTiles(tiles, x + 1);
            }
        }
        return isChange;
    }

    private void regroupTiles(Tile[] tiles, int x) {
        for (int i = x; i < tiles.length - 1; i++) {
            tiles[i].value = tiles[i + 1].value;
        }
        tiles[tiles.length - 1].value = 0;
    }

    private Tile[][] rotateGameTiles(int times) {
        Tile[][] tempTiles = gameTiles;
        for (int i = 0; i < times; i++) {
            tempTiles = rotateClockwise(tempTiles);
        }
        return tempTiles;
    }

    private Tile[][] rotateClockwise(Tile[][] tempTiles) {
        int row = tempTiles.length;
        int col = tempTiles[0].length;
        Tile[][] result = new Tile[col][row];
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                result[y][row-1-x] = tempTiles[x][y];
            }
        }
        return result;
    }

    public boolean canMove() {
        List<Tile> emptyTilesList = getEmptyTiles();
        if (!emptyTilesList.isEmpty())
            return true;
        return canMerge();
    }

    private boolean canMerge() {
        for (int x = 0; x < gameTiles.length; x++) {
            for (int y = 0; y < gameTiles[0].length; y++) {
                if (hasSameNeighbor(gameTiles[x][y], x, y))
                    return true;
            }
        }
        return false;
    }

    private boolean hasSameNeighbor(Tile tile, int x, int y) {
        if (inBounds(x + 1, y) && tile.value == gameTiles[x + 1][y].value)
            return true;
        if (inBounds(x - 1, y) && tile.value == gameTiles[x - 1][y].value)
            return true;
        if (inBounds(x, y + 1) && tile.value == gameTiles[x][y + 1].value)
            return true;
        if (inBounds(x, y - 1) && tile.value == gameTiles[x][y - 1].value)
            return true;
        return false;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < gameTiles.length && y >= 0 && y < gameTiles[0].length;
    }

    private void saveState(Tile[][] gameTiles) {
        Tile[][] tempGameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int x = 0; x < gameTiles.length; x++) {
            for (int y = 0; y < gameTiles[0].length; y++) {
                tempGameTiles[x][y] = new Tile(gameTiles[x][y].value);
            }
        }
        previousStates.push(tempGameTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }
    
    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    private void trySaveState() {
        if (isSaveNeeded)
            saveState(gameTiles);
    }

    public void randomMove() {
        int random = (int) (Math.random() * 4) + 1;

        switch (random) {
            case 1:
                up();
                break;
            case 2:
                down();
                break;
            case 3:
                left();
                break;
            case 4:
                right();
        }
    }

    public boolean hasBoardChanged() {
        Tile[][] tempGameTiles = previousStates.peek();

        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[0].length; j++) {
                if (gameTiles[i][j].value != tempGameTiles[i][j].value)
                    return true;
            }
        }
        return false;
    }

    public MoveFitness getMoveFitness(Move move) {
        move.move();
        MoveFitness moveFitness = null;
        if (!hasBoardChanged()) {
            moveFitness = new MoveFitness(-1, 0, move);
        } else {
            moveFitness = new MoveFitness(getEmptyTiles().size(), score, move);
        }
        rollback();
        return moveFitness;
    }

    public void autoMove() {
        PriorityQueue<MoveFitness> priorityQueue =
                new PriorityQueue<>(4, Collections.reverseOrder());

        priorityQueue.add(getMoveFitness(this::up));
        priorityQueue.add(getMoveFitness(this::down));
        priorityQueue.add(getMoveFitness(this::left));
        priorityQueue.add(getMoveFitness(this::right));

        priorityQueue.peek().getMove().move();
    }
}
