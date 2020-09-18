package co.runed.bolster.upgrade;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.cost.Cost;
import co.runed.bolster.util.cost.ManaCost;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Upgrade tree
 * Can set name, max number of points and starting number of points + cost
 * cost units are arbitrary and should be handled by the thing using the upgrade (e.g. mana or $$$)
 */
public class Upgrade implements IRegisterable
{
    // upgrade needs a way to add abilities, maybe have separate UpgradeInfo class that can get added
    // .addAbility(AbilityTrigger.LEFT_CLICK, () -> new DisguiseAbility(EntityType.ARMOR_STAND))

    String id;
    String name;
    ItemStack icon = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
    Cost cost = new ManaCost(0);
    int maxLevel = 1;
    int defaultLevel = 0;
    boolean exclusive = false;
    List<UpgradeAbilityData> abilities = new ArrayList<>();

    public Upgrade(String id)
    {
        this.id = id;
    }

    public Upgrade setName(String name)
    {
        this.name = name;

        return this;
    }

    public Upgrade setCost(Cost cost)
    {
        this.cost = cost;

        return this;
    }

    public Upgrade setDefaultLevel(int defaultLevel)
    {
        this.defaultLevel = defaultLevel;

        return this;
    }

    public Upgrade setMaxLevel(int maxLevel)
    {
        this.maxLevel = maxLevel;

        return this;
    }

    public Upgrade setIcon(ItemStack icon)
    {
        this.icon = icon;

        return this;
    }

    public Upgrade addAbility(AbilityTrigger trigger, Supplier<Ability> func)
    {
        this.abilities.add(new UpgradeAbilityData(trigger, func));

        return this;
    }

    public Upgrade register()
    {
        Bolster.getUpgradeRegistry().register(this.getId(), this);

        return this;
    }

    public Upgrade setExclusive(boolean exclusive)
    {
        this.exclusive = exclusive;

        return this;
    }

    public String getName()
    {
        if (this.maxLevel <= 1)
        {
            return name;
        }

        return name + StringUtil.toRoman(10);
    }

    public int getMaxLevel()
    {
        return maxLevel;
    }

    public ItemStack getIcon()
    {
        return icon;
    }

    public List<UpgradeAbilityData> getAbilities()
    {
        return abilities;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    public int getDefaultLevel()
    {
        return this.defaultLevel;
    }

    public static class UpgradeAbilityData
    {
        AbilityTrigger trigger;
        Supplier<Ability> abilitySupplier;

        public UpgradeAbilityData(AbilityTrigger trigger, Supplier<Ability> func)
        {
            this.trigger = trigger;
            this.abilitySupplier = func;
        }

        public AbilityTrigger getTrigger()
        {
            return trigger;
        }

        public Supplier<Ability> getAbilitySupplier()
        {
            return abilitySupplier;
        }
    }
}
