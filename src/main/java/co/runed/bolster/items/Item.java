package co.runed.bolster.items;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.conditions.ItemEquippedCondition;
import co.runed.bolster.abilities.conditions.ItemStackIsItemCondition;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public abstract class Item extends AbilityProvider implements IRegisterable
{
    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(Bolster.getInstance(), "item-id");
    public static final NamespacedKey ITEM_SKIN_KEY = new NamespacedKey(Bolster.getInstance(), "item-skin");
    public static final NamespacedKey ITEM_OWNER_KEY = new NamespacedKey(Bolster.getInstance(), "item-owner");

    private String id;
    private String name;
    private List<String> lore = new ArrayList<>();
    private ItemStack itemStack = new ItemStack(Material.STICK);

    private ItemSkin skin;
    private final List<Category> categories = new ArrayList<>(Collections.singletonList(Category.ALL));

    private final Map<Ability, Boolean> abilityCooldowns = new HashMap<>();

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

    public String getName()
    {
        return this.name + ChatColor.WHITE;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addLore(String lore)
    {
        this.lore.addAll(StringUtil.formatLore(lore));
    }

    public void addLore(List<String> lore)
    {
        this.lore.addAll(lore);
    }

    public List<String> getLore()
    {
        List<String> loreWithAbilities = new ArrayList<>();

        for (AbilityData abilityData : this.getAbilities())
        {
            Ability ability = abilityData.ability;

            if (ability.getDescription() == null) continue;

            loreWithAbilities.addAll(StringUtil.formatLore(ChatColor.RED + abilityData.trigger.getDisplayName() + ": " + ChatColor.YELLOW + ability.getDescription()));
        }

        if (loreWithAbilities.size() > 0 && this.lore.size() > 0)
        {
            loreWithAbilities.add("");
        }

        loreWithAbilities.addAll(this.lore);

        return loreWithAbilities;
    }

    protected ItemStack getItemStack()
    {
        return this.itemStack.clone();
    }

    protected void setItemStack(ItemBuilder builder)
    {
        this.itemStack = builder.build();
    }

    protected void setItemStack(ItemStack stack)
    {
        this.itemStack = stack;
    }

    public boolean hasSkin()
    {
        return this.getSkin() != null;
    }

    public ItemSkin getSkin()
    {
        return this.skin;
    }

    public void setSkin(ItemSkin skin)
    {
        this.skin = skin;
    }

    public List<Category> getCategories()
    {
        return this.categories;
    }

    public void addCategory(Category category)
    {
        if (this.categories.contains(category)) return;

        this.categories.add(category);
    }

    public void addAbility(AbilityTrigger trigger, Ability ability, boolean showCooldown)
    {
        this.abilityCooldowns.put(ability, showCooldown);

        this.addAbility(trigger, ability);
    }

    @Override
    public void addAbility(AbilityTrigger trigger, Ability ability)
    {
        if (trigger == AbilityTrigger.ON_SELECT_ITEM || trigger == AbilityTrigger.ON_DESELECT_ITEM)
        {
            ability.addCondition(new ItemStackIsItemCondition(this.getClass()));
        }
        else
        {
            ability.addCondition(new ItemEquippedCondition(Target.CASTER, EnumSet.allOf(EquipmentSlot.class), this.getClass()));
        }

        super.addAbility(trigger, ability);
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {
        Optional<AbilityData> filtered = this.getAbilities().stream().filter((info) -> info.ability == ability).findFirst();

        if (!filtered.isPresent()) return;

        if (this.abilityCooldowns.containsKey(ability) && this.abilityCooldowns.get(ability) && success)
        {
            ((Player) this.getOwner()).setCooldown(this.getItemStack().getType(), (int) (ability.getCooldown() * 20));
        }
    }

    public ItemStack toItemStack()
    {
        ItemBuilder builder = new ItemBuilder(this.getItemStack())
                .setDisplayName(this.getName())
                .setLore(this.getLore())
                .setPersistentData(Item.ITEM_ID_KEY, PersistentDataType.STRING, this.getId());


        if (this.hasSkin())
        {
            builder.setCustomModelData(this.getSkin().getCustomModelData())
                    .setPersistentData(Item.ITEM_SKIN_KEY, PersistentDataType.STRING, this.getSkin().getId());
        }

        if (this.getOwner() != null)
        {
            builder.setPersistentData(Item.ITEM_OWNER_KEY, PersistentDataType.STRING, this.getOwner().getUniqueId().toString());
        }

        return builder.build();
    }

    public void destroy()
    {
        for (AbilityData abilityData : this.getAbilities())
        {
            Bolster.getAbilityManager().remove(this.getOwner(), abilityData.ability);
        }

        this.getAbilities().clear();
        this.abilityCooldowns.clear();
    }
}
