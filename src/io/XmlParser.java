package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.LinkedList;

public class XmlParser {
    private BufferedReader inputStream = null;
    private String currentLine = null;
    private LinkedList<XmlNode> nodes = new LinkedList<>();
    private LinkedList<XmlEdge> edges = new LinkedList<>();

    /**
     * Parses the provided XML input file.
     * @param xmlFile is a String object that contains the path and name of the XML input file to
     *                be parsed.
     */
    public XmlParser(String xmlFile) {
        try {
            inputStream = new BufferedReader(new FileReader(xmlFile));

            while (nextLine()) {
                currentLine = currentLine.stripLeading();
                if (currentLine.startsWith("<node")) parseNode();
                else if (currentLine.startsWith("<edge")) parseEdge();
            }
        }
        catch (IOException exc) {System.out.println(String.valueOf(exc.getStackTrace()));}
        finally {
            try {
                if (inputStream != null) inputStream.close();
            }
            catch (IOException exc) {System.out.println(String.valueOf(exc.getStackTrace()));}
        }
    }

    public LinkedList<XmlNode> getNodes() {
        return nodes;
    }

    public LinkedList<XmlEdge> getEdges() {
        return edges;
    }

    public int getNodesSize() {
        return nodes.size();
    }

    public int getEdgesSize() {
        return edges.size();
    }

    private boolean nextLine() throws IOException {
        currentLine = inputStream.readLine();
        if (currentLine == null) return false;
        return true;
    }

    private void parseNode() throws IOException {
        XmlNode node = new XmlNode();
        node.setName(firstStringBetween('"', '"'));
        nextLine();
        node.setId(firstStringBetween('>', '<'));
        nodes.add(node);
    }

    private void parseEdge() throws IOException {
        XmlEdge edge = new XmlEdge();
        edge.setSource(firstStringBetween('"', '"'));
        edge.setTarget(lastStringBetween('"', '"'));
        nextLine();
        edge.setId(firstStringBetween('>', '<'));
        nextLine();
        edge.setWeight(firstStringBetween('>', '<'));
        edges.add(edge);
    }

    private String firstStringBetween(char begin, char end) {
        int beginIndex = currentLine.indexOf(begin) + 1;
        int endIndex = currentLine.indexOf(end, beginIndex);
        return currentLine.substring(beginIndex, endIndex);
    }

    private String lastStringBetween(char begin, char end) {
        int endIndex = currentLine.lastIndexOf(end) - 1;
        int beginIndex = currentLine.lastIndexOf(begin, endIndex) + 1;
        return currentLine.substring(beginIndex, endIndex + 1);
    }
}