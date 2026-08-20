package model;

public class DictionaryItem {
    private int id;
    private String name;
    private String itemType; // "SKILL" or "ACCOMMODATION"

    public DictionaryItem(int id, String name, String itemType) {
        this.id = id;
        this.name = name;
        this.itemType = itemType;
    }

    public DictionaryItem(int id, String name) {
        this(id, name, "GENERAL");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    @Override
    public String toString() {
        return name;
    }
}