package io;

public class XmlEdge {
    private String id;
    private String source;
    private String target;
    private String weight;

    public XmlEdge(){
        setId("");
        setSource("");
        setTarget("");
        setWeight("");
    }

    public XmlEdge(String id, String source, String target, String weight){
        setId(id);
        setSource(source);
        setTarget(target);
        setWeight(weight);
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getWeight() {
        return weight;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return id+" ("+source+" <-"+weight+"-> "+target+")";
    }
}
