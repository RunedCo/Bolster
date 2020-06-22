package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockIsMaterialCondition extends Condition {
    List<Material> materials = new ArrayList<>();

    public BlockIsMaterialCondition(Material material) {
        materials.add(material);
    }

    public BlockIsMaterialCondition(Tag<Material> tag) {
        this.materials.addAll(tag.getValues());
    }

    public BlockIsMaterialCondition(List<Material> materials) {
        this.materials.addAll(materials);
    }

    @Override
    public boolean evaluate(Ability ability, Properties properties) {
        Block block = properties.get(AbilityProperties.BLOCK);

        if (block == null) return false;

        return this.materials.contains(block.getType());
    }
}
