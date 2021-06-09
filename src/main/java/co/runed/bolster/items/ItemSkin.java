package co.runed.bolster.items;

import co.runed.bolster.util.config.IConfigurable;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.configuration.ConfigurationSection;

public class ItemSkin implements IRegisterable, IConfigurable
{
    private final String id;
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
    public String getId()
    {
        return Registries.ITEM_SKINS.getId(this);
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public void create(ConfigurationSection config)
    {
        this.name = config.getString("name", "");
        this.showName = config.getBoolean("show-name", false);
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
