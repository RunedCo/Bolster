package co.runed.bolster.classes;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public abstract class BolsterClass extends AbilityProvider
{
    String id;
    String name = null;
    ItemStack icon = new ItemStack(Material.PLAYER_HEAD);

    public String getName()
    {
        if (name == null) return this.getId();

        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ItemStack getIcon()
    {
        return icon;
    }

    public void setIcon(ItemStack icon)
    {
        this.icon = icon;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void setOwner(LivingEntity owner)
    {
        super.setOwner(owner);

        // TODO: MOVE OUTSIDE OF CLASS SPECIFIC IMPLEMENTATION
        Bolster.getAbilityManager().trigger(owner, this, AbilityTrigger.BECOME, new Properties());
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {

    }

    @Override
    public void onToggleCooldown(Ability ability)
    {

    }
}
