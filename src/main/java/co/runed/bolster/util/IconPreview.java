package co.runed.bolster.util;

import co.runed.dayroom.util.Identifiable;
import co.runed.dayroom.util.Nameable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface IconPreview {
    default ItemStack getIcon() {
        var builder = new ItemBuilder(Material.STICK);

        if (this instanceof Identifiable identifiable) {
            builder = builder.setDisplayName(Component.text(identifiable.getId()));
        }

        if (this instanceof Nameable nameable) {
            builder = builder.setDisplayName(Component.text(nameable.getName()));
        }

        return builder.build();
    }
}
