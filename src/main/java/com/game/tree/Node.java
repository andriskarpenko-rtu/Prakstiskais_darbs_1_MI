package com.game.tree;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public State state;
    public List<Node> children;

    public Node(State state) {
        this.state = state;
        this.children = new ArrayList<>();
    }
}
