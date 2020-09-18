package co.runed.bolster.wip;

import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class Quest implements IRegisterable
{
    String id;
    String name;
    Function<Player, Boolean> questSucceededFunction;
    Function<Player, Float> questProgressFunc;
    QuestType frequency = QuestType.ONE_OFF;

    public Quest(String id)
    {
        this.id = id;
    }

    public Quest setName(String name)
    {
        this.name = name;

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
        this.frequency = frequency;

        return this;
    }

    public boolean isComplete(Player player)
    {
        return this.questProgressFunc.apply(player) >= 1;
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

}
