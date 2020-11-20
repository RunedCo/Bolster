package co.runed.bolster.classes;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.core.CancelEventAbility;
import co.runed.bolster.util.properties.Properties;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;

public class TargetDummyClass extends BolsterClass
{
    @Override
    public void create(ConfigurationSection config)
    {
        super.create(config);

        this.setName(ChatColor.WHITE + "Target Dummy");

        this.addAbility(AbilityTrigger.ON_TAKE_DAMAGE, new TargetDummyAbility());
        this.addAbility(AbilityTrigger.ON_INTERACTED_WITH, new CancelEventAbility());

        this.setIcon(new ItemStack(Material.ARMOR_STAND));
    }

    public static void summon(Location location)
    {
        ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        entity.setCustomNameVisible(true);
        entity.setCustomName("Target Dummy");

        entity.setArms(true);

        entity.setRightArmPose(new EulerAngle(-45, 0, 0));
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
        entity.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));

        BolsterEntity.from(entity).setBolsterClass(new TargetDummyClass());
    }

    public static class TargetDummyAbility extends Ability
    {
        private static final DecimalFormat decimalFormatter = new DecimalFormat("#.##");

        Instant startInstant;
        double totalDamage = 0;

        @Override
        public void onActivate(Properties properties)
        {
            Event event = properties.get(AbilityProperties.EVENT);

            if (event instanceof EntityDamageEvent)
            {
                EntityDamageEvent damageEvent = (EntityDamageEvent) event;

                damageEvent.setDamage(0);
            }

            if (properties.contains(AbilityProperties.DAMAGER) && properties.get(AbilityProperties.DAMAGER) instanceof Player)
            {
                if (this.startInstant == null || this.startInstant.plusSeconds(10).isBefore(Instant.now()))
                {
                    this.startInstant = Instant.now();
                    this.totalDamage = 0;
                }

                Player damager = (Player) properties.get(AbilityProperties.DAMAGER);
                double damage = properties.get(AbilityProperties.DAMAGE);

                this.totalDamage += damage;

                long totalSeconds = Math.max(1, Duration.between(this.startInstant, Instant.now()).getSeconds());
                double dps = this.totalDamage / totalSeconds;

                String damageStr = decimalFormatter.format(damage);
                String dpsString = decimalFormatter.format(dps);

                damager.sendMessage("You did " + ChatColor.RED + damageStr + ChatColor.WHITE + " damage! (" + dpsString + " damage per second)");
            }
        }
    }
}
