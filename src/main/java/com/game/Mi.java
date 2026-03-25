package com.game;

import com.game.tree.Node;
import com.game.tree.State;
import com.game.tree.Tree;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int startNumber;

        while (true) {
            System.out.print("Enter start number (8–18): ");
            startNumber = scanner.nextInt();

            if (startNumber >= 8 && startNumber <= 18) {
                break;
            }

            System.out.println("Invalid number. Please enter a number between 8 and 18.");
        }

        State start = new State(startNumber, 0, 0, true);

        Node root = new Node(start);

        Tree tree = new Tree();

        tree.generateChildren(root);

        System.out.println("Children count: " + root.children.size());

//        for (Node child : root.children) {
//            System.out.println("Child number: " + child.state.number);
//        }
    }
}
