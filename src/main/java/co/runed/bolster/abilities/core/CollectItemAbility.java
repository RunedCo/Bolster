package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.effects.PlaySoundAbility;
import co.runed.bolster.conditions.BlockIsMaterialCondition;
import co.runed.bolster.conditions.IsEntityTypeCondition;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class CollectItemAbility extends Ability
{
    Collection<Material> materials = new ArrayList<>();
    Definition<Item> itemDefinition;
    int outputCount = 1;

    public CollectItemAbility(Collection<Material> materials, Definition<Item> itemDefinition, int outputCount)
    {
        super();

        this.itemDefinition = itemDefinition;
        this.materials = materials;
        this.outputCount = outputCount;

        // SET DEFAULT COOLDOWN
        this.setCooldown(10);

        this.addCondition(new IsEntityTypeCondition(Target.CASTER, EntityType.PLAYER));
        this.addCondition(new BlockIsMaterialCondition(this.materials));

        this.addAbility(new PlaySoundAbility(Target.CASTER, Target.CASTER_LOCATION, "activatebow", SoundCategory.BLOCKS, 0.5f, 1.5f));
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player player = (Player) properties.get(AbilityProperties.CASTER).getBukkit();

        ItemManager.getInstance().giveItem(player, player.getInventory(), itemDefinition, outputCount);
    }
}
