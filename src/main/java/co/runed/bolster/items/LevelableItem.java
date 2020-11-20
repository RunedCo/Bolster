package co.runed.bolster.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.LivingEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class LevelableItem extends Item
{
    int level = 1;
    boolean mergeLevels = true;
    ConfigurationSection levelConfig;

    @Override
    public void create(ConfigurationSection config)
    {
        this.mergeLevels = config.getBoolean("merge-levels", this.mergeLevels);

        // LOAD BASE
        // LOAD LEVEL SPECIFIC

        if (config.isList("levels"))
        {
            // TODO FIX
            List<LinkedHashMap> levels = (List<LinkedHashMap>) config.getList("levels");

            if (this.level <= levels.size())
            {
                Map allLevels = config.getValues(false);

                for (int i = 0; i < this.level; i++)
                {
                    allLevels.putAll(levels.get(i));
                }

                this.levelConfig = new MemoryConfiguration()
                        .createSection("level", allLevels);

                config = this.levelConfig;
            }
        }

        super.create(config);
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }

    @Override
    public void setEntity(LivingEntity entity)
    {
        this.setLevel(3);

        super.setEntity(entity);
    }
}
