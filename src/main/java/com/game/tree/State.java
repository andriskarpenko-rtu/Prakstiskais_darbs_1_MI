package com.game.tree;

public class State {

    public int number;
    public int playerScore;
    public int computerScore;
    public boolean playerTurn;

    public State(int number, int playerScore, int computerScore, boolean playerTurn) {
        this.number = number;
        this.playerScore = playerScore;
        this.computerScore = computerScore;
        this.playerTurn = playerTurn;
    }

    public boolean isTerminal() {
        return number >= 1200;
    }
}
