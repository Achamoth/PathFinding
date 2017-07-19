import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.lang.Double;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Graph {
    public static void AStar(Board board) {
        //Given a board object, finds the shortest path between the source and goal using AStar
        //Colours the visited squares white, and the path red

        //Start by finding the source and goal locations
        int[] goalLoc = board.getGoal();
        Node goal = new Node(goalLoc[0], goalLoc[1], 0, 10000);
        int[] sourceLoc = board.getSource();
        Node source = new Node(sourceLoc[0], sourceLoc[1],euclidianDist(sourceLoc[0], sourceLoc[1], goal) ,0);

        // //Now get all nodes from graph
        // Set<Node> nodes = getAllNodes(board, goal);

        //Set up hashset of visited nodes
        Set<String> visited = new HashSet<String>();
        visited.add(source.toString());

        //Set up hashmap of parent nodes and distances
        Map<Node, Node> parent = new HashMap<Node, Node>();
        parent.put(source, null);

        //Set up priority queue of nodes
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        pq.add(source);
        Set<String> inpq = new HashSet<String>();
        inpq.add(source.toString());

        //Conduct search
        while(!visited.contains(goal.toString()) && !pq.isEmpty()) {
            //Poll best node off priority queue
            Node curNode = pq.poll();
            inpq.remove(curNode.toString());
            ArrayList<Node> neighbours = getNeighbours(board, curNode, goal, source);
            //Check all of the node's neighbours
            for(Node n : neighbours) {
                //Add n to pq if it's unvisited and not already in pq
                if(!visited.contains(n.toString()) && !inpq.contains(n.toString())) {
                    pq.add(n);
                    inpq.add(n.toString());
                    visited.add(n.toString());
                    board.visitSquare(n.getX(), n.getY());
                    Runner.refresh();
                    parent.put(n, curNode);
                }
            }
        }

        //Now, calculate final path
    }

    // //Given a board object, find all the nodes and return them
    // private static Set<Node> getAllNodes(Board board, Node goal) {
    //     //Create empty set of nodes
    //     Set<Node> nodes = new HashSet<Node>();
    //     //Retrieve game board
    //     int[][] gameBoard = board.getGameBoard();
    //     //Loop over it
    //     for(int i=0; i<gameBoard.length; i++) {
    //         for(int j=0; j<gameBoard[0].length; j++) {
    //             //If the square is empty, it's a node
    //             if(gameBoard[i][j] == 0) {
    //                 Node curNode = new Node(j, i, 10000, euclidianDist(j, i, goal));
    //                 nodes.add(curNode);
    //             }
    //         }
    //     }
    //     return nodes;
    // }

    //Given a goal node, and a source node's coordinates, find the euclidian distance between them
    private static double euclidianDist(int x, int y, Node goal) {
        return Math.hypot(Math.abs(x - goal.getX()), Math.abs(y - goal.getY()));
    }

    //Find all nodes adjacent to a specified node
    private static ArrayList<Node> getNeighbours(Board board, Node node, Node goal, Node source) {
        //Construct empty array list
        ArrayList<Node> neighbours = new ArrayList<Node>();

        //Check that all adjacent positions are valid positions on the board, and not walls
        if(board.isEmptySquareOrGoal(node.getX()+1, node.getY())) {
            neighbours.add(new Node(node.getX()+1, node.getY(), euclidianDist(node.getX()+1, node.getY(),goal), euclidianDist(node.getX()+1, node.getY(), source)));
        }
        if(board.isEmptySquareOrGoal(node.getX()-1, node.getY())) {
            neighbours.add(new Node(node.getX()-1, node.getY(), euclidianDist(node.getX()-1, node.getY(),goal), euclidianDist(node.getX()-1, node.getY(), source)));
        }
        if(board.isEmptySquareOrGoal(node.getX(), node.getY()+1)) {
            neighbours.add(new Node(node.getX(), node.getY()+1, euclidianDist(node.getX(), node.getY()+1,goal), euclidianDist(node.getX(), node.getY()+1, source)));
        }
        if(board.isEmptySquareOrGoal(node.getX(), node.getY()-1)) {
            neighbours.add(new Node(node.getX(), node.getY()-1, euclidianDist(node.getX(), node.getY()-1,goal), euclidianDist(node.getX(), node.getY()-1, source)));
        }

        return neighbours;
    }
}

class Node implements Comparable<Node> {

    private int x;
    private int y;
    private double heuristic;
    private double distance;

    public Node(int x, int y, double heuristic, double distance) {
        this.x = x;
        this.y = y;
        this.heuristic = heuristic;
        this.distance = distance;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getHeuristic() {
        return this.heuristic;
    }

    public void setDistance(int newDist) {
        this.distance = newDist;
    }

    @Override
    public int hashCode() {
        return (this.x * this.y) + this.x;
    }

    @Override
    public int compareTo(Node n) {
        if((this.heuristic + this.distance) < (n.getHeuristic() + n.getDistance())) {
            //This object is less than n
            return -1;
        }
        else if((this.heuristic + this.distance) > (n.getHeuristic() + n.getDistance())) {
            //This object is greater than n
            return 1;
        }
        else {
            //This object is equal to n
            return 0;
        }
    }

    @Override
    public String toString() {
        return new String(this.x + "," + this.y);
    }
}
