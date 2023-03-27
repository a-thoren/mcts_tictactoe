import java.awt.*;
import java.util.*;
import java.util.List;

public class Board {
    int[][] values;
    static final int DRAW = 0;
    static final int ONGOING = -1;
    final int BOARD_SIZE;

    public Board() {
        this(3);
    }

    public Board(int size) {
        this.BOARD_SIZE = size;
        this.values = new int[this.BOARD_SIZE][this.BOARD_SIZE];
        reset();
    }

    public Board(Board board) {
        this.BOARD_SIZE = board.BOARD_SIZE;
        this.values = new int[this.BOARD_SIZE][this.BOARD_SIZE];
        for (int i = 0; i < values.length; i++) {
            System.arraycopy(board.values[i], 0, values[i], 0, values.length);
        }
    }

    public void reset() {
        for (int[] row: values) {
            Arrays.fill(row, -1);
        }
    }

    public void move(int player, Point position) {
        move(player, position.x, position.y);
    }

    public void move(int player, int x, int y) {
        this.values[x][y] = player;
    }

    /**
     *
     * @return -1 if the game is ongoing, 0 if it's a draw and 1 or 2 depending on the player who has won.
     */
    public int isWon() {
        boolean isFull = true;
        //Check if anyone has won for each row
        for (int i = 0; i < this.values.length; i++) {
            if (Arrays.stream(values[i]).distinct().count() == 1 && values[i][0] != -1) {
                //System.out.println("Player " + values[i][0] + " won in row " + i + '\n' + this);

                return values[i][0];
            }
        }

        //Check columns
        for (int j = 0; j < this.values.length; j++) {
            if (values[0][j] == -1)
                continue;
            int player = values[0][j];
            boolean matches = true;
            for (int i = 1; i < this.values.length; i++) {
                if (values[i][j] != player) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                //System.out.println("Player " + player + " won in column " + j + '\n' + this);
                return player;
            }
        }

        //Check diagonal
        boolean matches;
        int num;
        if (values[0][0] != -1) {
            num = values[0][0];
            matches = true;

            for (int i = 1; i < this.values.length; i++) {
                if (values[i][i] != num) {
                    matches = false;
                    break;
                }
            }
            if (matches)
                //System.out.println("Player " + num + " won in diagonal" + '\n' + this);
                return num;
        }

        if (values[0][this.BOARD_SIZE - 1] != -1) {
            num = values[0][this.BOARD_SIZE - 1];
            matches = true;

            for (int i = 1; i < this.values.length; i++) {
                if (values[i][this.BOARD_SIZE - 1 - i] != num) {
                    matches = false;
                    break;
                }
            }
            if (matches)
                //System.out.println("Player " + num + " won in diagonal" + '\n' + this);
                return num;
        }

        for (int[] value : this.values) {
            for (int j = 0; j < this.values.length; j++) {
                if (value[j] == -1) {
                    isFull = false;
                    break;
                }
            }
        }
        if (isFull)
            return DRAW;
        return ONGOING;
    }


    public java.util.List<Point> getEmptyPositions() {
        List<Point> empty = new ArrayList<>();
        for (int i = 0; i < this.values.length; i++) {
            for (int j = 0; j < this.values.length; j++) {
                if (this.values[i][j] == -1) {
                    empty.add(new Point(i, j));
                }
            }
        }
        return empty;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row: values) {
            sb.append(Arrays.toString(row));
            sb.append('\n');
        }
        return sb.toString();
    }
}
