package co.runed.bolster.abilities.core;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.conditions.IsEntityTypeCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.function.Supplier;

public class OpenInventoryAbility extends TargetedAbility<BolsterEntity>
{
    Supplier<Inventory> inventory;

    public OpenInventoryAbility(Target<BolsterEntity> target, Supplier<Inventory> inventory)
    {
        super(target);

        this.inventory = inventory;

        this.addCondition(new IsEntityTypeCondition(target, EntityType.PLAYER));
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player player = (Player) this.getTarget().get(properties).getBukkit();

        player.closeInventory();
        player.openInventory(inventory.get());
    }
}
