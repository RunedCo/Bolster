package co.runed.bolster.upgrade;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.util.cost.Cost;
import co.runed.bolster.util.cost.ManaCost;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Upgrade tree
 * Can set name, max number of points and starting number of points + cost
 * cost units are arbitrary and should be handled by the thing using the upgrade (e.g. mana or $$$)
 */
public class Upgrade extends AbilityProvider implements IRegisterable
{
    public static final Upgrade SKELETON = new Upgrade("skeleton", "Skeleton", new ItemStack(Material.SKELETON_SKULL), new ManaCost(0), 0);
    public static final Upgrade IMPACT_SKELETON = new Upgrade("skeleton_impact", "Impact Skeleton", new ItemStack(Material.TNT), new ManaCost(100), 1);
    public static final Upgrade WITHER_SKELETON = new Upgrade("skeleton_wither", "Wither Skeleton", new ItemStack(Material.WITHER_ROSE), new ManaCost(100), 1);
    public static final Upgrade FLAME_SKELETON = new Upgrade("skeleton_flame", "Flame Skeleton", new ItemStack(Material.FIRE_CHARGE), new ManaCost(100), 1);

    public static final UpgradeTree SKELETON_UPGRADE_TREE =
            new UpgradeTree(SKELETON)
                    .addChild(new UpgradeTree(IMPACT_SKELETON))
                    .addChild(new UpgradeTree(WITHER_SKELETON))
                    .addChild(new UpgradeTree(FLAME_SKELETON));

    String id;
    String name;
    ItemStack icon;
    Cost cost;
    int maxLevel;
    int defaultLevel;
    boolean exclusive;

    List<Upgrade> upgrades = new ArrayList<>();

    public Upgrade(String id, String name, ItemStack icon, Cost cost)
    {
        this(id, name, icon, cost, 1);
    }

    public Upgrade(String id, String name, ItemStack icon, Cost cost, int maxLevel)
    {
        this(id, name, icon, cost, maxLevel, 0);
    }

    public Upgrade(String id, String name, ItemStack icon, Cost cost, int maxLevel, int defaultLevel)
    {
        this(id, name, icon, cost, maxLevel, defaultLevel, true);
    }

    public Upgrade(String id, String name, ItemStack icon, Cost cost, int maxLevel, int defaultLevel, boolean exclusive)
    {
        this.id = id;
        this.name = name;
        this.icon = icon;

        this.cost = cost;
        this.maxLevel = maxLevel;
        this.defaultLevel = defaultLevel;
        this.exclusive = exclusive;
    }

    public Upgrade addChild(Upgrade upgrade)
    {
        this.upgrades.add(upgrade);

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

    public ItemStack getIcon()
    {
        return icon;
    }

    public void setIcon(ItemStack icon)
    {
        this.icon = icon;
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

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {

    }

    @Override
    public void onToggleCooldown(Ability ability)
    {

    }
}
