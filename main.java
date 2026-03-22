import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Mi extends JFrame {

    int currentNum, playerPts, compPts;
    boolean playerTurn, alphaBeta;

    JTextField numField;
    JLabel errLabel, lblNum, lblPts, lblCompPts, lblTurn, lblWinner, lblSub;
    JToggleButton btnAB, btnComp;
    JButton b2, b3, b4;
    JTextArea log;

    CardLayout cl = new CardLayout();
    JPanel root   = new JPanel(cl);

    public Mi() {
        setTitle("Reizināšanas spēle");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        root.add(setupPanel(), "setup");
        root.add(gamePanel(),  "game");
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
        ag.add(btnMM); ag.add(btnAB);
        p.add(row(btnMM, btnAB));

        p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Kurš sāk:"));
        p.add(Box.createVerticalStrut(4));
        JToggleButton btnHuman = new JToggleButton("Cilvēks", true);
        btnComp = new JToggleButton("Dators", false);
        ButtonGroup fg = new ButtonGroup();
        fg.add(btnHuman); fg.add(btnComp);
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

        lblPts     = new JLabel("0");
        lblCompPts = new JLabel("0");
        lblPts.setFont(lblPts.getFont().deriveFont(24f));
        lblCompPts.setFont(lblCompPts.getFont().deriveFont(24f));
        lblPts.setAlignmentX(CENTER_ALIGNMENT);
        lblCompPts.setAlignmentX(CENTER_ALIGNMENT);

        JPanel pb = col("Cilvēks", lblPts);
        JPanel cb = col("Dators",  lblCompPts);
        JPanel scoreRow = new JPanel(new GridLayout(1, 2, 10, 0));
        scoreRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        scoreRow.add(pb); scoreRow.add(cb);
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

        p.add(Box.createVerticalStrut(10));
        b2 = new JButton("×2"); b3 = new JButton("×3"); b4 = new JButton("×4");
        b2.addActionListener(e -> playerMove(2));
        b3.addActionListener(e -> playerMove(3));
        b4.addActionListener(e -> playerMove(4));
        JPanel br = new JPanel(new GridLayout(1, 3, 8, 0));
        br.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        br.add(b2); br.add(b3); br.add(b4);
        p.add(br);

        p.add(Box.createVerticalStrut(10));
        log = new JTextArea(5, 0);
        log.setEditable(false);
        log.setBackground(new Color(245, 245, 243));
        p.add(new JScrollPane(log));

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
        p.add(lblWinner);
        p.add(Box.createVerticalStrut(8));
        p.add(lblSub);
        p.add(Box.createVerticalStrut(24));

        JButton again = new JButton("Spēlēt vēlreiz");
        again.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        again.setAlignmentX(CENTER_ALIGNMENT);
        again.addActionListener(e -> { numField.setText(""); errLabel.setText(" "); cl.show(root, "setup"); });
        p.add(again);
        return p;
    }

    // palīgmetodes paneļiem
    JPanel row(JComponent a, JComponent b) {
        JPanel r = new JPanel(new GridLayout(1, 2, 6, 0));
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        r.add(a); r.add(b); return r;
    }

    JPanel col(String name, JLabel num) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(240, 240, 238));
        p.setBorder(new EmptyBorder(8, 0, 8, 0));
        JLabel l = new JLabel(name);
        l.setAlignmentX(CENTER_ALIGNMENT);
        p.add(l); p.add(num);
        return p;
    }

    // ── Loģika ─────────────────────────────────────────────────────────────────

    void startGame() {
        int n;
        try { n = Integer.parseInt(numField.getText().trim()); }
        catch (NumberFormatException ex) { n = -1; }
        if (n < 8 || n > 18) { errLabel.setText("Ievadi skaitli no 8 līdz 18."); return; }

        errLabel.setText(" ");
        currentNum = n; playerPts = 0; compPts = 0;
        alphaBeta  = btnAB.isSelected();
        playerTurn = !btnComp.isSelected();
        log.setText("");
        refresh();
        cl.show(root, "game");
        if (!playerTurn) { setBtns(false); scheduleComp(); }
    }

    void playerMove(int m) {
        if (!playerTurn) return;
        log.append("Cilvēks: " + currentNum + " x " + m + " = " + currentNum * m + "\n");
        doMove(m, true);
        playerTurn = false;
        refresh();
        if (checkEnd()) return;
        setBtns(false);
        scheduleComp();
    }

    void compMove() {
        int m = bestMove();
        log.append("Dators:  " + currentNum + " x " + m + " = " + currentNum * m + "\n");
        doMove(m, false);
        playerTurn = true;
        refresh();
        if (checkEnd()) return;
        setBtns(true);
    }

    void doMove(int m, boolean byPlayer) {
        int r = currentNum * m;
        if (r % 2 == 0) { if (byPlayer) compPts   = Math.max(0, compPts - 1);
        else           playerPts  = Math.max(0, playerPts - 1); }
        else             { if (byPlayer) playerPts++;
        else           compPts++; }
        currentNum = r;
    }

    int bestMove() {
        int best = Integer.MIN_VALUE, bm = 2;
        for (int m : new int[]{2, 3, 4}) {
            int v = minimax(currentNum * m, playerPts, compPts, false, 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (v > best) { best = v; bm = m; }
        }
        return bm;
    }

    int minimax(int num, int pp, int cp, boolean isMax, int depth, int a, int b) {
        if (num >= 1200 || depth == 0) return cp - pp;
        int best = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int m : new int[]{2, 3, 4}) {
            int r = num * m, np = pp, nc = cp;
            if (r % 2 == 0) { if (!isMax) nc = Math.max(0, nc-1); else np = Math.max(0, np-1); }
            else             { if (!isMax) np++; else nc++; }
            int v = minimax(r, np, nc, !isMax, depth - 1, a, b);
            if (isMax) { best = Math.max(best, v); if (alphaBeta) { a = Math.max(a, v); if (b <= a) break; } }
            else       { best = Math.min(best, v); if (alphaBeta) { b = Math.min(b, v); if (b <= a) break; } }
        }
        return best;
    }

    boolean checkEnd() {
        if (currentNum < 1200) return false;
        String w = playerPts > compPts ? "Tu uzvarēji!" : compPts > playerPts ? "Dators uzvarēja." : "Neizšķirts.";
        lblWinner.setText(w);
        lblSub.setText("Cilvēks " + playerPts + " pts  ·  Dators " + compPts + "  pts  ·  Skaitlis " + currentNum);
        cl.show(root, "result");
        return true;
    }

    void scheduleComp() {
        Timer t = new Timer(700, e -> { compMove(); ((Timer) e.getSource()).stop(); });
        t.setRepeats(false); t.start();
    }

    void refresh() {
        lblNum.setText(String.valueOf(currentNum));
        lblPts.setText(String.valueOf(playerPts));
        lblCompPts.setText(String.valueOf(compPts));
        lblTurn.setText(playerTurn ? "Tavs gājiens" : "Dators domā...");
    }

    void setBtns(boolean en) { b2.setEnabled(en); b3.setEnabled(en); b4.setEnabled(en); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mi::new);
    }
}
