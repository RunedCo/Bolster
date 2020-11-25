package co.runed.bolster.wip;

import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Currency implements IRegisterable
{
    String id;
    String name;
    String shortName;
    ItemStack icon = new ItemStack(Material.GOLD_INGOT);
    boolean isItem = false;
    boolean pluralize;

    public Currency(String id, String name, boolean pluralize, ItemStack itemStack)
    {
        this(id, name, name, pluralize);

        this.icon = itemStack;
        this.isItem = true;
    }

    public Currency(String id, String name, String shortName, boolean pluralize)
    {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.pluralize = pluralize;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    public boolean shouldPluralize()
    {
        return pluralize;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    public void setIcon(ItemStack icon)
    {
        this.icon = icon;
    }

    @Override
    public ItemStack getIcon()
    {
        return icon;
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public void create(ConfigurationSection config)
    {

    }
}
