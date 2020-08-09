package ee.cone.view;

import java.util.ArrayList;

public class View {
    private String viewName;
    private final String clazz;
    private ArrayList<Node> nodeDataArray;
    private ArrayList<Link> linkDataArray;

    public View(String viewName) {
        this.clazz = "GraphLinksModel";
        this.viewName = viewName;
        this.nodeDataArray = new ArrayList<>();
        this.linkDataArray = new ArrayList<>();
    }

    public ArrayList<Node> getNodeDataArray() {
        return nodeDataArray;
    }

    public void setNodeDataArray(ArrayList<Node> nodeDataArray) {
        this.nodeDataArray = nodeDataArray;
    }

    public ArrayList<Link> getLinkDataArray() {
        return linkDataArray;
    }

    public void setLinkDataArray(ArrayList<Link> linkDataArray) {
        this.linkDataArray = linkDataArray;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public ArrayList<Node> getNodes() {
        return nodeDataArray;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodeDataArray = nodes;
    }

    public ArrayList<Link> getLinks() {
        return linkDataArray;
    }

    public void setLinks(ArrayList<Link> links) {
        this.linkDataArray = links;
    }

    public void addNode(Node node) {
        this.nodeDataArray.add(node);
    }

    public void addLink(Link link) {
        this.linkDataArray.add(link);
    }

    public void removeNode(Node node) {
        this.nodeDataArray.remove(node);
    }

    public void removeLink(Link link) {
        this.linkDataArray.remove(link);
    }
}
