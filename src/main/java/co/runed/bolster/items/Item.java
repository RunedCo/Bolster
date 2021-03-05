package co.runed.bolster.items;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityProviderType;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.conditions.ConditionPriority;
import co.runed.bolster.conditions.HasItemCondition;
import co.runed.bolster.conditions.ItemEquippedCondition;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.target.Target;
import co.runed.bolster.util.traits.Traits;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public abstract class Item extends AbilityProvider implements IRegisterable
{
    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(Bolster.getInstance(), "item-id");
    public static final NamespacedKey ITEM_SKIN_KEY = new NamespacedKey(Bolster.getInstance(), "item-skin");
    public static final NamespacedKey ITEM_OWNER_KEY = new NamespacedKey(Bolster.getInstance(), "item-owner");

    public static final String ATTACK_DAMAGE_KEY = "attack-damage";
    public static final String ATTACK_SPEED_KEY = "attack-speed";
    public static final String KNOCKBACK_RESISTANCE_KEY = "knockback-resistance";
    public static final String KNOCKBACK_KEY = "knockback";
    public static final String POWER_KEY = "power";
    public static final String DROPPABLE_KEY = "droppable";
    public static final String HEALTH_KEY = "health";

    private static final UUID attackDamageUuid = new UUID(1234, 1234);
    private static final UUID attackSpeedUuid = new UUID(1235, 1235);
    private static final UUID knockbackResistanceUuid = new UUID(1236, 1236);
    private static final UUID knockBackUuid = new UUID(1237, 1237);
    private static final UUID powerUuid = new UUID(1238, 1238);
    private static final UUID healthUuid = new UUID(1239, 1239);


    private List<String> lore = new ArrayList<>();
    private ItemStack itemStack = new ItemStack(Material.STICK);
    private boolean droppable = true;
    private boolean clearOnRemove = true;

    // attributes
    double attackDamage = 0;
    double attackSpeed = 0;
    double knockBackResistance = 0;
    double knockBack = 0;
    double power = 0;
    double health = 0;

    private ItemSkin skin;
    private final List<Category> categories = new ArrayList<>(Collections.singletonList(Category.ALL));

    private final Map<Ability, Boolean> abilityCooldowns = new HashMap<>();

    @Override
    public void create(ConfigurationSection config)
    {
        super.create(config);

        this.setName(ChatColor.translateAlternateColorCodes('&', config.getString("name", "")));
        // TODO SET ITEM STACK this.setItemStack();

        this.setAttackDamage(config.getDouble(ATTACK_DAMAGE_KEY, 0));
        this.setAttackSpeed(config.getDouble(ATTACK_SPEED_KEY, 0));
        this.setKnockBackResistance(config.getDouble(KNOCKBACK_RESISTANCE_KEY, 0));
        this.setKnockBack(config.getDouble(KNOCKBACK_KEY, 0));
        this.setPower(config.getDouble(POWER_KEY, 0));
        this.setHealth(config.getDouble(HEALTH_KEY, 0));
        this.setDroppable(config.getBoolean(DROPPABLE_KEY, true));
    }

    @Override
    public AbilityProviderType getType()
    {
        return AbilityProviderType.ITEM;
    }

    @Override
    public void onEnable()
    {
        this.setTrait(Traits.MAX_HEALTH, this.getHealth());

        super.onEnable();
    }

    @Override
    public String getId()
    {
        return Registries.ITEMS.getId(this);
    }

    @Override
    public String getName()
    {
        return (this.hasSkin() && this.getSkin().shouldShowName() ? this.getSkin().getName() : super.getName()) + ChatColor.WHITE;
    }

    public void setLore(String lore)
    {
        this.setLore(StringUtil.formatLore(lore));
    }

    public void setLore(List<String> lore)
    {
        this.lore = new ArrayList<>(lore);
    }

    public void addLore(String lore)
    {
        this.addLore(StringUtil.formatLore(lore));
    }

    public void addLore(List<String> lore)
    {
        this.lore.addAll(lore);
    }

    public List<String> getStatsLore()
    {
        List<String> out = new ArrayList<>();

        if (this.attackDamage > 0)
        {
            out.add(ChatColor.GRAY + "Attack Damage: " + ChatColor.AQUA + this.attackDamage);
        }

        if (this.power > 0)
        {
            out.add(ChatColor.GRAY + "Power: " + ChatColor.AQUA + this.power);
        }

        if (this.knockBack > 0)
        {
            out.add(ChatColor.GRAY + "Knockback: " + ChatColor.AQUA + this.knockBack);
        }

        if (this.knockBackResistance > 0)
        {
            out.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.AQUA + this.knockBackResistance);
        }

        if (this.health > 0)
        {
            out.add(ChatColor.GRAY + "Health: " + ChatColor.AQUA + this.health);
        }

        return out;
    }

    public List<String> getLore()
    {
        List<String> loreWithAbilities = new ArrayList<>();

        loreWithAbilities.addAll(this.getStatsLore());

        String abilityDescriptions = super.getDescription();

        if (((abilityDescriptions != null && !abilityDescriptions.isEmpty()) || this.lore.size() > 0) && loreWithAbilities.size() > 0)
        {
            loreWithAbilities.add("");
        }

        if (abilityDescriptions != null && !abilityDescriptions.isEmpty())
        {
            loreWithAbilities.addAll(StringUtil.formatLore(abilityDescriptions));
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

    public void setKnockBack(double knockBack)
    {
        this.knockBack = knockBack;
    }

    public double getKnockBack()
    {
        return knockBack;
    }

    public double getHealth()
    {
        return health;
    }

    public void setHealth(double health)
    {
        this.health = health;
        this.setTrait(Traits.MAX_HEALTH, health);
    }

    public void setDroppable(boolean droppable)
    {
        this.droppable = droppable;
    }

    public boolean isDroppable()
    {
        return droppable;
    }

    public void setClearOnRemove(boolean clearOnRemove)
    {
        this.clearOnRemove = clearOnRemove;
    }

    public boolean shouldClearOnRemove()
    {
        return clearOnRemove;
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

    public void addAbility(AbilityTrigger trigger, Ability ability, boolean showCooldown, boolean mustBeActive, boolean mustBeInInventory)
    {
        if (showCooldown) this.abilityCooldowns.put(ability, showCooldown);

        if (mustBeActive)
        {
            ability.addCondition(new ItemEquippedCondition(Target.CASTER, EnumSet.allOf(EquipmentSlot.class), this.getClass()), ConditionPriority.HIGHEST);
        }

        // todo make sure hasitemcondition doesn't break anything or lag server
        // only run mustBeInInventory if not already running active (if an item is active it is always in inventory)
        if (mustBeInInventory && !mustBeActive)
        {
            ability.addCondition(new HasItemCondition(Target.CASTER, this.getClass(), 1), ConditionPriority.HIGHEST);
        }

        super.addAbility(trigger, ability);
    }

    public void addAbility(AbilityTrigger trigger, Ability ability, boolean showCooldown, boolean mustBeActive)
    {
        this.addAbility(trigger, ability, showCooldown, mustBeActive, true);
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

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {
//        Optional<AbilityData> filtered = this.getAbilities().stream().filter((info) -> info.ability == ability).findFirst();
//
//        if (!filtered.isPresent()) return;

        // TODO CHECK PERFORMANCE IMPACT OF THIS ESPECIALLY FOR TICKING ABILITIES
        if (this.getEntity() instanceof Player && this.getId() != null)// && this.isDirty())
        {
            ItemManager.getInstance().rebuildItemStack((Player) this.getEntity(), this.getId());
        }
    }

    @Override
    public void onToggleCooldown(Ability ability)
    {
        if (this.abilityCooldowns.containsKey(ability) && this.abilityCooldowns.get(ability) && this.getEntity() instanceof Player)
        {
            Player player = (Player) this.getEntity();

            if (ability.isOnCooldown())
            {
                player.setCooldown(this.getItemStack().getType(), (int) (ability.getRemainingCooldown() * 20));
            }
            else
            {
                player.setCooldown(this.getItemStack().getType(), 0);
            }
        }
    }

    @Override
    public boolean rebuild()
    {
        if (!this.isDirty()) return false;

        super.rebuild();

        if (this.getEntity() != null && this.getEntity() instanceof Player)
        {
            ItemManager.getInstance().rebuildItemStack((Player) this.getEntity(), this.getId());
        }

        return true;
    }

    public ItemStack toItemStack()
    {
        ItemBuilder builder = new ItemBuilder(this.getItemStack())
                .setDisplayName(this.getName())
                .setLore(this.getLore())
                .setPersistentData(Item.ITEM_ID_KEY, PersistentDataType.STRING, this.getId())
                .setUnbreakable(true)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addItemFlag(ItemFlag.HIDE_UNBREAKABLE);

        if (this.attackDamage > 1)
            builder = builder.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(attackDamageUuid, "attack_damage", this.attackDamage - 1, AttributeModifier.Operation.ADD_NUMBER));
        if (this.attackSpeed > 1)
            builder = builder.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(attackSpeedUuid, "attack_speed", this.attackSpeed - 1, AttributeModifier.Operation.ADD_NUMBER));
        if (this.knockBackResistance > 1)
            builder = builder.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(knockbackResistanceUuid, "knockback_resistance", this.knockBackResistance, AttributeModifier.Operation.ADD_NUMBER));
        if (this.knockBack > 1)
            builder = builder.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, new AttributeModifier(knockBackUuid, "knockback", this.knockBack, AttributeModifier.Operation.ADD_NUMBER));
        if (this.health > 0)
            builder = builder.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(healthUuid, "health", this.knockBack, AttributeModifier.Operation.ADD_NUMBER));

        if (this.hasSkin())
        {
            ItemSkin skin = this.getSkin();

            builder = builder.setCustomModelData(skin.getCustomModelData())
                    .setPersistentData(Item.ITEM_SKIN_KEY, PersistentDataType.STRING, skin.getId());
        }

        if (this.getEntity() != null)
        {
            builder = builder.setPersistentData(Item.ITEM_OWNER_KEY, PersistentDataType.STRING, this.getEntity().getUniqueId().toString());
        }

        return builder.build();
    }

    @Override
    public void destroy(boolean trigger)
    {
        super.destroy(trigger);

        this.abilityCooldowns.clear();
        this.lore.clear();
        this.categories.clear();
    }
}
