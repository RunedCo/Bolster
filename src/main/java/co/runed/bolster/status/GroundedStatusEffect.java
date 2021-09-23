package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;

public class GroundedStatusEffect extends StatusEffect {
    public GroundedStatusEffect(double duration) {
        super(duration);
    }


    @Override
    public ChatColor getColor() {
        return ChatColor.GRAY;
    }

    @Override
    public void onStart() {
        this.addPotionEffect(PotionEffectType.JUMP, 128, true, false, false);
    }

    @Override
    public void onEnd() {
        this.getEntity().removePotionEffect(PotionEffectType.JUMP);
    }

    @Override
    public void onTick() {

    }
}
