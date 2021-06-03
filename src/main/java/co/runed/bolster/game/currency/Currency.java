package co.runed.bolster.game.currency;

import co.runed.bolster.util.ICategorised;
import co.runed.bolster.util.IConfigurable;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Currency implements IRegisterable, ICategorised, IConfigurable
{
    String id;
    String name;
    String shortName;
    ItemStack icon = new ItemStack(Material.GOLD_INGOT);
    boolean isItem = false;
    boolean pluralize;
    
    public Currency(String id, String name, String shortName, ItemStack itemStack, boolean pluralize, boolean isItem)
    {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.pluralize = pluralize;
        this.icon = itemStack;
        this.isItem = isItem;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getPluralisedName()
    {
        return this.getName() + (this.shouldPluralize() ? "s" : "");
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public boolean shouldPluralize()
    {
        return pluralize;
    }

    @Override
    public String getId()
    {
        return this.id != null ? this.id : Registries.CURRENCIES.getId(this);
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
