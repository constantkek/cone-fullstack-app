package ee.cone.view;

public class Link {
    private String from;
    private String to;

    public Link(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return this.from + this.to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
