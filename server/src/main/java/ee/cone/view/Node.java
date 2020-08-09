package ee.cone.view;

public class Node {
    private String key;
    private String color;
    private String loc;

    public Node(String key, String color, String loc) {
        this.key = key;
        this.color = color;
        this.loc = loc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
