package co.runed.bolster.classes;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.core.CancelEventAbility;
import co.runed.bolster.util.properties.Properties;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class TargetDummyClass extends BolsterClass
{
    public TargetDummyClass()
    {
        super(ChatColor.WHITE + "Target Dummy");

        this.addAbility(AbilityTrigger.ON_TAKE_DAMAGE, new TargetDummyAbility());
        this.addAbility(AbilityTrigger.ON_INTERACTED_WITH, new CancelEventAbility());

        this.setIcon(new ItemStack(Material.ARMOR_STAND));
    }

    public static class TargetDummyAbility extends Ability
    {
        private static final DecimalFormat decimalFormatter = new DecimalFormat("#.##");

        public TargetDummyAbility()
        {
            this.setShouldCancelEvent(true);
        }

        @Override
        public void onActivate(Properties properties)
        {
            if(properties.contains(AbilityProperties.DAMAGER) && properties.get(AbilityProperties.DAMAGER) instanceof Player)
            {
                Player damager = (Player) properties.get(AbilityProperties.DAMAGER);
                String damageStr = decimalFormatter.format(properties.get(AbilityProperties.DAMAGE));

                damager.sendMessage("You did " + ChatColor.RED + damageStr + ChatColor.WHITE + " damage!");
            }
        }
    }
}
