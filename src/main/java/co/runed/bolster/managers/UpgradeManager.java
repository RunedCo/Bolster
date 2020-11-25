package co.runed.bolster.managers;

import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.util.Manager;
import co.runed.bolster.wip.upgrade.Upgrade;
import co.runed.bolster.wip.upgrade.UpgradeProvider;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class UpgradeManager extends Manager
{
    private static UpgradeManager _instance;

    private final Map<UUID, List<Data>> upgradeData = new HashMap<>();

    public UpgradeManager(Plugin plugin)
    {
        super(plugin);

        _instance = this;
    }

    public void addUpgrade(LivingEntity entity, Upgrade upgrade)
    {
        if (this.hasUpgrade(entity, upgrade)) return;

        UpgradeProvider provider = new UpgradeProvider();

        provider.setId(upgrade.getId() + "_provider");

        for (Upgrade.UpgradeAbilityData data : upgrade.getAbilities())
        {
            provider.addAbility(data.getTrigger(), data.getAbilitySupplier().get());
        }

        provider.setEntity(entity);

        Data data = new Data(upgrade, upgrade.getDefaultLevel(), provider);
        UUID uuid = entity.getUniqueId();

        if (!this.upgradeData.containsKey(uuid)) this.upgradeData.put(uuid, new ArrayList<>());
        this.upgradeData.get(uuid).add(data);
    }

    public void removeUpgrade(LivingEntity entity, Upgrade upgrade)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.upgradeData.containsKey(uuid)) return;

        for (Data data : this.upgradeData.get(uuid))
        {
            if (data.upgrade == upgrade)
            {
                data.provider.destroy();
                return;
            }
        }
    }

    public int getUpgradeLevel(LivingEntity entity, Upgrade upgrade)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.upgradeData.containsKey(uuid)) return 0;

        for (Data data : this.upgradeData.get(uuid))
        {
            if (data.upgrade == upgrade) return data.level;
        }

        return 0;
    }

    public void setUpgradeLevel(LivingEntity entity, Upgrade upgrade, int level)
    {
        this.addUpgrade(entity, upgrade);

        UUID uuid = entity.getUniqueId();

        if (!this.upgradeData.containsKey(uuid)) return;

        for (Data data : this.upgradeData.get(uuid))
        {
            if (data.upgrade == upgrade)
            {
                data.setLevel(level);
                return;
            }
        }
    }

    public boolean hasUpgrade(LivingEntity entity, Upgrade upgrade)
    {
        return this.hasUpgrade(entity, upgrade, 0);
    }

    public boolean hasUpgrade(LivingEntity entity, Upgrade upgrade, int minLevel)
    {
        return this.getUpgradeLevel(entity, upgrade) >= minLevel;
    }

    private static class Data
    {
        private final Upgrade upgrade;
        private int level;
        private final AbilityProvider provider;

        public Data(Upgrade upgrade, int level, AbilityProvider provider)
        {
            this.upgrade = upgrade;
            this.level = level;
            this.provider = provider;
        }

        public void setLevel(int level)
        {
            this.level = Math.max(level, upgrade.getMaxLevel());
        }
    }

    public static UpgradeManager getInstance()
    {
        return _instance;
    }
}
