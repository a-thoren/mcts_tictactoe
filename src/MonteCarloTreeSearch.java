import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MonteCarloTreeSearch {
    int WIN_SCORE;
    int LOSE_SCORE;
    int evaluations;
    int opponent;
    enum Policy {
        RANDOM,
        UCT
    }
    Policy OPPONENT_SELECTION_POLICY = Policy.RANDOM;
    Policy SELECTION_POLICY = Policy.UCT;

    public MonteCarloTreeSearch() {
        this.WIN_SCORE = 10;
        this.LOSE_SCORE = Integer.MIN_VALUE;
        this.evaluations = 25;
    }

    public MonteCarloTreeSearch(int WIN_SCORE, int LOSE_SCORE) {
        this.WIN_SCORE = WIN_SCORE;
        this.LOSE_SCORE = LOSE_SCORE;
        this.evaluations = 25;
    }

    public MonteCarloTreeSearch(int WIN_SCORE, int LOSE_SCORE, int EVALUATIONS) {
        this.WIN_SCORE = WIN_SCORE;
        this.LOSE_SCORE = LOSE_SCORE;
        this.evaluations = EVALUATIONS;
    }

    /**
     * Finds the next, optimal move
     * @param board
     * @param player
     * @return a board on which the next move has been played
     */
    public Board findNextMove(Board board, int player) {
        opponent = player == 1 ? 2 : 1;
        Tree tree = new Tree();
        Node root = tree.root;
        root.state.board = board;
        root.state.player = opponent;

        for (int i = 0; i < this.evaluations; i++) {

            //1. Selection
            Node promisingNode = selection(root);

            //2. Expansion
            int promisingBoardStatus = promisingNode.state.board.isWon();
            if (promisingBoardStatus == Board.ONGOING) {
                expansion(promisingNode);
            }

            //3. Simulation
            Node toExplore = opponentSelection(promisingNode);
            int result = simulateRandomPlayout(toExplore);

            //4. Backpropagation
            backpropagation(toExplore, result);
        }


        Node winner = root.getMostVisitedChild();
        tree.root = winner;
        return winner.state.board;
    }

    /**
     *
     * @param node the node from where to start backpropagating.
     * @param player
     * While there is a parent of the current node we check if the node's player is our player.
     * If so, check if the node has lead to a loss, and if not increase the node's win score by WIN_SCORE. Go to parent and repeat.
     */
    public void backpropagation(Node node, int player) {
        Node temp = node;
        while (temp != null) {
            temp.state.visits++;
            if (temp.state.player == player) {
                temp.state.winScore += temp.state.winScore == LOSE_SCORE ? 0 : WIN_SCORE;
            }
            temp = temp.parent;
        }
    }

    /**
     *
     * @param node
     * First, checks if opponent has won. If this is the case then the state's score is set to negative infinity.
     * Then, plays a random game until it is finished.
     * @return the status of the board after the random game has been played.
     */
    public int simulateRandomPlayout(Node node) {
        Node temp = new Node(node);
        GameState tempState = temp.state;
        int boardStatus = tempState.board.isWon();
        if (boardStatus == opponent) {
            temp.parent.state.winScore = LOSE_SCORE;
            return boardStatus;
        }
        while (boardStatus == Board.ONGOING) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.board.isWon();
        }
        return boardStatus;
    }

    /**
     *
     * @param root
     * The selection policy for our player.
     * @return the child of root with the highest UCT value
     */
    public Node selection(Node root) {
        Node node = root;
        while (node.children.size() != 0) {
            if (this.SELECTION_POLICY == Policy.UCT) {
                //node = findChildWithHighestUCT(node);
                node = node.findChildWithHighestUCT();
            } else if (this.SELECTION_POLICY == Policy.RANDOM) {
                node = node.getRandomChildNode();
            }
        }
        return node;
    }

    /**
     *
     * @param node
     * @return if node has no children return node. Otherwise, return the node according to the opponent's selection policy
     */
    public Node opponentSelection(Node node) {
        if (node.children.size() > 0) {
            if (this.OPPONENT_SELECTION_POLICY == Policy.RANDOM) {
                return node.getRandomChildNode();
            } else if (this.OPPONENT_SELECTION_POLICY == Policy.UCT){
                //return findChildWithHighestUCT(node);
                return node.findChildWithHighestUCT();
            }
        }
        return node;
    }
    /**
     *
     * @param node
     * First, finds all possible states given the current state of node.
     * Then, converts these states into new nodes and adds them to node's list of children
     */
    public void expansion(Node node) {
        List<GameState> possibleStates = node.state.getAllNextStates();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state, node);
            newNode.state.player = (node.state.getOpponent());
            node.children.add(newNode);
        });
    }

    /** Finds the UCT value of a node.
     *
     * @param totalVisits The number of visits to the node's parent.
     * @param nodeWinScore The win score of the node.
     * @param nodeVisits The number of visits to the node.
     * @return if the node in question has not been visited return Integer.MAX_VALUE to guarantee we explore it,
     * otherwise return the upper confidence bound for trees.
     */
    public static double uctValue(int totalVisits, double nodeWinScore, int nodeVisits) {
        return nodeVisits == 0 ? Integer.MAX_VALUE :
                nodeWinScore /  nodeVisits + Math.sqrt(2) * Math.sqrt(Math.log(totalVisits) / nodeVisits);
    }

    /**
     *
     * @param node
     * @return The child of node that has the highest UCT score.
     */
    public static Node findChildWithHighestUCT(Node node) {
        int parentVisit = node.state.visits;
        return Collections.max(node.children, Comparator.comparing(
                child -> uctValue(parentVisit, child.state.winScore, child.state.visits)));
    }
}
