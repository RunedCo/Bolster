package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

public class KnockbackResistanceStatusEffect extends StatusEffect {
    private AttributeModifier attributeModifier;

    private double resistanceAmount = 1;

    public KnockbackResistanceStatusEffect(double duration, double resistanceAmount) {
        super(duration);

        this.resistanceAmount = resistanceAmount;
    }

    @Override
    public String getName() {
        return "Knockback Resistance";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public void onStart() {
        this.attributeModifier = new AttributeModifier("knockback_resistance_status", this.resistanceAmount, AttributeModifier.Operation.ADD_NUMBER);

        var attr = this.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);

        if (attr == null) return;

        attr.addModifier(attributeModifier);
    }

    @Override
    public void onEnd() {
        var attr = this.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);

        if (attr == null) return;

        attr.removeModifier(attributeModifier);
    }

    @Override
    public void onTick() {

    }
}
