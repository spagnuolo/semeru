package graph;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class Node implements Comparable<Node> {
    private static int inputOrder = 0;
    private int order;
    private String name;
    private HashMap<Node, Integer> neighbours;
    private HashMap<Node, Path> paths;
    private double sigma;

    public Node(String name) {
        order = inputOrder++;
        setName(name);
        setSigma(0.0);
        neighbours = new HashMap<Node, Integer>();
    }

    public String getName() {
        return this.name;
    }

    public int getWeight(Node target) {
        return neighbours.get(target);
    }

    /**
     * @return returns the neighbours HashMap.
     */
    public Set<Map.Entry<Node, Integer>> getNeighbours() {
        return neighbours.entrySet();
    }

    public Path getPath(Node target) {
        return paths.get(target);
    }

    public Collection<Path> getPaths() {
        return paths.values();
    }

    /**
     *
     * @return returns the sigma for the betweenness centrality measure calculation.
     */
    public double getSigma() {
        return sigma;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNeighbour(Node target, int weight) {
        neighbours.put(target, weight);
    }

    public void setPath(Node source, Path target) {
        paths.put(source, target);
    }

    /**
     * @param sigma sets the sigma for the betweenness centrality measure calculation.
     */
    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    /**
     * @param sigma increases the sigma for the betweenness centrality measure calculation.
     */
    public void increaseSigma(double sigma) {
        this.sigma += sigma;
    }

    /**
     * Initializes the HashMap paths
     * @param size is the number of paths or the number of nodes in the graph.
     */
    public void initPaths(int size) {
        paths = new HashMap<Node, Path>(size);
    }

    @Override
    public int compareTo(Node node) {
        if (order > node.order) return 1;
        else if (order < node.order) return -1;
        else return 0;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getName());
        result.append(": ");
        for (Map.Entry<Node, Integer> entry : getNeighbours()) {
            result.append(entry.getKey().name);
            result.append("(");
            result.append(entry.getValue());
            result.append(") ");
        }
        return result.substring(0);
    }
}
