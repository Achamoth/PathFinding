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
    //Public enum that records what pathfinding algorithm to use
    public static final int A_STAR = 0;
    public static final int DIJKSTRA = 1;
    public static int ALGORITHM = A_STAR;

    //Find a path from source to goal using A*, and also record visited nodes (in order) in visitedNodes
    public static ArrayList<int[]> AStar(Board board, ArrayList<int[]> visitedNodes) {
        //Given a board object, finds the shortest path between the source and goal using AStar
        //Colours the visited squares white, and the path red

        //Start by finding the source and goal locations
        int[] goalLoc = board.getGoal();
        Node goal = new Node(goalLoc[0], goalLoc[1], 0, 1000000);
        int[] sourceLoc = board.getSource();
        Node source = new Node(sourceLoc[0], sourceLoc[1],manhattanDist(sourceLoc[0], sourceLoc[1], goal) ,0);

        //Set up hashset of visited nodes
        Set<String> visited = new HashSet<String>();

        //Set up hashmap of parent nodes and distances
        Map<String, String> parent = new HashMap<String, String>();
        parent.put(source.toString(), null);
        Map<String, Integer> distances = new HashMap<String, Integer>();
        distances.put(source.toString(), new Integer(0));

        //Set up priority queue of nodes
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        pq.add(source);
        //Open set of nodes (unvisited, but marked for visiting)
        Set<String> inpq = new HashSet<String>();
        inpq.add(source.toString());

        //Conduct search
        while(!visited.contains(goal.toString()) && !pq.isEmpty()) {
            //Poll best node off priority queue
            Node curNode = pq.poll();
            inpq.remove(curNode.toString());
            visited.add(curNode.toString());
            int[] nodeInt = new int[2];
            nodeInt[0] = curNode.getX();
            nodeInt[1] = curNode.getY();
            visitedNodes.add(nodeInt);

            ArrayList<Node> neighbours = getNeighbours(board, curNode, goal, source);
            //Check all of the node's neighbours
            for(Node n : neighbours) {
                //Add n to pq if it's unvisited, and we've found a shorter path to it than what currently exists
                if(!visited.contains(n.toString())) {
                    if(!inpq.contains(n.toString())) {
                        pq.add(n);
                        inpq.add(n.toString());
                        parent.put(n.toString(), curNode.toString());
                        distances.put(n.toString(), new Integer((int)n.getDistance()));
                    }
                    else if(n.getDistance() < distances.get(n.toString()).intValue()) {
                        pq.remove(n);
                        pq.add(n); //Update the priority queue
                        parent.put(n.toString(), curNode.toString()); //Update the parent
                        distances.put(n.toString(), new Integer(distances.get(curNode.toString()).intValue() +1)); //Update the distance
                    }
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

    //Given a goal node, and a source node's coordinates, find the manhattan distance between them
    private static int manhattanDist(int x, int y, Node goal) {
        return Math.abs(x-goal.getX()) + Math.abs(y-goal.getY());
    }

    //Find all nodes adjacent to a specified node
    private static ArrayList<Node> getNeighbours(Board board, Node node, Node goal, Node source) {
        //Construct empty array list
        ArrayList<Node> neighbours = new ArrayList<Node>();

        //Tie-breaker constant
        double tbreaker = 1 + (double)( (double)1 / ( (double)board.getWidth() * (double)board.getHeight() ) );

        //Check that all adjacent positions are valid positions on the board, and not walls
        if(board.isEmptySquareOrGoal(node.getX()+1, node.getY())) {
            neighbours.add(new Node(node.getX()+1, node.getY(), tbreaker*manhattanDist(node.getX()+1, node.getY(),goal), node.getDistance()+ 1));
        }
        if(board.isEmptySquareOrGoal(node.getX()-1, node.getY())) {
            neighbours.add(new Node(node.getX()-1, node.getY(), tbreaker*manhattanDist(node.getX()-1, node.getY(),goal), node.getDistance()+ 1));
        }
        if(board.isEmptySquareOrGoal(node.getX(), node.getY()+1)) {
            neighbours.add(new Node(node.getX(), node.getY()+1, tbreaker*manhattanDist(node.getX(), node.getY()+1,goal), node.getDistance()+ 1));
        }
        if(board.isEmptySquareOrGoal(node.getX(), node.getY()-1)) {
            neighbours.add(new Node(node.getX(), node.getY()-1, tbreaker*manhattanDist(node.getX(), node.getY()-1,goal), node.getDistance()+ 1));
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
    public boolean equals(Object obj) {
        if(!(obj instanceof Node)) {
            return false;
        }
        Node n = (Node) obj;
        return  this.x == n.getX() &&
                this.y == n.getY();
    }

    @Override
    public int compareTo(Node n) {
        //Calculate each node's function (for the pathfinding search i.e. the distance from source + the heuristic to goal)
        double thisFunction = 0.0;
        double nFunction = 0.0;
        if(Graph.ALGORITHM == Graph.A_STAR) {
            thisFunction = this.heuristic + this.distance;
            nFunction = n.getHeuristic() + n.getDistance();
        }
        else {
            thisFunction = this.distance;
            nFunction = n.getDistance();
        }

        if(thisFunction < nFunction) {
            //This object is less than n
            return -1;
        }
        else if(thisFunction > nFunction) {
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
