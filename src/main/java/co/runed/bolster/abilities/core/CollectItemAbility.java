package co.runed.bolster.abilities.core;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.conditions.BlockIsMaterialCondition;
import co.runed.bolster.abilities.conditions.CasterIsEntityTypeCondition;
import co.runed.bolster.items.Item;
import co.runed.bolster.properties.Properties;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CollectItemAbility extends Ability
{
    Material material;
    Class<? extends Item> itemClass;
    int outputCount = 1;

    public CollectItemAbility(Material material, Class<? extends Item> itemClass)
    {
        this(material, itemClass, 1);
    }

    public CollectItemAbility(Material material, Class<? extends Item> itemClass, int outputCount)
    {
        super();

        this.itemClass = itemClass;
        this.material = material;
        this.outputCount = outputCount;

        // SET DEFAULT COOLDOWN
        this.setCooldown(10);

        this.addCondition(new CasterIsEntityTypeCondition(EntityType.PLAYER));
        this.addCondition(new BlockIsMaterialCondition(material));
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player player = (Player) properties.get(AbilityProperties.CASTER);

        Bolster.getItemManager().giveItem(player, itemClass, outputCount);
    }
}
