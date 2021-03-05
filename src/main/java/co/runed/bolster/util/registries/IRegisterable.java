package co.runed.bolster.util.registries;

import co.runed.bolster.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

// TODO SPLIT INTO MULTIPLE INTERFACES (IRegisterable, IConfigurable, ICategorised, and one for icon, display name, and description)
public interface IRegisterable
{
    String getId();

    default String getName()
    {
        return this.getId();
    }

    String getDescription();

    default ItemStack getIcon()
    {
        return new ItemBuilder(Material.STICK)
                .setDisplayName(this.getName())
                .build();
    }
}
