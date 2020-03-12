package io;

import graph.Graph;
import graph.Edge;
import graph.Node;
import errorhandling.NoSuchNodeException;
import errorhandling.ShortestPathException;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class XmlCreator {
    private Graph graph;
    private PrintWriter outputStream;
    private int indentationWidth;

    /**
     * This method creates the XML output file.
     * @param graph is a Graph object containing the relevant graph data.
     * @param outputFile is the path and the name of the XML output file where the results shall be stored
     *                   once the -a option is used in the command line. Please see manual.
     */
    public XmlCreator(Graph graph, String outputFile) {
        this.graph = graph;
        outputStream = null;
        indentationWidth = 0;

        try {
            outputStream = new PrintWriter(new FileWriter(outputFile));
        }
        catch (IOException exc) {
            System.err.println(exc);
        }

        doCreateXml();
    }

    private void doCreateXml() {
        printHeader();
        printNodes();
        printEdges();
        printTrailer();

        if (outputStream != null) {
            outputStream.close();
        }
    }

    private void printLine(String line) {
        outputStream.print("  ".repeat(indentationWidth));
        outputStream.println(line);
    }

    private void increaseIndentation() {
        ++indentationWidth;
    }

    private void decreaseIndentation() {
        --indentationWidth;
    }

    private void printHeader() {
        printLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        printLine("<graphml>");
        increaseIndentation();
        printLine("<key id=\"v_id\" for=\"node\" attr.name=\"id\" attr.type=\"double\"/>");
        printLine("<key id=\"betweenness_centrality\" for=\"node\" attr.name=\"betweenness\" attr.type=\"double\"/>");
        printLine("<key id=\"shortest_path_to\" for=\"node\"/>");
        printLine("<key id=\"e_id\" for=\"edge\" attr.name=\"id\" attr.type=\"double\"/>");
        printLine("<key id=\"e_weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>");
        printLine("<graph id=\"G\" edgedefault=\"undirected\">");
        increaseIndentation();
    }

    private void printNodes () {
        int id = 0;
        for (Node node : graph.getNodes()) {
            printLine("<node id=\""+node.getName()+"\">");
            increaseIndentation();
            printLine("<data key=\"v_id\">"+ id++ +"</data>");
            printNodeBetweenness(node);
            printNodeShortestPath(node);
            decreaseIndentation();
            printLine("</node>");
        }
    }

    private void printNodeBetweenness(Node node) {
        String line = "<data key=\"betweenness_centrality\">";
        try {
            line += graph.calcNodeBetweenness(node.getName());
        }
        catch (NoSuchNodeException exc) {
            System.out.println(exc.getMessage());
        }
        printLine(line+"</data>");
    }

    private void printNodeShortestPath(Node node) {
        for (Node target : graph.getNodes()) {
            String line = "<data shortest_path_to=\""+target.getName()+"\">";
            try {
                line += graph.getShortestPath(node.getName(), target.getName());
            }
            catch (ShortestPathException exc) {
                System.out.println(exc);
            }
            printLine(line+"</data>");
        }
    }

    private void printEdges () {
        int id = 0;
        for (Edge edge : graph.getEdges()) {
            printLine("<edge source=\""+edge.getSource()+"\" target=\""+edge.getTarget()+"\">");
            increaseIndentation();
            printLine("<data key=\"e_id\">"+ id++ +"</data>");
            printLine("<data key=\"e_weight\">"+edge.getWeight()+"</data>");
            decreaseIndentation();
            printLine("</edge>");
        }
    }

    private void printTrailer() {
        decreaseIndentation();
        printLine("</graph>");
        decreaseIndentation();
        printLine("</graphml>");
    }
}
