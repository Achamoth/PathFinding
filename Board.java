import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.*;

import java.util.ArrayList;

public class Board extends JPanel implements ActionListener {

    //The dimensions of the simulation
    private final int B_WIDTH = 1000;
    private final int B_HEIGHT = 750;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = (B_WIDTH*B_HEIGHT)/(DOT_SIZE*DOT_SIZE);

    //Integer constants used to record the state of each block in the game board (enums, effectively)
    private final int EMPTY = 0;
    private final int WALL = 1;
    private final int GOAL = 2;
    private final int VISITED = 3;
    private final int PATH = 4;
    private final int SOURCE = 5;

    //Integer constants used to determine the program's operational mode
    public static final int WALL_PAINT_MODE = 10;
    public static final int ERASE_MODE = 11;
    public static final int SOURCE_PLACE_MODE = 12;
    public static final int GOAL_PLACE_MODE = 13;

    //Records the state of each block in the game board
    private int[][] gameBoard = new int[B_HEIGHT/DOT_SIZE][B_WIDTH/DOT_SIZE];

    //Keep track of where the source and goal nodes are
    private int sourceX;
    private int sourceY;
    private int goalX;
    private int goalY;

    public Board() {
        //Set right click listener, black background, focusable property and preferred size
        this.addMouseListener(new RightClickListener());
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        //Initialize the game board to all empty blocks
        for(int i=0; i<gameBoard.length; i++) {
            for(int j=0; j<gameBoard[i].length; j++) {
                gameBoard[i][j] = EMPTY;
            }
        }

        //Place source and goal
        this.sourceX = 4;
        this.sourceY = 3;
        this.goalX = (B_WIDTH/DOT_SIZE)-3;
        this.goalY = (B_HEIGHT/DOT_SIZE)-4;
        gameBoard[this.sourceY][this.sourceX] = SOURCE;
        gameBoard[this.goalY][this.goalX] = GOAL;

        //Set mouse motion listener (for painting walls)
        this.addMouseMotionListener(new WallPaintListener());
    }

    //Returns the source location as
    public int[] getSource() {
        int[] sourceLoc = new int[2];
        sourceLoc[0] = this.sourceX;
        sourceLoc[1] = this.sourceY;
        return sourceLoc;
    }

    //Returns the source location as
    public int[] getGoal() {
        int[] goalLoc = new int[2];
        goalLoc[0] = this.goalX;
        goalLoc[1] = this.goalY;
        return goalLoc;
    }

    public int[][] getGameBoard() {
        return this.gameBoard;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.doDrawing(g);
    }

    //Draw the board by looking at the states in the gameBoard object
    private void doDrawing(Graphics g) {
        //Loop through gameBoard object
        for(int i=0; i<gameBoard.length; i++) {
            for(int j=0; j<gameBoard[i].length; j++) {
                //Look at state
                int state = gameBoard[i][j];
                //Determine which color to paint square depending on state
                switch(state) {
                    //Empty sqaure
                    case EMPTY:
                        g.setColor(Color.black);
                        break;
                    //Square is a wall
                    case WALL:
                        g.setColor(Color.blue);
                        break;
                    //Square is a goal
                    case GOAL:
                        g.setColor(Color.orange);
                        break;
                    //Square is a visited node
                    case VISITED:
                        g.setColor(Color.lightGray);
                        break;
                    //Square is a node along the final path
                    case PATH:
                        g.setColor(Color.red);
                        break;
                    //Square is the source node
                    case SOURCE:
                        g.setColor(Color.green);
                        break;
                }
                //Paint square
                g.fillRect(j*DOT_SIZE,i*DOT_SIZE,DOT_SIZE,DOT_SIZE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Repaint display
        this.repaint();
    }

    //Given an x, y coordinate pair, check to see if it's valid
    public boolean valid(int x, int y) {
        return ( (x/DOT_SIZE>=0) && (x/DOT_SIZE<(B_WIDTH/DOT_SIZE)) && (y/DOT_SIZE>=0) && (y/DOT_SIZE<(B_HEIGHT/DOT_SIZE)) );
    }

    //Given an x,y coordinate pair, check to see if it's an empty square on the board
    public boolean isEmptySquareOrGoal(int x, int y) {
        if(x<gameBoard[0].length && x>=0 && y<gameBoard.length && y>=0) {
            if(gameBoard[y][x] == EMPTY || gameBoard[y][x] == GOAL) {
                return true;
            }
        }
        return false;
    }

    //Given an x-coordinate and y-coordinate for a mouse event, set a wall there
    public void setWall(int x, int y) {
        if(this.valid(x, y)) {
            this.gameBoard[y/DOT_SIZE][x/DOT_SIZE] = WALL;
        }
    }

    //Given an x-coordinate and y-coordinate for a mouse event, make that square empty
    public void removeWall(int x, int y) {
        if(this.valid(x, y)) {
            if(this.gameBoard[y/DOT_SIZE][x/DOT_SIZE] == WALL) {
                this.gameBoard[y/DOT_SIZE][x/DOT_SIZE] = EMPTY;
            }
        }
    }

    //Given an x,y coordinate pair, place the source there
    public void placeSource(int x, int y) {
        if(this.valid(x, y)) {
            this.gameBoard[this.sourceY][this.sourceX] = EMPTY;
            this.sourceY = y/DOT_SIZE;
            this.sourceX = x/DOT_SIZE;
            this.gameBoard[this.sourceY][this.sourceX] = SOURCE;
        }
    }

    //Given an x,y coordinate pair, place the goal there
    public void placeGoal(int x, int y) {
        if(this.valid(x, y)) {
            this.gameBoard[this.goalY][this.goalX] = EMPTY;
            this.goalY = y/DOT_SIZE;
            this.goalX = x/DOT_SIZE;
            this.gameBoard[this.goalY][this.goalX] = GOAL;
        }
    }

    //Given an x,y coordinate pair, make that square visited
    public void visitSquare(int x, int y) {
        this.gameBoard[y][x] = VISITED;
    }
}

class RightClickMenu extends JPopupMenu {

    //Declare menu items
    private JMenuItem startPathFinding;
    private JMenuItem startWallPainting;
    private JMenuItem startWallErasing;
    private JMenuItem moveSource;
    private JMenuItem moveGoal;

    //Create and add all menu items
    public RightClickMenu() {

        //First menu item allows user to start pathfinding
        this.startPathFinding = new JMenuItem("Find Path");
        this.startPathFinding.addActionListener(new MenuItemListener());
        this.add(startPathFinding);

        //Second menu item allows user to paint walls
        this.startWallPainting = new JMenuItem("Paint Walls");
        this.startWallPainting.addActionListener(new MenuItemListener());
        this.add(startWallPainting);

        //Third menu item allows user to erase walls
        this.startWallErasing = new JMenuItem("Erase Walls");
        this.startWallErasing.addActionListener(new MenuItemListener());
        this.add(startWallErasing);

        //Fourth menu item allows user to move the source
        this.moveSource = new JMenuItem("Move Source");
        this.moveSource.addActionListener(new MenuItemListener());
        this.add(moveSource);

        //Fifth menu item allows user to move the goal
        this.moveGoal = new JMenuItem("Move Goal");
        this.moveGoal.addActionListener(new MenuItemListener());
        this.add(moveGoal);
    }
}

class MenuItemListener extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
        //Find out which menu item generated the event (i.e. which menu item was clicked)
        JMenuItem source = (JMenuItem) e.getSource();
        String text = source.getText();

        //Process the event accordingly
        if(text.equals("Find Path")) {
            Runner.findPath();
        }
        //User wants to enter wall paint mode
        else if(text.equals("Paint Walls")) {
            Runner.setPaintMode();
        }
        //User wants to enter eraser mode (to remove walls)
        else if(text.equals("Erase Walls")) {
            Runner.setEraseMode();
        }
        //User wants to move the source
        else if(text.equals("Move Source")) {
            Runner.setSourcePlaceMode();
        }
        //User wants to move the goal
        else if(text.equals("Move Goal")) {
            Runner.setGoalPlaceMode();
        }
    }
}

class RightClickListener extends MouseAdapter {
    //Override mouse pressed event handler
    public void mousePressed(MouseEvent e) {
        //If the button pressed is the popup menu trigger, call the pop() method
        if(e.isPopupTrigger()) {
            this.pop(e);
        }
    }

    //Override mouse released event handler
    public void mouseReleased(MouseEvent e) {
        //If the button pressed is the popup menu trigger, call the pop() method
        if(e.isPopupTrigger()) {
            this.pop(e);
        }
    }

    //Create and display the right click menu on the component in which the event occurred
    public void pop(MouseEvent e) {
        RightClickMenu menu = new RightClickMenu();
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}

class WallPaintListener extends MouseMotionAdapter {
    //Override mouse dragged event handler
    public void mouseDragged(MouseEvent e) {
        //User is trying to paint walls
        if(Runner.getUserMode() == Board.WALL_PAINT_MODE) {
            Runner.setWall(e.getX(), e.getY());
            Runner.refresh();
        }
        //User is trying to erase walls
        else if(Runner.getUserMode() == Board.ERASE_MODE) {
            Runner.removeWall(e.getX(), e.getY());
            Runner.refresh();
        }
        //User is trying to place source
        else if(Runner.getUserMode() == Board.SOURCE_PLACE_MODE) {
            Runner.placeSource(e.getX(), e.getY());
            Runner.refresh();
        }
        //User is trying to place goal
        else if(Runner.getUserMode() == Board.GOAL_PLACE_MODE) {
            Runner.placeGoal(e.getX(), e.getY());
            Runner.refresh();
        }
    }
}
