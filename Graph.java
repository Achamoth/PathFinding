import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.lang.Double;
import java.lang.Integer;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Graph {
    //Find a path from source to goal using A*, and also record visited nodes (in order) in visitedNodes
    public static ArrayList<int[]> AStar(Board board, ArrayList<int[]> visitedNodes) {
        //Given a board object, finds the shortest path between the source and goal using AStar
        //Colours the visited squares white, and the path red

        //Start by finding the source and goal locations
        int[] goalLoc = board.getGoal();
        Node goal = new Node(goalLoc[0], goalLoc[1], 0, 10000);
        int[] sourceLoc = board.getSource();
        Node source = new Node(sourceLoc[0], sourceLoc[1],euclidianDist(sourceLoc[0], sourceLoc[1], goal) ,0);

        //Set up hashset of visited nodes
        Set<String> visited = new HashSet<String>();
        visited.add(source.toString());

        //Set up hashmap of parent nodes and distances
        Map<String, String> parent = new HashMap<String, String>();
        parent.put(source.toString(), null);

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
                    int[] nodeInt = new int[2];
                    nodeInt[0] = n.getX();
                    nodeInt[1] = n.getY();
                    visitedNodes.add(nodeInt);
                    parent.put(n.toString(), curNode.toString());
                }
            }
        }

        //Now, calculate final path
        ArrayList<String> pathInverted = new ArrayList<String>();
        String curNode = parent.get(goal.toString());
        while(curNode != null) {
            pathInverted.add(curNode);
            curNode = parent.get(curNode);
        }
        ArrayList<int[]> path = new ArrayList<int[]>();
        for(int i=pathInverted.size()-1; i>=0; i--) {
            path.add(stringToCoordinates(pathInverted.get(i)));
        }
        
        return path;
    }

    //Given coordinates as a string (x,y format), output the coordinates as an array of 2 ints
    private static int[] stringToCoordinates(String coordinates) {
        int[] output = new int[2];
        String tokens[] = coordinates.split(",");
        output[0] = Integer.parseInt(tokens[0]);
        output[1] = Integer.parseInt(tokens[1]);
        return output;
    }

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
