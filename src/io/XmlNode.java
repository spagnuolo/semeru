package io;

public class XmlNode {
    private String id;
    private String name;

    public XmlNode(){
        setId("");
        setName("");
    }

    public XmlNode(String id, String name){
        setId(id);
        setName(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name+" id:"+id;
    }
}

