import java.awt.EventQueue;
import javax.swing.JFrame;
import java.util.ArrayList;

public class Runner {

    private static PathFinding ex;
    private static Board board;

    //This indicates what mode the program is in (i.e. wall paint mode, erase mode etc.)
    private static int userMode;

    public static void main(String[] args) {

        //Start off in wall paint mode by default
        userMode = Board.WALL_PAINT_MODE;

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ex = new PathFinding();
                board = ex.getBoard();
                ex.setVisible(true);
            }
        });
    }

    //Paint a wall at the specified position
    public static void setWall(int x, int y) {
        board.setWall(x, y);
    }

    //Remove a wall at the specified position
    public static void removeWall(int x, int y) {
        board.removeWall(x, y);
    }

    //Place the source at the specified position
    public static void placeSource(int x, int y) {
        board.placeSource(x, y);
    }

    //Place the goal at the specified position
    public static void placeGoal(int x, int y) {
        board.placeGoal(x, y);
    }

    //Print a message to terminal. Used for debugging
    public static void print(String message) {
        System.out.println(message);
    }

    //Repaint the board
    public static void refresh() {
        board.actionPerformed(null);
    }

    //Return the user mode
    public static int getUserMode() {
        return userMode;
    }

    //Set the user mode to paint mode (for painting walls)
    public static void setPaintMode() {
        userMode = Board.WALL_PAINT_MODE;
    }

    //Set the user mode to erase mode (for removing walls)
    public static void setEraseMode() {
        userMode = Board.ERASE_MODE;
    }

    //Set the user mode to source place mode (for placing the source)
    public static void setSourcePlaceMode() {
        userMode = Board.SOURCE_PLACE_MODE;
    }

    //Set the user mode to goal place mode (for placing the goal)
    public static void setGoalPlaceMode() {
        userMode = Board.GOAL_PLACE_MODE;
    }

    //Use pathfinding algorithm to find path from source to goal, and paint display in real-time to visualize algorithm
    public static void findPath() {
        Graph.AStar(board);
    }
}
