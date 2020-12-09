package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;

public class KnockbackResistanceStatusEffect extends StatusEffect
{
    private AttributeModifier attributeModifier;

    private double resistanceAmount = 1;

    public KnockbackResistanceStatusEffect(double duration, double resistanceAmount)
    {
        super(duration);

        this.resistanceAmount = resistanceAmount;
    }

    @Override
    public String getName()
    {
        return "Knockback Resistance";
    }

    @Override
    public ChatColor getColor()
    {
        return ChatColor.GOLD;
    }

    @Override
    public Collection<PotionEffectType> getPotionEffects()
    {
        return new ArrayList<>();
    }

    @Override
    public void onStart()
    {
        this.attributeModifier = new AttributeModifier("knockback_resistance_status", this.resistanceAmount, AttributeModifier.Operation.ADD_NUMBER);

        AttributeInstance attr = this.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);

        if (attr == null) return;

        attr.addModifier(attributeModifier);
    }

    @Override
    public void onEnd()
    {
        AttributeInstance attr = this.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);

        if (attr == null) return;

        attr.removeModifier(attributeModifier);
    }

    @Override
    public void onTick()
    {

    }
}