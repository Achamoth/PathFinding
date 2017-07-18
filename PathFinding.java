import javax.swing.JFrame;

public class PathFinding extends JFrame {

    //The board object (the main component of this frame)
    private Board board;

    public PathFinding() {
        //Create board object
        board = new Board();

        //Add a new board as a component to this frame
        add(board);

        //Prohibit resizing of the frame and pack
        setResizable(false);
        pack();

        //Set title, relative location and default close operation
        setTitle("PathFinding");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    //Getter method
    public Board getBoard() {
        return this.board;
    }
}
