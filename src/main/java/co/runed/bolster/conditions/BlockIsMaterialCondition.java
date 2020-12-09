package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BlockIsMaterialCondition extends TargetedCondition<Block>
{
    Collection<Material> materials = new ArrayList<>();

    public BlockIsMaterialCondition(Material material)
    {
        this(Collections.singletonList(material));
    }

    public BlockIsMaterialCondition(Tag<Material> tag)
    {
        this(tag.getValues());
    }

    public BlockIsMaterialCondition(Collection<Material> materials)
    {
        super(Target.BLOCK);

        this.materials.addAll(materials);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        Block block = this.getTarget().get(properties);

        if (block == null) return false;

        return this.materials.contains(block.getType());
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
