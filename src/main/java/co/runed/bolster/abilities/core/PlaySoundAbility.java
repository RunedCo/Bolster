package co.runed.bolster.abilities.core;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.conditions.IsEntityTypeCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.target.Target;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PlaySoundAbility extends TargetedAbility<BolsterEntity>
{
    Sound sound;
    String soundId = null;
    SoundCategory soundCategory;
    float volume;
    float pitch;

    public PlaySoundAbility(Target<BolsterEntity> target, Sound sound, SoundCategory soundCategory, float volume, float pitch)
    {
        this(target, soundCategory, volume, pitch);

        this.sound = sound;
    }

    public PlaySoundAbility(Target<BolsterEntity> target, String sound, SoundCategory soundCategory, float volume, float pitch)
    {
        this(target, soundCategory, volume, pitch);

        this.soundId = sound;
    }

    private PlaySoundAbility(Target<BolsterEntity> target, SoundCategory soundCategory, float volume, float pitch)
    {
        super(target);

        this.addCondition(new IsEntityTypeCondition(EntityType.PLAYER));

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
            player.playSound(player.getLocation(), this.sound, this.soundCategory, this.volume, this.pitch);
        }
        else
        {
            player.playSound(player.getLocation(), this.soundId, this.soundCategory, this.volume, this.pitch);
        }
    }
}
