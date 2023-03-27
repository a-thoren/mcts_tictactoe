import java.awt.*;
import java.util.*;

import static org.junit.Assert.*;

public class MCTS {
    static Random rand = new Random();
    static MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(1, Integer.MIN_VALUE, 50);

    public static void main(String[] args) {

        //Some generic tests for the tic tac toe
        //ticTacToeTests();

        //Verbose play of a 3x3 and 5x5 board
        aiPlay(3, true);
        //aiPlay(5, true);

        // Human controlled game on 3x3 board
        // humanVsAI(3);

        //Compares different number of evaluations of the MCTS algorithm for 100 games
        //manyAIGamesWithDifferentEvaluations(100);
        //The same as above but on a 5x5 board
        //manyAIGamesWithDifferentEvaluations(100, 5);
        //Compares the effect of the opponent's policy
        //compareOpponentPolicy();
        //Compares all policy combinations
        //compareDifferentPolicies();


    }

    public static void compareDifferentPolicies() {
        mcts.SELECTION_POLICY = MonteCarloTreeSearch.Policy.UCT;
        mcts.OPPONENT_SELECTION_POLICY = MonteCarloTreeSearch.Policy.RANDOM;
        manyAIGamesWithDifferentEvaluations(100);

        mcts.SELECTION_POLICY = MonteCarloTreeSearch.Policy.RANDOM;
        manyAIGamesWithDifferentEvaluations(100);

        mcts.SELECTION_POLICY = MonteCarloTreeSearch.Policy.UCT;
        mcts.OPPONENT_SELECTION_POLICY = MonteCarloTreeSearch.Policy.UCT;
        manyAIGamesWithDifferentEvaluations(100);

        mcts.SELECTION_POLICY = MonteCarloTreeSearch.Policy.RANDOM;
        manyAIGamesWithDifferentEvaluations(100);
    }

    public static void compareOpponentPolicy() {
        mcts.SELECTION_POLICY = MonteCarloTreeSearch.Policy.UCT;
        mcts.OPPONENT_SELECTION_POLICY = MonteCarloTreeSearch.Policy.RANDOM;
        mcts.evaluations = 50;
        manyAIGames(10000);
        mcts.OPPONENT_SELECTION_POLICY = MonteCarloTreeSearch.Policy.UCT;
        manyAIGames(10000);
    }

    public static void compareDifferentWinScore() {
        mcts.SELECTION_POLICY = MonteCarloTreeSearch.Policy.UCT;
        mcts.OPPONENT_SELECTION_POLICY = MonteCarloTreeSearch.Policy.RANDOM;
        mcts.WIN_SCORE = 1;
        manyAIGamesWithDifferentEvaluations(100);

        mcts.WIN_SCORE = 10;
        manyAIGamesWithDifferentEvaluations(100);

        mcts.WIN_SCORE = 100;
        manyAIGamesWithDifferentEvaluations(100);
    }

    public static void manyAIGamesWithDifferentEvaluations(int games) {
        System.out.printf("Player selection policy: %s, opponent selection policy %s%n", mcts.SELECTION_POLICY, mcts.OPPONENT_SELECTION_POLICY);
        mcts.evaluations = 50;
        manyAIGames(games);
        mcts.evaluations = 250;
        manyAIGames(games);
        mcts.evaluations = 500;
        manyAIGames(games);
        mcts.evaluations = 1000;
        manyAIGames(games);
    }

    public static void manyAIGamesWithDifferentEvaluations(int games, int boardSize) {
        System.out.printf("Player selection policy: %s, opponent selection policy %s%n", mcts.SELECTION_POLICY, mcts.OPPONENT_SELECTION_POLICY);
        mcts.evaluations = 50;
        manyAIGames(games, boardSize);
        mcts.evaluations = 250;
        manyAIGames(games, boardSize);
        mcts.evaluations = 500;
        manyAIGames(games, boardSize);
        mcts.evaluations = 1000;
        manyAIGames(games, boardSize);
    }


    public static void manyAIGames(int games) {
        manyAIGames(games, 3);
    }

    public static void manyAIGames(int games, int boardSize) {
        Map<Integer, Integer> result = new HashMap<>();
        long time = System.currentTimeMillis();
        for (int i = 0; i < games; i++) {
            result.merge(aiPlay(boardSize,false), 1, Integer::sum);
        }
        long endTime = System.currentTimeMillis();
        System.out.printf("Result of %d AI vs AI games : " + result + " with %d evaluations. Took %.2f seconds.%n", games, mcts.evaluations, (endTime - time) / 1000.0);
    }

    public static int aiPlay(int boardSize, boolean verbose) {
            Board board = new Board(boardSize);
            int player = 1;
            int i = 1;
            while (board.isWon() == Board.ONGOING) {
                board = mcts.findNextMove(board, player);
                if (verbose)
                    System.out.println("Board at move " + i++ + '\n' + board);
                player = player == 1 ? 2 : 1;
            }
            if (verbose)
                System.out.printf("Player %d won%n", board.isWon());
            return board.isWon();
    }

    public static void humanVsAI(int boardSize) {
        Board board = new Board(boardSize);
        Scanner scanner = new Scanner(System.in);
        int player = 1;
        int i = 0;
        System.out.println("Board at move " + i++ + '\n' + board);
        while (board.isWon() == Board.ONGOING) {
            if (player == 1) {
                System.out.println("Enter your next move as row, col. E.g. 0, 1.");
                String move = scanner.nextLine();
                int[] position = Arrays.stream(move.split(",")).mapToInt(Integer::parseInt).toArray();
                board.move(player, position[0], position[1]);
            } else {
                board = mcts.findNextMove(board, player);
            }
            System.out.println("Board at move " + i++ + '\n' + board);
            player = player == 1 ? 2 : 1;
        }
    }

    public static void ticTacToeTests() {
        Board board = new Board();
        System.out.println(board);
        assertEquals(board.isWon(), -1);

        Arrays.fill(board.values[1], 1);
        System.out.println(board);
        assertEquals(board.isWon(), 1);

        Arrays.fill(board.values[2], 2);
        System.out.println(board);
        assertEquals(board.getEmptyPositions(), Arrays.asList(new Point(0,0), new Point(0, 1), new Point(0, 2)));

        board.move(1, new Point(0,1));
        System.out.println(board);
        assertEquals(board.getEmptyPositions(), Arrays.asList(new Point(0,0), new Point(0, 2)));

        board.move(1, 0,0);
        board.move(1, 1,0);
        board.move(1, 2,0);
        board.move(2, 1,2);
        System.out.println(board);
        assertEquals(board.isWon(), 1);

        board.move(1, 2,2);
        board.move(2, 2,0);
        System.out.println(board);
        assertEquals(board.isWon(), 1);

        board.move(-1, 2, 2);
        System.out.println(board);
        assertEquals(board.isWon(), -1);

        board.move(2, 1, 1);
        board.move(2, 0, 2);
        System.out.println(board);
        assertEquals(board.isWon(), 2);
    }




}
