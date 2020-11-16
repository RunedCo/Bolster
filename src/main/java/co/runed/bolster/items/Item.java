package co.runed.bolster.items;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.conditions.ItemEquippedCondition;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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

    private static final UUID attackDamageUuid = new UUID(1234, 1234);
    private static final UUID attackSpeedUuid = new UUID(1235, 1235);
    private static final UUID knockbackResistanceUuid = new UUID(1236, 1236);
    private static final UUID knockBackUuid = new UUID(1237, 1237);
    private static final UUID powerUuid = new UUID(1238, 1238);

    private String id;
    private String name;
    private List<String> lore = new ArrayList<>();
    private ItemStack itemStack = new ItemStack(Material.STICK);
    private boolean droppable = true;

    // attributes
    double attackDamage = 0;
    double attackSpeed = 0;
    double knockBackResistance = 0;
    double knockBack = 0;
    double power = 0;

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
        return (this.hasSkin() && this.getSkin().shouldShowName() ? this.getSkin().getName() : this.name) + ChatColor.WHITE;
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

        if (this.attackDamage > 0)
        {
            loreWithAbilities.add(ChatColor.GRAY + "Attack Damage: " + ChatColor.YELLOW + this.attackDamage);
        }

        if (this.power > 0)
        {
            loreWithAbilities.add(ChatColor.GRAY + "Power: " + ChatColor.YELLOW + this.power);
        }

        if (this.attackSpeed > 0)
        {
            loreWithAbilities.add(ChatColor.GRAY + "Attack Speed: " + ChatColor.YELLOW + this.attackSpeed);
        }

        if (this.knockBack > 0)
        {
            loreWithAbilities.add(ChatColor.GRAY + "Knockback: " + ChatColor.YELLOW + this.knockBack);
        }

        if (this.knockBackResistance > 0)
        {
            loreWithAbilities.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.YELLOW + this.knockBackResistance);
        }

        String desc = super.getDescription();

        if (((desc != null && !desc.isEmpty()) || this.lore.size() > 0) && loreWithAbilities.size() > 0)
        {
            loreWithAbilities.add("");
        }

        if (desc != null && !desc.isEmpty())
        {
            loreWithAbilities.addAll(StringUtil.formatLore(desc));
        }

        if (loreWithAbilities.size() > 0 && this.lore.size() > 0)
        {
            loreWithAbilities.add("");
        }

        loreWithAbilities.addAll(this.lore);

        return loreWithAbilities;
    }

    @Override
    public String getDescription()
    {
        if (this.getLore().size() <= 0) return "";

        return StringUtil.join("\n", this.getLore());
    }

    @Override
    public ItemStack getIcon()
    {
        return this.toItemStack();
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

    public void setAttackDamage(double attackDamage)
    {
        this.attackDamage = attackDamage;
    }

    public double getAttackDamage()
    {
        return attackDamage;
    }

    public void setAttackSpeed(double attackSpeed)
    {
        this.attackSpeed = attackSpeed;
    }

    public double getAttackSpeed()
    {
        return attackSpeed;
    }

    public void setPower(double power)
    {
        this.power = power;
    }

    public double getPower()
    {
        return power;
    }

    public void setKnockBackResistance(double knockBackResistance)
    {
        this.knockBackResistance = knockBackResistance;
    }

    public double getKnockBackResistance()
    {
        return knockBackResistance;
    }

    public void setDroppable(boolean droppable)
    {
        this.droppable = droppable;
    }

    public boolean isDroppable()
    {
        return droppable;
    }

    @Override
    public List<Category> getCategories()
    {
        return this.categories;
    }

    @Override
    public void addCategory(Category category)
    {
        if (this.categories.contains(category)) return;

        this.categories.add(category);
    }

    public void addAbility(AbilityTrigger trigger, Ability ability, boolean showCooldown)
    {
        this.addAbility(trigger, ability, showCooldown, true);
    }

    @Override
    public void addAbility(AbilityTrigger trigger, Ability ability)
    {
        this.addAbility(trigger, ability, false, true);
    }

    public void addAbility(AbilityTrigger trigger, Ability ability, boolean showCooldown, boolean addDefaultConditions)
    {
        if (showCooldown) this.abilityCooldowns.put(ability, showCooldown);

        /* if (trigger == AbilityTrigger.ON_SELECT_ITEM || trigger == AbilityTrigger.ON_DESELECT_ITEM)
        {
            ability.addCondition(new ItemStackIsItemCondition(this.getClass()));
        }
        else
        {

        }*/

        if (addDefaultConditions)
        {
            ability.addCondition(new ItemEquippedCondition(Target.CASTER, EnumSet.allOf(EquipmentSlot.class), this.getClass()));
        }

        super.addAbility(trigger, ability);

        if (this.getEntity() != null && this.getEntity() instanceof Player)
        {
            ItemManager.getInstance().rebuildItemStack((Player) this.getEntity(), this.getId());
        }
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {
        Optional<AbilityData> filtered = this.getAbilities().stream().filter((info) -> info.ability == ability).findFirst();

        if (!filtered.isPresent()) return;

        if (this.abilityCooldowns.containsKey(ability) && this.abilityCooldowns.get(ability) && success)
        {
            ((Player) this.getEntity()).setCooldown(this.getItemStack().getType(), (int) (ability.getCooldown() * 20));
        }

        // TODO CHECK PERFORMANCE IMPACT OF THIS ESPECIALLY FOR TICKING ABILITIES
        if (this.getEntity() instanceof Player && this.getId() != null)// && this.isDirty())
        {
            ItemManager.getInstance().rebuildItemStack((Player) this.getEntity(), this.getId());
        }
    }

    @Override
    public void onToggleCooldown(Ability ability)
    {

    }

    public ItemStack toItemStack()
    {
        ItemBuilder builder = new ItemBuilder(this.getItemStack())
                .setDisplayName(this.getName())
                .setLore(this.getLore())
                .setPersistentData(Item.ITEM_ID_KEY, PersistentDataType.STRING, this.getId());

        if (this.attackDamage > 1)
            builder.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(attackDamageUuid, "attack_damage", this.attackDamage - 1, AttributeModifier.Operation.ADD_NUMBER));
        if (this.attackSpeed > 1)
            builder.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(attackSpeedUuid, "attack_speed", this.attackSpeed - 1, AttributeModifier.Operation.ADD_NUMBER));
        if (this.knockBackResistance > 1)
            builder.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(knockbackResistanceUuid, "knockback_resistance", this.knockBackResistance, AttributeModifier.Operation.ADD_NUMBER));
        if (this.knockBack > 1)
            builder.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, new AttributeModifier(knockBackUuid, "knockback", this.knockBack, AttributeModifier.Operation.ADD_NUMBER));

        if (this.hasSkin())
        {
            ItemSkin skin = this.getSkin();

            builder.setCustomModelData(skin.getCustomModelData())
                    .setPersistentData(Item.ITEM_SKIN_KEY, PersistentDataType.STRING, skin.getId());
        }

        if (this.getEntity() != null)
        {
            builder.setPersistentData(Item.ITEM_OWNER_KEY, PersistentDataType.STRING, this.getEntity().getUniqueId().toString());
        }

        return builder.build();
    }

    @Override
    public void destroy()
    {
        super.destroy();

        this.abilityCooldowns.clear();
    }
}
