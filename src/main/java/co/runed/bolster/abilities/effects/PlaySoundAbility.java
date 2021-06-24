package co.runed.bolster.abilities.effects;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.conditions.IsEntityTypeCondition;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PlaySoundAbility extends TargetedAbility<BolsterEntity>
{
    Target<Location> locationTarget;
    Sound sound;
    String soundId = null;
    SoundCategory soundCategory;
    float volume;
    float pitch;

    public PlaySoundAbility(Target<BolsterEntity> target, Target<Location> locationTarget, Sound sound, SoundCategory soundCategory, float volume, float pitch)
    {
        this(target, locationTarget, soundCategory, volume, pitch);

        this.sound = sound;
    }

    public PlaySoundAbility(Target<BolsterEntity> target, Target<Location> locationTarget, String sound, SoundCategory soundCategory, float volume, float pitch)
    {
        this(target, locationTarget, soundCategory, volume, pitch);

        this.soundId = sound;
    }

    private PlaySoundAbility(Target<BolsterEntity> target, Target<Location> locationTarget, SoundCategory soundCategory, float volume, float pitch)
    {
        super(target);

        this.addCondition(new IsEntityTypeCondition(Target.CASTER, EntityType.PLAYER));

        this.locationTarget = locationTarget;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player player = (Player) this.getTarget().get(properties).getBukkit();

        if (this.soundId == null)
        {
            player.playSound(this.locationTarget.get(properties), this.sound, this.soundCategory, this.volume, this.pitch);
        }
        else
        {
            player.playSound(this.locationTarget.get(properties), this.soundId, this.soundCategory, this.volume, this.pitch);
        }
    }
}
