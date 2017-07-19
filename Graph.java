import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.lang.Double;
import java.util.PriorityQueue;
import java.util.ArrayList;

public class Graph {
    public static void AStar(Board board) {
        //Given a board object, finds the shortest path between the source and goal using AStar
        //Colours the visited squares white, and the path red

        //Start by finding the source and goal locations
        int[] goalLoc = board.getGoal();
        Node goal = new Node(goalLoc[0], goalLoc[1], 0, 10000000);
        int[] sourceLoc = board.getSource();
        Node source = new Node(sourceLoc[0], sourceLoc[1],findHeuristic(sourceLoc[0], sourceLoc[1], goal) ,0);

        //Set up hashset of visited nodes
        Set<Node> visited = new HashSet<Node>();
        visited.add(source);
        board.visitSquare(source.getX(), source.getY());
        Runner.refresh();

        //Set up hashmap of parent nodes and distances
        Map<Node, Node> parent = new HashMap<Node, Node>();
        parent.put(source, null);

        //Set up priority queue of nodes
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        pq.add(source);

        //Conduct search
        while(!visited.contains(goal) && !pq.isEmpty()) {
            //Poll best node off priority queue
            Node curNode = pq.poll();
            ArrayList<Node> neighbours = getNeighbours(board, curNode, goal);
            //Check all of the node's neighbours
            for(Node n : neighbours) {
                //Add n to pq if it's unvisited and not already in pq
                if(!visited.contains(n) && !pq.contains(n)) {
                    pq.add(n);
                    visited.add(n);
                    board.visitSquare(curNode.getX(), curNode.getY());
                    System.out.println("" + curNode.getX() + " " + curNode.getY());
                    Runner.refresh();
                    parent.put(n, curNode);
                }
            }
        }

        //Now, calculate final path

    }

    //Given a node, find all adjacent nodes and return them in an array list
    private static ArrayList<Node> getNeighbours(Board board, Node node, Node goal) {
        //Construct empty array list
        ArrayList<Node> neighbours = new ArrayList<Node>();

        //Check that all adjacent positions are valid positions on the board, and not walls
        if(board.valid(node.getX()+1, node.getY()) && board.emptySquare(node.getX()+1, node.getY())) {
            neighbours.add(new Node(node.getX()+1, node.getY(), findHeuristic(node.getX()+1, node.getY(),goal), node.getDistance()+1));
        }
        if(board.valid(node.getX()-1, node.getY()) && board.emptySquare(node.getX()-1, node.getY())) {
            neighbours.add(new Node(node.getX()-1, node.getY(), findHeuristic(node.getX()-1, node.getY(),goal), node.getDistance()+1));
        }
        if(board.valid(node.getX(), node.getY()+1) && board.emptySquare(node.getX(), node.getY()+1)) {
            neighbours.add(new Node(node.getX(), node.getY()+1, findHeuristic(node.getX(), node.getY()+1,goal), node.getDistance()+1));
        }
        if(board.valid(node.getX(), node.getY()-1) && board.emptySquare(node.getX(), node.getY()-1)) {
            neighbours.add(new Node(node.getX(), node.getY()-1, findHeuristic(node.getX(), node.getY()-1,goal), node.getDistance()+1));
        }

        return neighbours;
    }

    //Given a source and goal node, find the euclidian distance between them (as a heuristic)
    private static double findHeuristic(Node source, Node goal) {
        return Math.hypot(Math.abs(source.getX() - goal.getX()), Math.abs(source.getY() - goal.getY()));
    }

    //Given a goal node, and a source node's coordinates, find the euclidian distance between them
    private static double findHeuristic(int x, int y, Node goal) {
        return Math.hypot(Math.abs(x - goal.getX()), Math.abs(y - goal.getY()));
    }
}

class Node implements Comparable<Node> {

    private int x;
    private int y;
    private double heuristic;
    private int distance;

    public Node(int x, int y, double heuristic, int distance) {
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

    public int getDistance() {
        return this.distance;
    }

    public double getHeuristic() {
        return this.heuristic;
    }

    @Override
    public int hashCode() {
        return (this.x * this.y) + this.x;
    }

    @Override
    public int compareTo(Node n) {
        if((this.heuristic + (double)this.distance) < (n.getHeuristic() + (double)n.getDistance())) {
            //This object is less than n
            return -1;
        }
        else if((this.heuristic + (double)this.distance) > (n.getHeuristic() + (double)n.getDistance())) {
            //This object is greater than n
            return 1;
        }
        else {
            //This object is equal to n
            return 0;
        }
    }
}
