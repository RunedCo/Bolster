package co.runed.bolster.items;

public class ItemSkin {
    private String id;
    private String name;
    private int customModelData = 0;

    public ItemSkin(String id, String name, int customModelData) {
        this.id = id;
        this.name = name;
        this.customModelData = customModelData;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCustomModelData() {
        return customModelData;
    }
}
