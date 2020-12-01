package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.BlockIsMaterialCondition;
import co.runed.bolster.conditions.IsEntityTypeCondition;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.target.Target;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class CollectItemAbility extends Ability
{
    Collection<Material> materials = new ArrayList<>();
    Function<Properties, Class<? extends Item>> itemClassFunc;
    int outputCount = 1;

    public CollectItemAbility(Collection<Material> materials, Class<? extends Item> itemClass)
    {
        this(materials, itemClass, 1);
    }

    public CollectItemAbility(Collection<Material> materials, Class<? extends Item> itemClass, int outputCount)
    {
        this(materials, (properties) -> itemClass, outputCount);
    }

    public CollectItemAbility(Collection<Material> materials, Function<Properties, Class<? extends Item>>itemClassFunc)
    {
        this(materials, itemClassFunc, 1);
    }

    public CollectItemAbility(Collection<Material> materials, Function<Properties, Class<? extends Item>>itemClassFunc, int outputCount)
    {
        super();

        this.itemClassFunc = itemClassFunc;
        this.materials = materials;
        this.outputCount = outputCount;

        // SET DEFAULT COOLDOWN
        this.setCooldown(10);

        this.addCondition(new IsEntityTypeCondition(Target.CASTER, EntityType.PLAYER));
        this.addCondition(new BlockIsMaterialCondition(this.materials));
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player player = (Player) properties.get(AbilityProperties.CASTER).getBukkit();

        ItemManager.getInstance().giveItem(player, itemClassFunc.apply(properties), outputCount);
    }
}
