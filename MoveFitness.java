package com.codegym.task.task35.task3513;

public class MoveFitness implements Comparable<MoveFitness> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveFitness(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveFitness o) {
        int result = Integer.compare(this.numberOfEmptyTiles, o.numberOfEmptyTiles);
        if (result == 0) {
            result = Integer.compare(this.score, o.score);
        }
        return result;
    }
}
