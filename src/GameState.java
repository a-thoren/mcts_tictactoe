import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    Board board;
    int player;
    int visits;
    double winScore;


    public GameState(Board board) {
        this.board = new Board(board);
    }

    public GameState(GameState state) {
        this.board = new Board(state.board);
        this.player = state.player;
        this.visits = state.visits;
        this.winScore = state.winScore;
    }

    public GameState() {
        this.board = new Board();
    }

    public List<GameState> getAllNextStates() {
        List<GameState> possibleStates = new ArrayList<>();
        List<Point> availablePositions = this.board.getEmptyPositions();
        for (Point p : availablePositions) {
            GameState newState = new GameState(this.board);
            newState.player = this.getOpponent();
            newState.board.move(newState.player, p);
            possibleStates.add(newState);
        }
        return possibleStates;
    }

    public void randomPlay() {
        List<Point> availablePositions = this.board.getEmptyPositions();
        Point p = availablePositions.get(MCTS.rand.nextInt(availablePositions.size()));
        this.board.move(this.player, p);
    }

    public void togglePlayer() {
        this.player = getOpponent();
    }

    public int getOpponent() {
        return this.player == 1 ? 2 : 1;
    }

}

