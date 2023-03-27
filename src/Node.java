import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Node {
    GameState state;
    Node parent;
    List<Node> children;

    public Node(GameState state, Node parent) {
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public Node(Node node) {
        this.children = new ArrayList<>();
        for (Node child: node.children) {
            this.children.add(new Node(child));
        }
        this.parent = node.parent == null ? null : node.parent;
        this.state = new GameState(node.state);
    }

    public Node() {
        this.children = new ArrayList<>();
        this.state = new GameState();
    }

    public Node getRandomChildNode() {
        if (children.size() > 0) {
            return children.get(MCTS.rand.nextInt(children.size()));
        }
        return null;
    }

    public Node getMostVisitedChild() {
        List<Integer> childScores = children.stream().map(child -> child.state.visits).collect(Collectors.toList());
        int maxScore = Collections.max(childScores);
        return children.get(childScores.indexOf(maxScore));
    }

    /** Finds the UCT value of a node.
     * @return if the node in question has not been visited return Integer.MAX_VALUE to guarantee we explore it,
     * otherwise return the upper confidence bound for trees.
     */
    public double uctValue() {
        return this.state.visits == 0 ? Integer.MAX_VALUE :
                this.state.winScore /  this.state.visits + Math.sqrt(2) * Math.sqrt(Math.log(this.parent.state.visits) / this.state.visits);
    }

    /**
     *
     * @return The child of this node that has the highest UCT score.
     */
    public Node findChildWithHighestUCT() {
        return Collections.max(this.children, Comparator.comparing(
                Node::uctValue));
    }
}