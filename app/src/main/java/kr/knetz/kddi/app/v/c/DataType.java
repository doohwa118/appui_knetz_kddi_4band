package kr.knetz.kddi.app.v.c;


public class DataType {
    private String name;
    private String value;
    private int id;

    public DataType(String name, int id){
        this.name = name;
        this.id = id;
    }

    public DataType(String name, String value, int id){
        this.name = name;
        this.value = value;
        this.id = id;
    }

    public DataType(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
