package main;

import java.lang.StringBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;
import java.io.File;
import java.io.IOException;

import io.XmlCreator;
import io.XmlParser;
import io.XmlNode;
import io.XmlEdge;

import graph.Graph;
import graph.Node;
import graph.Edge;

import errorhandling.NegativeWeightException;
import errorhandling.NoSuchNodeException;
import errorhandling.ShortestPathException;

public class Main {
    private static String manual;
    static {
        try {
            manual = new String(Files.readAllBytes(Paths.get("Main/manual.txt")));
        }
        catch (IOException e) {
            System.out.println(e.getMessage()+" No SHORT MANUAL available. Please check USER MANUAL in project documentation!");
        }
    }

    /**
     * @param arg are the arguments from the main function as described in the manuals.
     */
    public static void main(String[] arg) {
        try {
            if (arg.length > 0) {
                boolean doesFileExist = new File(arg[0]).exists();
                if (!doesFileExist) throw new NoSuchFileException("Given XML input file name and path incorrect. Please check manual:");
                var graphmlInputPath = arg[0];
                XmlParser xml = new XmlParser(graphmlInputPath);
                Graph network = new Graph();

                // Fill network.
                for (XmlNode node : xml.getNodes()) {
                    network.setNode(node.getName());
                }
                for (XmlEdge edge : xml.getEdges()) {
                    int weight = Integer.parseInt(edge.getWeight());
                    try {
                        network.setEdge(edge.getId(), edge.getSource(), edge.getTarget(), weight);
                    }
                    catch (NegativeWeightException exc) {
                        System.out.println(exc.getMessage());
                    }
                }

                // Options.
                if (arg.length == 1) {
                    graphProperties(network);
                }
                else {
                    switch(arg[1]) {
                        case "-a":
                        case "--all": graphToXml(network, arg); break;

                        case "-b":
                        case "--betweenness": graphNodeBetweenness(network, arg); break;

                        case "-s":
                        case "--shortestpath": graphShortestPath(network, arg); break;

                        default: System.out.println("Wrong arguments. Please check manual:" + manual);
                    }
                }
            }
            else throw new NoSuchFileException("No XML input file provided. Please check manual:");
        }
        catch(NoSuchFileException e){
            System.out.println(e.getMessage());
            System.out.println(manual);
        }
    }

    /**
     * @param graph is a Graph object containing the relevant graph data.
     */
    public static void graphProperties(Graph graph) {
        System.out.println("The graph properties are as follows:");
        System.out.println("Number of Nodes: "+graph.getNodesSize());
        System.out.println("Number of Egdes: "+graph.getEdgesSize());

        System.out.println("Nodes:");
        StringBuilder nodesString = new StringBuilder();
        for (Node node : graph.getNodes()) {
            nodesString.append(node.getName() +" ");
        }
        System.out.println(String.valueOf(nodesString));

        System.out.println("Edges: ");
        for (Edge edge : graph.getEdges()) {
            System.out.println("\t"+edge);
        }

        System.out.println("Nodes: Neighbours(Weight)");
        for(Node node : graph.getNodes()) {
            System.out.println(String.valueOf(node));
        }

        System.out.println("Connectivity: "+graph.isConnected());
        System.out.println("Diameter: "+graph.getDiameter());
    }

    /**
     * @param graph is a Graph object containing the relevant graph data.
     * @param arg are the arguments from the main function as described in the manuals.
     */
    public static void graphToXml(Graph graph, String[] arg) {
        if (arg.length == 3) {
            var xmlOutPath = arg[2];
            XmlCreator newXml = new XmlCreator(graph, xmlOutPath);
        }
        else {
            System.out.println("No output file path and name given. Please check manual:");
            System.out.println(manual);
        }
    }

    /**
     * @param graph is a Graph object containing the relevant graph data.
     * @param arg are the arguments from the main function as described in the manuals.
     */
    public static void graphNodeBetweenness(Graph graph, String[] arg) {
        try {
            if (arg.length == 3) {
                double temp = graph.calcNodeBetweenness(arg[2]);
                System.out.println("The betweenness centrality measure for node " + arg[2] + " is:");
                System.out.println(String.valueOf(temp));
            }
            else {
                System.out.println("Wrong arguments provided! Please check manual:");
                System.out.println(manual);
            }
        }
        catch (NoSuchNodeException exc) {
            System.out.println(exc.getMessage());
            System.out.println(manual);
        }
    }

    /**
     * @param graph is a Graph object containing the relevant graph data.
     * @param arg are the arguments from the main function as described in the manuals.
     */
    public static void graphShortestPath(Graph graph, String[] arg) {
        if (arg.length == 4) {
            try {
                String temp =  graph.getShortestPath(arg[2], arg[3]);
                System.out.println("The shortest path between node " + arg[2] + " and node " + arg[3] + " according to Dijkstra:");
                System.out.println(temp);
            }
            catch(ShortestPathException e) {
                System.out.println(String.valueOf(e.getMessage()));
                System.out.println(manual);
            }
        }
        else {
            System.out.println("Wrong arguments provided. Please check manual:");
            System.out.println(manual);
        }
    }
}
