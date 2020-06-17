package co.runed.bolster.items;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.abilities.PassiveAbility;
import co.runed.bolster.abilities.conditions.HoldingItemCondition;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Item {
    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(Bolster.getInstance(), "item-id");
    public static final NamespacedKey ITEM_SKIN_KEY = new NamespacedKey(Bolster.getInstance(), "item-skin");
    public static final NamespacedKey ITEM_OWNER_KEY = new NamespacedKey(Bolster.getInstance(), "item-owner");

    private String id;
    public String name;
    public String description;
    private ItemStack itemStack = new ItemStack(Material.STICK);

    private ItemSkin skin;
    private final List<ItemCategory> categories = new ArrayList<>();

    private LivingEntity owner;

    private ItemAction primaryAbility = ItemAction.RIGHT_CLICK;
    private final Map<ItemAction, Ability> abilities = new HashMap<>();
    private final List<PassiveAbility> passives = new ArrayList<>();

    public Item() {
        this.addCategory(ItemCategory.ALL);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name + ChatColor.RESET;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLore() {
        return StringUtil.formatLore(this.getDescription());
    }

    protected ItemStack getItemStack() {
        return this.itemStack.clone();
    }

    protected void setItemStack(ItemStack stack) {
        this.itemStack = stack;
    }

    public LivingEntity getOwner() {
        return this.owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;

        for (PassiveAbility passive : this.passives) {
            passive.setCaster(this.getOwner());
        }
    }

    public boolean hasSkin() {
        return this.getSkin() != null;
    }

    public ItemSkin getSkin() {
        return this.skin;
    }

    public void setSkin(ItemSkin skin) {
        this.skin = skin;
    }

    public List<ItemCategory> getCategories() {
        return this.categories;
    }

    public void addCategory(ItemCategory category) {
        if(this.categories.contains(category)) return;

        this.categories.add(category);
    }

    public void setPrimaryAbility(ItemAction primaryAbility) {
        this.primaryAbility = primaryAbility;
    }

    public void addPassive(PassiveAbility ability) {
        ability.addCondition(new HoldingItemCondition(this));

        this.passives.add(ability);
    }

    public void setAbility(ItemAction slot, Ability ability) {
        ability.setCooldownSource(this.getId() + "." + ability.getCooldownSource());

        this.abilities.put(slot, ability);
    }

    public Ability getAbility(ItemAction slot) {
        if(this.abilities.containsKey(slot)) {
            return this.abilities.get(slot);
        }

        return null;
    }

    public void castAbility(ItemAction slot, AbilityProperties properties) {
        Ability ability = this.getAbility(slot);

        if(ability == null) return;

        ability.setCaster(this.getOwner());

        boolean success = ability.activate(properties);

        if (!success) return;

        if(this.primaryAbility == slot && this.getOwner().getType() == EntityType.PLAYER) {
            ((Player)this.getOwner()).setCooldown(this.getItemStack().getType(), (int) ability.getTotalCooldown() * 20);
        }
    }

    public ItemStack toItemStack() {
        ItemBuilder builder = new ItemBuilder(this.getItemStack())
                .setDisplayName(this.getName())
                .setLore(this.getLore())
                .setPersistentData(Item.ITEM_ID_KEY, PersistentDataType.STRING, this.getId());


        if(this.hasSkin()) {
            builder.setCustomModelData(this.getSkin().getCustomModelData())
                    .setPersistentData(Item.ITEM_SKIN_KEY, PersistentDataType.STRING, this.getSkin().getId());
        }

        if(this.getOwner() != null) {
            builder.setPersistentData(Item.ITEM_OWNER_KEY, PersistentDataType.STRING, this.getOwner().getUniqueId().toString());
        }

        return builder.build();
    }

    public void destroy() {
        for (Ability ability : this.abilities.values()) {
            ability.destroy();
        }

        for (PassiveAbility passive : this.passives) {
            passive.destroy();
        }

        this.abilities.clear();
        this.passives.clear();
    }
}
