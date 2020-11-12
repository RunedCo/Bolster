package co.runed.bolster.items;

import co.runed.bolster.util.registries.IRegisterable;

public class ItemSkin implements IRegisterable
{
    private String id;
    private String name;
    private int customModelData = 0;
    private boolean showName;

    public ItemSkin(String id, String name, int customModelData, boolean showName)
    {
        this.id = id;
        this.name = name;
        this.customModelData = customModelData;
        this.showName = showName;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    public String getName()
    {
        return name;
    }

    public int getCustomModelData()
    {
        return customModelData;
    }

    public boolean shouldShowName()
    {
        return showName;
    }
}
