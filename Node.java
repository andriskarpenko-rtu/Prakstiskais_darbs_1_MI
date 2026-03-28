import java.util.ArrayList;
import java.util.List;

public class Node {
    State state;
    List<Node> children;

    public Node(State state) {
        this.state = state;
        this.children = new ArrayList<>();
    }
}