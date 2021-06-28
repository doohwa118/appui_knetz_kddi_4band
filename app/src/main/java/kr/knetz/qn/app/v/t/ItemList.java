package kr.knetz.qn.app.v.t;


public class ItemList {
    String name;
    String value;
    int id;

    public ItemList(String name, String value, int id){
        this.name = name;
        this.value = value;
        this.id = id;
    }

    public ItemList(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
