package co.runed.bolster.abilities.core;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.conditions.BlockIsMaterialCondition;
import co.runed.bolster.abilities.conditions.IsEntityTypeCondition;
import co.runed.bolster.items.Item;
import co.runed.bolster.properties.Properties;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class CollectItemAbility extends Ability
{
    Collection<Material> materials = new ArrayList<>();
    Class<? extends Item> itemClass;
    int outputCount = 1;

    public CollectItemAbility(Material material, Class<? extends Item> itemClass)
    {
        this(Collections.singletonList(material), itemClass);
    }

    public CollectItemAbility(Collection<Material> materials, Class<? extends Item> itemClass)
    {
        this(materials, itemClass, 1);
    }

    public CollectItemAbility(Material material, Class<? extends Item> itemClass, int outputCount)
    {
        this(Collections.singletonList(material), itemClass, outputCount);
    }

    public CollectItemAbility(Collection<Material> materials, Class<? extends Item> itemClass, int outputCount)
    {
        super();

        this.itemClass = itemClass;
        this.materials = materials;
        this.outputCount = outputCount;

        // SET DEFAULT COOLDOWN
        this.setCooldown(10);

        this.addCondition(new IsEntityTypeCondition(EntityType.PLAYER));
        this.addCondition(new BlockIsMaterialCondition(this.materials));
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player player = (Player) properties.get(AbilityProperties.CASTER);

        Bolster.getItemManager().giveItem(player, itemClass, outputCount);
    }
}
