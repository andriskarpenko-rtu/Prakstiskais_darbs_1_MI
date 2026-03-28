public class State {
    int number;
    int playerPts;
    int compPts;
    boolean playerTurn; // true = cilvēks, false = dators

    public State(int number, int playerPts, int compPts, boolean playerTurn) {
        this.number = number;
        this.playerPts = playerPts;
        this.compPts = compPts;
        this.playerTurn = playerTurn;
    }

    public boolean isTerminal() {
        return number >= 1200;
    }
}