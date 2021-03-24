package co.runed.bolster.abilities.base;

import co.runed.bolster.game.Team;
import org.bukkit.entity.Entity;

import java.util.stream.Collectors;

/**
 * A MultiTargetAbility that gets entities on a specific team
 */
public class TeamTargetAbility extends MultiTargetAbility
{
    Team team;

    public TeamTargetAbility(Team team)
    {
        super((props) -> team.getMembers().stream().map(e -> (Entity) e).collect(Collectors.toList()));

        this.team = team;
    }
}