package co.runed.bolster.items;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.PassiveAbility;
import co.runed.bolster.abilities.conditions.HoldingItemCondition;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public abstract class Item {
    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(Bolster.getInstance(), "item-id");
    public static final NamespacedKey ITEM_SKIN_KEY = new NamespacedKey(Bolster.getInstance(), "item-skin");
    public static final NamespacedKey ITEM_OWNER_KEY = new NamespacedKey(Bolster.getInstance(), "item-owner");

    private String id;
    public String name;
    private List<String> lore = new ArrayList<>();

    private ItemSkin skin;
    private ItemStack itemStack;
    private Player owner;

    private ItemAbilitySlot primaryAbility = ItemAbilitySlot.RIGHT;
    private Ability leftClickAbility;
    private Ability rightClickAbility;
    private List<PassiveAbility> passives = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        String extra = "";

        if(this.hasSkin()) {
            extra = " (" + this.getSkin().getName() + ")";
        }

        return this.name + extra;
    }

    public void setName(String name) {
        this.name = name;
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
        ability.setCooldownSource(this.getId() + "." + ability.getId());

        if(slot == ItemAbilitySlot.LEFT) {
            this.leftClickAbility = ability;
            return;
        }

        this.rightClickAbility = ability;
    }

    public Ability getAbility(ItemAbilitySlot slot) {
        if(slot == ItemAbilitySlot.LEFT) {
            return this.leftClickAbility;
        }

        return this.rightClickAbility;
    }

    public void castAbility(ItemAbilitySlot slot) {
        Ability ability = this.getAbility(slot);

        if(ability == null) return;

        ability.setCaster(this.getOwner());

        boolean success = ability.activate();

        if (!success) return;

        if(this.primaryAbility == slot) {
            this.getOwner().setCooldown(this.itemStack.getType(), (int) ability.getCooldown() * 20);
        }
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;

        for (PassiveAbility passive : this.passives) {
            passive.setCaster(this.getOwner());
        }
    }

    public ItemStack toItemStack() {
        ItemStack stack = this.itemStack.clone();
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
        if(this.leftClickAbility != null) {
            this.leftClickAbility.destroy();
            this.leftClickAbility = null;
        }

        if(this.rightClickAbility != null) {
            this.rightClickAbility.destroy();
            this.rightClickAbility = null;
        }

        for (PassiveAbility passive : this.passives) {
            passive.destroy();
        }

        this.passives.clear();
    }
}
