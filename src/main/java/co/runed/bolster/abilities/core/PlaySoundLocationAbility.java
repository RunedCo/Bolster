package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.conditions.IsEntityTypeCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;

public class PlaySoundLocationAbility extends TargetedAbility<Location>
{
    Sound sound;
    String soundId = null;
    SoundCategory soundCategory;
    float volume;
    float pitch;

    public PlaySoundLocationAbility(Target<Location> target, Sound sound, SoundCategory soundCategory, float volume, float pitch)
    {
        this(target, soundCategory, volume, pitch);

        this.sound = sound;
    }

    public PlaySoundLocationAbility(Target<Location> target, String sound, SoundCategory soundCategory, float volume, float pitch)
    {
        this(target, soundCategory, volume, pitch);

        this.soundId = sound;
    }

    private PlaySoundLocationAbility(Target<Location> target, SoundCategory soundCategory, float volume, float pitch)
    {
        super(target);

        this.addCondition(new IsEntityTypeCondition(Target.CASTER, EntityType.PLAYER));

        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Location location =  this.getTarget().get(properties);

        if (this.soundId == null)
        {
            location.getWorld().playSound(location, this.sound, this.soundCategory, this.volume, this.pitch);
        }
        else
        {
            location.getWorld().playSound(location, this.soundId, this.soundCategory, this.volume, this.pitch);
        }
    }
}
