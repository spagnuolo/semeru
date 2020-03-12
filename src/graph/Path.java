package graph;
import java.util.LinkedList;

public class Path implements Comparable<Path> {
    private String name;
    private int distance;
    private Path predecessor;
    private LinkedList<String> branches;

    public Path(String name) {
        setName(name);
        setDistance(Integer.MAX_VALUE);
        setPredecessor(null);
        branches = new LinkedList<String>();
    }

    public String getName() {
        return this.name;
    }

    public int getDistance() {
        return this.distance;
    }

    public Path getPredecessor() {
        return this.predecessor;
    }

    public LinkedList<String> getBranches() {
        return branches;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setPredecessor(Path predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * @param branch is the branch added to the predecessor tree.
     */
    public void addBranch(String branch) {
        branches.add(branch);
    }

    /**
     * Clears the the predecessor tree.
     */
    public void clearBranches() {
        branches.clear();
    }

    @Override
    public int compareTo(Path path) {
        if (distance == path.distance) {
            // Lexicographically.
            return name.compareTo(path.name);
        }

        return Integer.compare(distance, path.distance);
    }

    @Override
    public String toString() {
        if (predecessor != null) {
            return " → "+name+"("+distance+")";
        }
        return name+"("+distance+")";
    }
}
