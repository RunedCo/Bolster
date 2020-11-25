package co.runed.bolster.wip;

import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Quest implements IRegisterable
{
    String id;
    String name;
    String description;
    Function<Player, Boolean> questSucceededFunction;
    Function<Player, Float> questProgressFunc;
    QuestType questType = QuestType.ONE_OFF;

    private static DecimalFormat PERCENT_DECIMAL_FORMAT = new DecimalFormat("#%");

    public Quest(String id)
    {
        this.id = id;
    }

    public Quest setName(String name)
    {
        this.name = name;

        return this;
    }

    public Quest setDescription(String description)
    {
        this.description = description;

        return this;
    }

    public Quest onCompleted(Function<Player, Boolean> func)
    {
        this.questSucceededFunction = func;

        return this;
    }

    public Quest onGetProgress(Function<Player, Float> func)
    {
        this.questProgressFunc = func;

        return this;
    }

    public Quest setType(QuestType frequency)
    {
        this.questType = frequency;

        return this;
    }

    public float getProgress(Player player)
    {
        if (this.questProgressFunc == null) return 0.0f;

        return this.questProgressFunc.apply(player);
    }

    public boolean isComplete(Player player)
    {
        return this.getProgress(player) >= 1;
    }

    public void complete(Player player)
    {
        if (this.isComplete(player) && this.questSucceededFunction != null)
        {
            this.questSucceededFunction.apply(player);
        }
    }

    public List<String> getItemTooltip(Player player)
    {
        List<String> tooltip = new ArrayList<>();

        tooltip.addAll(StringUtil.formatLore(ChatColor.WHITE + this.description));

        tooltip.add("");
        tooltip.add(ChatColor.WHITE + "" + ChatColor.BOLD + this.questType.displayName.toUpperCase());
        if (this.isComplete(player))
        {
            tooltip.add(ChatColor.GREEN + "" + ChatColor.BOLD + "COMPLETED");
        }
        else
        {
            tooltip.add(ChatColor.WHITE + PERCENT_DECIMAL_FORMAT.format(this.getProgress(player)) + " completed!");
        }

        return tooltip;
    }

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
    public String getDescription()
    {
        return this.getDescription();
    }

    @Override
    public void create(ConfigurationSection config)
    {

    }

}
