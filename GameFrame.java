import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GameFrame extends JFrame {

    private static final int[] MOVES = {2, 3, 4};
    private static final int SEARCH_DEPTH = 5;

    int currentNum, playerPts, compPts;
    boolean playerTurn, alphaBeta;

    int moveCounter = 1;
    int generatedNodes = 0;
    int evaluatedNodes = 0;
    double lastMoveTimeMs = 0.0;

    JTextField numField;
    JLabel errLabel, lblNum, lblPts, lblCompPts, lblTurn, lblWinner, lblSub, lblStats;
    JToggleButton btnAB, btnComp;
    JButton b2, b3, b4, btnSaveLog;
    JTextArea log;

    CardLayout cl = new CardLayout();
    JPanel root = new JPanel(cl);

    public GameFrame() {
        setTitle("Reizināšanas spēle");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        root.add(setupPanel(), "setup");
        root.add(gamePanel(), "game");
        root.add(resultPanel(), "result");
        add(root);

        setVisible(true);
    }

    JPanel setupPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        p.add(new JLabel("Sākuma skaitlis (8–18):"));
        p.add(Box.createVerticalStrut(4));

        numField = new JTextField();
        numField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        p.add(numField);

        errLabel = new JLabel(" ");
        errLabel.setForeground(Color.RED);
        p.add(errLabel);

        p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Algoritms:"));
        p.add(Box.createVerticalStrut(4));

        JToggleButton btnMM = new JToggleButton("Minimax", true);
        btnAB = new JToggleButton("Alfa-beta", false);
        ButtonGroup ag = new ButtonGroup();
        ag.add(btnMM);
        ag.add(btnAB);
        p.add(row(btnMM, btnAB));

        p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Kurš sāk:"));
        p.add(Box.createVerticalStrut(4));

        JToggleButton btnHuman = new JToggleButton("Cilvēks", true);
        btnComp = new JToggleButton("Dators", false);
        ButtonGroup fg = new ButtonGroup();
        fg.add(btnHuman);
        fg.add(btnComp);
        p.add(row(btnHuman, btnComp));

        p.add(Box.createVerticalStrut(16));
        JButton start = new JButton("Sākt spēli");
        start.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        start.addActionListener(e -> startGame());
        numField.addActionListener(e -> startGame());
        p.add(start);

        return p;
    }

    JPanel gamePanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        lblPts = new JLabel("0");
        lblCompPts = new JLabel("0");
        lblPts.setFont(lblPts.getFont().deriveFont(24f));
        lblCompPts.setFont(lblCompPts.getFont().deriveFont(24f));
        lblPts.setAlignmentX(CENTER_ALIGNMENT);
        lblCompPts.setAlignmentX(CENTER_ALIGNMENT);

        JPanel pb = col("Cilvēks", lblPts);
        JPanel cb = col("Dators", lblCompPts);

        JPanel scoreRow = new JPanel(new GridLayout(1, 2, 10, 0));
        scoreRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        scoreRow.add(pb);
        scoreRow.add(cb);
        p.add(scoreRow);

        p.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("pašreizējais skaitlis");
        hint.setForeground(Color.GRAY);
        hint.setAlignmentX(CENTER_ALIGNMENT);
        p.add(hint);

        lblNum = new JLabel("—");
        lblNum.setFont(lblNum.getFont().deriveFont(42f));
        lblNum.setAlignmentX(CENTER_ALIGNMENT);
        p.add(lblNum);

        p.add(Box.createVerticalStrut(6));

        lblTurn = new JLabel("Tavs gājiens");
        lblTurn.setAlignmentX(CENTER_ALIGNMENT);
        p.add(lblTurn);

        p.add(Box.createVerticalStrut(8));

        lblStats = new JLabel("Statistika: -");
        lblStats.setForeground(Color.DARK_GRAY);
        lblStats.setAlignmentX(CENTER_ALIGNMENT);
        p.add(lblStats);

        p.add(Box.createVerticalStrut(10));

        b2 = new JButton("×2");
        b3 = new JButton("×3");
        b4 = new JButton("×4");

        b2.addActionListener(e -> playerMove(2));
        b3.addActionListener(e -> playerMove(3));
        b4.addActionListener(e -> playerMove(4));

        JPanel br = new JPanel(new GridLayout(1, 3, 8, 0));
        br.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        br.add(b2);
        br.add(b3);
        br.add(b4);
        p.add(br);

        p.add(Box.createVerticalStrut(10));

        btnSaveLog = new JButton("Saglabāt spēles gaitu failā");
        btnSaveLog.setAlignmentX(CENTER_ALIGNMENT);
        btnSaveLog.addActionListener(e -> saveLogToFile());
        p.add(btnSaveLog);

        p.add(Box.createVerticalStrut(10));

        log = new JTextArea(12, 0);
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setBackground(new Color(245, 245, 243));

        JScrollPane sp = new JScrollPane(log);
        sp.setPreferredSize(new Dimension(390, 250));
        p.add(sp);

        return p;
    }

    JPanel resultPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(60, 24, 24, 24));

        lblWinner = new JLabel("—");
        lblWinner.setFont(lblWinner.getFont().deriveFont(22f));
        lblWinner.setAlignmentX(CENTER_ALIGNMENT);

        lblSub = new JLabel(" ");
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        JButton saveResultLog = new JButton("Saglabāt spēles gaitu failā");
        saveResultLog.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        saveResultLog.setAlignmentX(CENTER_ALIGNMENT);
        saveResultLog.addActionListener(e -> saveLogToFile());

        JButton again = new JButton("Spēlēt vēlreiz");
        again.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        again.setAlignmentX(CENTER_ALIGNMENT);
        again.addActionListener(e -> {
            numField.setText("");
            errLabel.setText(" ");
            cl.show(root, "setup");
        });

        p.add(lblWinner);
        p.add(Box.createVerticalStrut(8));
        p.add(lblSub);
        p.add(Box.createVerticalStrut(20));
        p.add(saveResultLog);
        p.add(Box.createVerticalStrut(10));
        p.add(again);

        return p;
    }

    JPanel row(JComponent a, JComponent b) {
        JPanel r = new JPanel(new GridLayout(1, 2, 6, 0));
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        r.add(a);
        r.add(b);
        return r;
    }

    JPanel col(String name, JLabel num) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(240, 240, 238));
        p.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel l = new JLabel(name);
        l.setAlignmentX(CENTER_ALIGNMENT);
        num.setAlignmentX(CENTER_ALIGNMENT);

        p.add(l);
        p.add(num);
        return p;
    }

    void startGame() {
        int n;
        try {
            n = Integer.parseInt(numField.getText().trim());
        } catch (NumberFormatException ex) {
            n = -1;
        }

        if (n < 8 || n > 18) {
            errLabel.setText("Ievadi skaitli no 8 līdz 18.");
            return;
        }

        errLabel.setText(" ");
        currentNum = n;
        playerPts = 0;
        compPts = 0;
        moveCounter = 1;
        generatedNodes = 0;
        evaluatedNodes = 0;
        lastMoveTimeMs = 0.0;

        alphaBeta = btnAB.isSelected();
        playerTurn = !btnComp.isSelected();

        log.setText("");
        log.append("=== JAUNA SPĒLE ===\n");
        log.append("Sākuma skaitlis: " + currentNum + "\n");
        log.append("Algoritms: " + (alphaBeta ? "Alfa-beta" : "Minimax") + "\n");
        log.append("Pirmais spēlē: " + (playerTurn ? "Cilvēks" : "Dators") + "\n\n");

        refresh();
        cl.show(root, "game");

        if (!playerTurn) {
            setBtns(false);
            scheduleComp();
        } else {
            setBtns(true);
        }
    }

    void playerMove(int move) {
        if (!playerTurn) return;

        log.append(doMove(move, true) + "\n");

        playerTurn = false;
        refresh();

        if (checkEnd()) return;

        setBtns(false);
        scheduleComp();
    }

    void compMove() {
        long start = System.nanoTime();
        int move = bestMove();
        long end = System.nanoTime();

        lastMoveTimeMs = (end - start) / 1_000_000.0;

        log.append(doMove(move, false) + "\n");

        playerTurn = true;
        refresh();

        if (checkEnd()) return;

        setBtns(true);
    }

    String doMove(int move, boolean byPlayer) {
        int old = currentNum;
        int result = currentNum * move;

        String actor = byPlayer ? "Cilvēks" : "Dators";
        String effect;

        if (result % 2 == 0) {
            if (byPlayer) {
                compPts--;
                effect = "Dators -1";
            } else {
                playerPts--;
                effect = "Cilvēks -1";
            }
        } else {
            if (byPlayer) {
                playerPts++;
                effect = "Cilvēks +1";
            } else {
                compPts++;
                effect = "Dators +1";
            }
        }

        currentNum = result;

        return moveCounter++ + ". " + actor + ": " + old + " ×" + move + " = " + result +
                " → " + effect +
                " (Cilvēks: " + playerPts + ", Dators: " + compPts + ")";
    }

    int bestMove() {
        generatedNodes = 0;
        evaluatedNodes = 0;

        State currentState = new State(currentNum, playerPts, compPts, false);
        Node rootNode = new Node(currentState);

        generateTree(rootNode, SEARCH_DEPTH);

        int bestValue = Integer.MIN_VALUE;
        int bestMove = 2;

        for (Node child : rootNode.children) {
            int value = minimax(
                    child,
                    SEARCH_DEPTH - 1,
                    false,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE
            );

            int moveUsed = child.state.number / rootNode.state.number;

            if (value > bestValue) {
                bestValue = value;
                bestMove = moveUsed;
            }
        }

        return bestMove;
    }

    void generateTree(Node node, int depth) {
        if (depth == 0 || node.state.isTerminal()) {
            return;
        }

        generateChildren(node);

        for (Node child : node.children) {
            generateTree(child, depth - 1);
        }
    }

    void generateChildren(Node node) {
        if (node.state.isTerminal()) {
            return;
        }

        for (int move : MOVES) {
            State childState = applyMove(node.state, move);
            Node child = new Node(childState);
            node.children.add(child);
            generatedNodes++;
        }
    }

    State applyMove(State state, int move) {
        int newNumber = state.number * move;
        int newPlayerPts = state.playerPts;
        int newCompPts = state.compPts;

        if (newNumber % 2 == 0) {
            if (state.playerTurn) {
                newCompPts--;
            } else {
                newPlayerPts--;
            }
        } else {
            if (state.playerTurn) {
                newPlayerPts++;
            } else {
                newCompPts++;
            }
        }

        return new State(newNumber, newPlayerPts, newCompPts, !state.playerTurn);
    }

    int evaluate(State state) {
        evaluatedNodes++;
        return state.compPts - state.playerPts;
    }

    int minimax(Node node, int depth, boolean isMax, int alpha, int beta) {
        if (depth == 0 || node.state.isTerminal() || node.children.isEmpty()) {
            return evaluate(node.state);
        }

        if (isMax) {
            int best = Integer.MIN_VALUE;

            for (Node child : node.children) {
                int value = minimax(child, depth - 1, false, alpha, beta);
                best = Math.max(best, value);

                if (alphaBeta) {
                    alpha = Math.max(alpha, value);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }

            return best;
        } else {
            int best = Integer.MAX_VALUE;

            for (Node child : node.children) {
                int value = minimax(child, depth - 1, true, alpha, beta);
                best = Math.min(best, value);

                if (alphaBeta) {
                    beta = Math.min(beta, value);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }

            return best;
        }
    }

    boolean checkEnd() {
        if (currentNum < 1200) return false;

        String winner;
        if (playerPts > compPts) {
            winner = "Tu uzvarēji!";
        } else if (compPts > playerPts) {
            winner = "Dators uzvarēja.";
        } else {
            winner = "Neizšķirts.";
        }

        log.append("\n=== SPĒLES BEIGAS ===\n");
        log.append("Rezultāts: Cilvēks " + playerPts + " | Dators " + compPts + "\n");
        log.append("Uzvarētājs: " + winner + "\n");

        lblWinner.setText(winner);
        lblSub.setText("Cilvēks " + playerPts + " pts  ·  Dators " + compPts + " pts  ·  Skaitlis " + currentNum);

        cl.show(root, "result");
        return true;
    }

    void scheduleComp() {
        Timer t = new Timer(700, e -> {
            compMove();
            ((Timer) e.getSource()).stop();
        });
        t.setRepeats(false);
        t.start();
    }

    void refresh() {
        lblNum.setText(String.valueOf(currentNum));
        lblPts.setText(String.valueOf(playerPts));
        lblCompPts.setText(String.valueOf(compPts));
        lblTurn.setText(playerTurn ? "Tavs gājiens" : "Dators domā...");

        lblStats.setText(String.format(
                "Statistika: ģenerētās virsotnes = %d, novērtētās virsotnes = %d, laiks = %.3f ms",
                generatedNodes, evaluatedNodes, lastMoveTimeMs
        ));
    }

    void setBtns(boolean enabled) {
        b2.setEnabled(enabled);
        b3.setEnabled(enabled);
        b4.setEnabled(enabled);
    }

    void saveLogToFile() {
        if (log.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nav ko saglabāt. Spēles gaita vēl nav izveidota.",
                    "Informācija",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Saglabāt spēles gaitu");
        chooser.setSelectedFile(new File("spele_log.txt"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(log.getText());
            writer.write("\n");
            writer.write("Statistika:\n");
            writer.write("Ģenerētās virsotnes: " + generatedNodes + "\n");
            writer.write("Novērtētās virsotnes: " + evaluatedNodes + "\n");
            writer.write(String.format("Pēdējā gājiena laiks: %.3f ms\n", lastMoveTimeMs));

            JOptionPane.showMessageDialog(
                    this,
                    "Fails veiksmīgi saglabāts:\n" + file.getAbsolutePath(),
                    "Saglabāts",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Kļūda saglabājot failu:\n" + ex.getMessage(),
                    "Kļūda",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}