package com.game.tree;

import com.game.tree.Node;
import com.game.tree.State;

public class Tree {

    private static final int[] MOVES = {2, 3, 4};

    public void generateChildren(Node node) {

        if (node.state.isTerminal()) {
            return;
        }

        for (int move : MOVES) {

            int newNumber = node.state.number * move;

            int playerScore = node.state.playerScore;
            int computerScore = node.state.computerScore;

            if (newNumber % 2 == 0) {
                if (node.state.playerTurn)
                    computerScore--;
                else
                    playerScore--;
            } else {
                if (node.state.playerTurn)
                    playerScore++;
                else
                    computerScore++;
            }

            State newState = new State(
                    newNumber,
                    playerScore,
                    computerScore,
                    !node.state.playerTurn
            );

            Node child = new Node(newState);

            node.children.add(child);
        }
    }
}