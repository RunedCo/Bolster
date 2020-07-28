package co.runed.bolster.abilities.costs;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.properties.Properties;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ItemAbilityCost extends AbilityCost
{
    Class<? extends Item> itemClass;
    int count;

    public ItemAbilityCost()
    {
        this(1);
    }

    public ItemAbilityCost(int count)
    {
        this(null, count);
    }

    public ItemAbilityCost(Class<? extends Item> itemClass)
    {
        this(itemClass, 1);
    }

    public ItemAbilityCost(Class<? extends Item> itemClass, int count)
    {
        this.itemClass = itemClass;
        this.count = count;
    }

    @Override
    public boolean run(Ability ability, Properties properties)
    {
        LivingEntity caster = properties.get(AbilityProperties.CASTER);

        if (!(caster instanceof Player)) return false;

        Player player = (Player) caster;

        // If player is in creative don't remove items
        if (player.getGameMode().equals(GameMode.CREATIVE)) return true;

        Item item;

        if (this.itemClass == null)
        {
            item = properties.get(AbilityProperties.ITEM);

            if (item == null) return false;
        }
        else
        {
            item = Bolster.getItemManager().createItem(player, this.itemClass);
        }

        return Bolster.getItemManager().removeItem(player, item, this.count);
    }
}
