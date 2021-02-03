package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.game.Team;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;

public class IsOnTeamCondition extends TargetedCondition<BolsterEntity>
{
    Team team;

    public IsOnTeamCondition(Target<BolsterEntity> target, Team team)
    {
        super(target);
        this.team = team;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        BolsterEntity entity = this.getTarget().get(properties);

        return this.team.isInTeam(entity.getBukkit());
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        if (inverted) return ChatColor.RED + "You must not be on the " + this.team.getName() + " to use this ability!";

        return ChatColor.RED + "You must be on the " + this.team.getName() + " to use this ability!";
    }
}
