package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.properties.Properties;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.entity.EntityType;

public class DisguiseAbility extends Ability
{
    Disguise disguise;

    public DisguiseAbility(EntityType type)
    {
        this(new MobDisguise(DisguiseType.getType(type)));
    }

    public DisguiseAbility(Disguise disguise)
    {
        super();

        this.disguise = disguise;
    }

    public Disguise getDisguise()
    {
        return this.disguise;
    }

    @Override
    public void onActivate(Properties properties)
    {
        DisguiseAPI.disguiseEntity(properties.get(AbilityProperties.CASTER), this.disguise);
    }
}
