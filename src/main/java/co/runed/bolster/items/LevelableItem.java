package co.runed.bolster.items;

import co.runed.bolster.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public abstract class LevelableItem extends Item
{
    int level = 1;
    boolean mergeLevels = true;

    ConfigurationSection currentLevelConfig;
    HashMap<Integer, ConfigurationSection> levels = new HashMap<>();
    HashMap<Integer, ConfigurationSection> unmergedLevels = new HashMap<>();

    @Override
    public List<String> getLore()
    {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "Level " + this.getLevel());

        lore.addAll(super.getLore());

        return lore;
    }

    @Override
    public void setConfig(ConfigurationSection config)
    {
        super.setConfig(config);

        this.mergeLevels = config.getBoolean("merge-levels", this.mergeLevels);

        // LOAD BASE
        // LOAD LEVEL SPECIFIC

        if (config.isList("levels"))
        {
            // TODO FIX
            List<LinkedHashMap<String, Object>> levels = (List<LinkedHashMap<String, Object>>) config.getList("levels");

            if (levels == null) return;

            Map<String, Object> allLevels = config.getValues(false);

            for (int i = 0; i < levels.size(); i++)
            {
                allLevels.putAll(levels.get(i));

                this.unmergedLevels.put(i + 1, ConfigUtil.fromMap(levels.get(i)));
                this.levels.put(i + 1, ConfigUtil.fromMap(allLevels));
            }
        }

        // get + process milestones from here
    }

    @Override
    public void create(ConfigurationSection config)
    {
        HashMap<Integer, ConfigurationSection> mapToUse = this.levels;

        if (!this.mergeLevels) mapToUse = this.unmergedLevels;

        ConfigurationSection cumulativeLevelConfig = mapToUse.get(Math.max(1, Math.min(this.getLevel(), mapToUse.size())));

        ConfigUtil.merge(config, cumulativeLevelConfig);

        super.create(config);
    }

    public void setLevel(int level)
    {
        int previousLevel = this.level;

        level = Math.max(0, Math.min(level, this.levels.size()));

        this.level = level;

        if (this.level != previousLevel) this.markDirty();
    }

    public int getLevel()
    {
        return level;
    }

    public List<String> getMilestones()
    {
        return new ArrayList<>();
    }
}
