package co.runed.bolster.items;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.abilities.PassiveAbility;
import co.runed.bolster.abilities.conditions.HoldingItemCondition;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    private List<String> lore = new ArrayList<>();

    private ItemSkin skin;
    private ItemStack itemStack = new ItemStack(Material.STICK);
    private LivingEntity owner;

    private ItemAbilitySlot primaryAbility = ItemAbilitySlot.RIGHT_CLICK;

    private final Map<ItemAbilitySlot, Ability> abilities = new HashMap<>();
    private final List<PassiveAbility> passives = new ArrayList<>();

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

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void addLore(String lore) {
        this.lore.add(lore);
    }

    public List<String> getLore() {
        return lore;
    }

    protected ItemStack getItemStack() {
        return this.itemStack.clone();
    }

    protected void setItemStack(ItemStack stack) {
        this.itemStack = stack;
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

    public void setPrimaryAbility(ItemAbilitySlot primaryAbility) {
        this.primaryAbility = primaryAbility;
    }

    public void addPassive(PassiveAbility ability) {
        ability.addCondition(new HoldingItemCondition(this));

        this.passives.add(ability);
    }

    public void setAbility(Ability ability, ItemAbilitySlot slot) {
        ability.setCooldownSource(this.getId() + "." + ability.getCooldownSource());

        this.abilities.put(slot, ability);
    }

    public Ability getAbility(ItemAbilitySlot slot) {
        if(this.abilities.containsKey(slot)) {
            return this.abilities.get(slot);
        }

        return null;
    }

    public void castAbility(ItemAbilitySlot slot, AbilityProperties properties) {
        Ability ability = this.getAbility(slot);

        if(ability == null) return;

        ability.setCaster(this.getOwner());

        boolean success = ability.activate(properties);

        if (!success) return;

        if(this.primaryAbility == slot && this.getOwner().getType() == EntityType.PLAYER) {
            ((Player)this.getOwner()).setCooldown(this.getItemStack().getType(), (int) ability.getTotalCooldown() * 20);
        }
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

    public ItemStack toItemStack() {
        ItemStack stack = this.getItemStack();
        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        meta.setDisplayName(this.getName());
        meta.setLore(this.lore);

        pdc.set(Item.ITEM_ID_KEY, PersistentDataType.STRING, this.getId());

        if(this.hasSkin()) {
            meta.setCustomModelData(this.getSkin().getCustomModelData());

            pdc.set(Item.ITEM_SKIN_KEY, PersistentDataType.STRING, this.getSkin().getId());
        }

        if(this.getOwner() != null) {
            pdc.set(Item.ITEM_OWNER_KEY, PersistentDataType.STRING, this.getOwner().getUniqueId().toString());
        }

        stack.setItemMeta(meta);

        return stack;
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
