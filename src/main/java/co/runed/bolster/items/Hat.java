package co.runed.bolster.items;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Hat extends Item
{
    public static final ItemStack HAT_BASE = new ItemStack(Material.MINECART);

    @Override
    public void create(ConfigurationSection config)
    {
        super.create(config);

        this.addAbility(AbilityTrigger.RIGHT_CLICK, new EquipHatAbility());
        this.addCategory(Category.HATS);
    }

    public static class EquipHatAbility extends Ability
    {
        @Override
        public void onActivate(Properties properties)
        {
            BolsterEntity caster = properties.get(AbilityProperties.CASTER);
            ItemStack item = properties.get(AbilityProperties.ITEM_STACK);

            EntityEquipment equipment = caster.getEquipment();

            ItemStack invItem = equipment.getItem(EquipmentSlot.HEAD);

            if (invItem.getType() == Material.AIR)
            {
                equipment.setHelmet(item);
                item.setAmount(0);
            }
        }
    }
}
