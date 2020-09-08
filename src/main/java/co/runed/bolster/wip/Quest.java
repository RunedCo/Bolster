package co.runed.bolster.wip;

import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.entity.Player;

import java.util.function.Function;

public abstract class Quest implements IRegisterable
{
    String id;

    public Quest(String id, String name, String description, Function<Player, Boolean> questSucceededFunction)
    {
        this.id = id;
    }

    public boolean isComplete(Player player)
    {
        return this.getProgress(player) >= 1;
    }

    public abstract float getProgress(Player player);

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
}
