package graph;

import java.lang.Runnable;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.NoSuchElementException;

import errorhandling.NoSuchNodeException;
import errorhandling.ShortestPathException;
import errorhandling.NegativeWeightException;

public class Graph implements Runnable {
    private HashMap<String, Node> nodes;
    private ConcurrentLinkedQueue<Node> concurrentNodesQueue;
    private LinkedList<Edge> edges;
    private boolean hasPathsFound;
    private LinkedList<Node> orderOfDistancesStack;
    private boolean hasBetweennessCalculated;
    private HashMap<Node, Double> nodesBetweenness;

    public Graph() {
        hasPathsFound = false;
        hasBetweennessCalculated = false;
        nodes = new HashMap<>();
        edges = new LinkedList<>();
    }

    /**
     * This method uses the allPairsShortestPath() method to traverse through the distances of the
     * shortest paths for all nodes and returns the biggest distance of all paths.
     * @return returns the diameter of the graph.
     */
    public int getDiameter()  {
        if (!hasPathsFound) {
            allPairsShortestPath();
        }

        int diameter = 0;
        for (Node node : nodes.values()) {
            for (Path path : node.getPaths()) {
                int distance = path.getDistance();
                if (distance == Integer.MAX_VALUE) break;
                if (diameter < distance) {
                    diameter = distance;
                }
            }
        }

        return diameter;
    }

    public Collection<Node> getNodes() {
        return new TreeSet<Node>(nodes.values());
    }

    public int getNodesSize() {
        return nodes.size();
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }

    /**
     * This method creates a new Node instance.
     * @param name of the node as String object.
     */
    public void setNode(String name) {
        if (!nodes.containsKey(name)) {
            nodes.put(name, new Node(name));
        }
        hasPathsFound = false;
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    public int getEdgesSize() {
        return edges.size();
    }

    /**
     * For both String objects (source and target), two Node objects are created.
     * Each Node object is then set as neighbour for the respective other Node object.
     * Weight attributes are set and finally both Node objects are added to the list of edges.
     *
     * @param id a unique id for the edge
     * @param source a unique string wich represents a node.
     * @param target a unique string wich is connected to source.
     * @param weight the weight between source an target.
     * @throws NegativeWeightException to caller and is caught there
     */
    public void setEdge(String id, String source, String target, int weight) throws NegativeWeightException {
        if (weight < 0) {
            throw new NegativeWeightException("No negative weights allowed. Edge with id:"+id+" ignored.");
        }
        setNode(source);
        setNode(target);
        getNode(source).setNeighbour(getNode(target), weight);
        getNode(target).setNeighbour(getNode(source), weight);
        edges.add(new Edge(id, source, target, weight));
        hasPathsFound = false;
    }

    /**
     * This boolean flag returns true if a path from every node to every other existing node in the graph was found.
     * @return returns false if nodes are unreachable or the graph is empty.
     */
    public boolean isConnected() {
        Node node;
        try {
            node = nodes.values().iterator().next();
        }
        catch (NoSuchElementException exc) {
            return false;
        }
        if (!hasPathsFound) {
            shortestPath(node.getName());
        }
        for (Path path : node.getPaths()) {
            if (path.getDistance() == Integer.MAX_VALUE) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method returns the shortest path as a String object with arrows
     * in between the nodes along the path. The start node is the source node
     * and the end node is the target node in the method.
     * If the source or target node isn't in the graph, the String 'No such Node*s'
     * will be returned. If no path to the target was found, a String with the message
     * 'source is not connected to target' will be returned.
     *
     * @param source a String which uniquely identifies the source node
     * @param target a String which uniquely identifies the target node
     * @return a String of the shortest path
     * @throws ShortestPathException to caller and is caught there
     */
    public String getShortestPath(String source, String target) throws ShortestPathException {
        if (!nodes.containsKey(source)) {
            throw new ShortestPathException("No such Node: " + source + " \tPlease check manual:");
        }
        if (!nodes.containsKey(target)) {
            throw new ShortestPathException("No such Node: " + target + " \tPlease check manual:");
        }

        if (!hasPathsFound) {
            shortestPath(source);
        }

        Node start = getNode(source);
        Path path = start.getPath(getNode(target));
        if (path.getDistance() == Integer.MAX_VALUE) {
            throw new ShortestPathException(source+" is not connected to "+target);
        }

        StringBuilder result = new StringBuilder();
        while (path != null) {
            result.insert(0, path);
            path = path.getPredecessor();
        }

        return result.toString();
    }

    /**
     * Returns the betweenness centrality measure for a selected node,
     * where this measure is defined as the total number of shortest paths
     * that pass the given node divided by the number of shortest paths
     * that go through the given node.
     *
     * @param node as String which uniquely identifies the node
     * @return the betweenness centrality measure for the given the node as double
     * @throws NoSuchNodeException to caller and is caught there
     */
    public double calcNodeBetweenness(String node) throws NoSuchNodeException {
        if(!nodes.containsKey(node)) {
            throw new NoSuchNodeException("Node "+node+" not found. Please check manual:");
        }

        if(hasBetweennessCalculated) {
            return nodesBetweenness.get(getNode(node))/2;
        }

        nodesBetweenness = new HashMap<>();
        for (Node vertex : getNodes()) {
            nodesBetweenness.put(vertex, 0.0);
        }

        for (Node source : nodes.values()) {
            shortestPath(source.getName());

            HashMap<Node, Double> delta = new HashMap<>();
            for (Node vertex : getNodes()) {
                delta.put(vertex, 0.0);
            }

            while (!orderOfDistancesStack.isEmpty()) {
                Node target = orderOfDistancesStack.removeLast();
                double quotient = (delta.get(target) + 1.0) / target.getSigma();

                Path path = source.getPath(target);
                for (String vertex : path.getBranches()) {
                    double tmpDelta = delta.get(getNode(vertex));
                    delta.put(getNode(vertex), (getNode(vertex).getSigma() * quotient) + tmpDelta);
                }

                if (!target.equals(source)) {
                    double tmpBetweenness = nodesBetweenness.get(target);
                    nodesBetweenness.put(target, tmpBetweenness + delta.get(target));
                }
            }
        }

        hasBetweennessCalculated = true;
        hasPathsFound = true;

        // Undirectional graph, thus divided by 2
        return nodesBetweenness.get(getNode(node))/2;
    }

    private void shortestPath(String source) {
        Node start = getNode(source);

        // Add all other nodes as possible path.
        start.initPaths(getNodesSize());
        for (Node node : nodes.values()) {
            start.setPath(node, new Path(node.getName()));
            node.setSigma(0);
        }

        // Order of decreasing distance.
        orderOfDistancesStack = new LinkedList<>();

        // Map of equal distances, used for betweenness measure.
        HashMap<Path, Integer> equalDistances = new HashMap<>();
        equalDistances.put(start.getPath(start), 0);
        start.setSigma(1.0);

        start.getPath(start).setDistance(0);
        TreeSet<Path> q = new TreeSet<>(start.getPaths());
        while (!q.isEmpty()) {
            Path first = q.pollFirst();
            if (first.getDistance() == Integer.MAX_VALUE) {
                break; // Only unreachable nodes are left in q.
            }
            orderOfDistancesStack.add(getNode(first.getName()));

            // Count equal paths for the betweenness measure.
            if (first.getPredecessor() != null) {
                getNode(first.getName()).increaseSigma(getNode(first.getPredecessor().getName()).getSigma());
            }
            else {
                getNode(first.getName()).increaseSigma(1);
            }

            for (Map.Entry<Node, Integer> neighbour : getNode(first.getName()).getNeighbours()) {
                Path path = start.getPath(neighbour.getKey());
                int altDistance = first.getDistance() + neighbour.getValue();
                if(altDistance < path.getDistance()) {
                    // Relax.
                    q.remove(path);
                    path.setDistance(altDistance);
                    path.setPredecessor(first);
                    q.add(path);

                    // New and fresh branch for the betweenness centrality.
                    equalDistances.put(path, altDistance);
                    path.clearBranches();
                    path.addBranch(first.getName());
                    getNode(path.getName()).setSigma(0.0);
                }
                else if (equalDistances.get(path).equals(altDistance)) {
                    // Store equal paths as possible branches.
                    neighbour.getKey().increaseSigma(getNode(first.getName()).getSigma());
                    path.addBranch(first.getName());
                }
            }
        }
    }

    private void allPairsShortestPath() {
        concurrentNodesQueue = new ConcurrentLinkedQueue<Node>(nodes.values());
        Thread thread = new Thread(this);
        thread.setName("allPairsShortestPath");
        thread.start();
        allPairsShortestPathParallel();
        try {
            thread.join();
        }
        catch (InterruptedException exc) {
            System.out.println(exc.getMessage());
        }
        hasPathsFound = true;
    }

    private void allPairsShortestPathParallel() {
        Node node = concurrentNodesQueue.poll();
        while (node != null) {
            shortestPath(node.getName());
            node = concurrentNodesQueue.poll();
        }
    }

    @Override
    public void run() {
        allPairsShortestPathParallel();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Graph").append(this.hashCode());
        result.append(", nodes: ").append(getNodesSize());
        result.append(", edges: ").append(getEdgesSize());
        result.append(", betweennessCalculated: ").append(hasBetweennessCalculated);
        result.append(", pathsFound: ").append(hasPathsFound);
        return result.toString();
    }
}
