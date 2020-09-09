package co.runed.bolster.abilities.core;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.conditions.IsEntityTypeCondition;
import co.runed.bolster.util.cost.ItemCost;
import co.runed.bolster.items.Item;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ConvertItemAbility extends Ability
{
    int inputCost;
    Class<? extends Item> outputItemClass;
    int outputCount;

    public ConvertItemAbility(int inputCost, Class<? extends Item> outputItemClass, int outputItemCount)
    {
        super();

        this.inputCost = inputCost;

        this.outputItemClass = outputItemClass;
        this.outputCount = outputItemCount;

        this.addCost(new ItemCost(inputCost));
        this.addCondition(new IsEntityTypeCondition(EntityType.PLAYER));
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player player = (Player) properties.get(AbilityProperties.CASTER).getBukkit();

        Bolster.getItemManager().giveItem(player, this.outputItemClass, this.outputCount);
    }
}
