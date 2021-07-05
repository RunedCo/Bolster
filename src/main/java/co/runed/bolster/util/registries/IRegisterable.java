package co.runed.bolster.util.registries;

import co.runed.bolster.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

// TODO SPLIT INTO MULTIPLE INTERFACES (IRegisterable, IConfigurable, ICategorised, and one for icon, display name, and description)
public interface IRegisterable
{
    String getId();

    default ItemStack getIcon()
    {
        return new ItemBuilder(Material.STICK)
                .setDisplayName(Component.text(this.getId()))
                .build();
    }
}
