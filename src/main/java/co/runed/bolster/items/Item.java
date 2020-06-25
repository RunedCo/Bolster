package co.runed.bolster.items;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.IAbilitySource;
import co.runed.bolster.abilities.conditions.HoldingItemCondition;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Stream;

public abstract class Item implements IAbilitySource {
    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(Bolster.getInstance(), "item-id");
    public static final NamespacedKey ITEM_SKIN_KEY = new NamespacedKey(Bolster.getInstance(), "item-skin");
    public static final NamespacedKey ITEM_OWNER_KEY = new NamespacedKey(Bolster.getInstance(), "item-owner");

    private String id;
    private String name;
    private List<String> lore = new ArrayList<>();
    private ItemStack itemStack = new ItemStack(Material.STICK);

    private ItemSkin skin;
    private final List<ItemCategory> categories = new ArrayList<>(Collections.singletonList(ItemCategory.ALL));

    private LivingEntity owner;

    private final Collection<AbilityData> abilities = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name + ChatColor.WHITE;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addLore(String description) {
        this.lore.addAll(StringUtil.formatLore(description));
    }

    public List<String> getLore() {
        List<String> loreWithAbilities = new ArrayList<>();

        for (AbilityData abilityData : this.abilities) {
            Ability ability = abilityData.ability;

            if (ability.getDescription() == null) continue;

            loreWithAbilities.addAll(StringUtil.formatLore(ChatColor.RED + abilityData.trigger.getDisplayName() +  ": " + ChatColor.YELLOW + ability.getDescription()));
        }

        if (loreWithAbilities.size() > 0 && this.lore.size() > 0) {
            loreWithAbilities.add("");
        }

        loreWithAbilities.addAll(this.lore);

        return loreWithAbilities;
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

        for (AbilityData abilityData : this.abilities) {
            Ability ability = abilityData.ability;

            if(ability.getCaster() == owner) continue;

            ability.setCaster(owner);
            Bolster.getAbilityManager().add(owner, abilityData.trigger, ability);
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

    public void addAbility(AbilityTrigger trigger, Ability ability) {
        this.addAbility(trigger, ability, false);
    }

    public void addAbility(AbilityTrigger trigger, Ability ability, Boolean showCooldown) {
        ability.setAbilitySource(this);

        ability.addCondition(new HoldingItemCondition(this));

        this.abilities.add(new AbilityData(trigger, ability, showCooldown));
    }

    public Boolean hasAbility(AbilityTrigger trigger) {
        return this.abilities.stream().anyMatch((info) -> info.trigger.equals(trigger));
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success) {
        Optional<AbilityData> filtered = this.abilities.stream().filter((info) -> info.ability == ability).findFirst();

        if(!filtered.isPresent()) return;

        AbilityData abilityData = filtered.get();

        if (abilityData.showCooldown && success) {
            ((Player)this.getOwner()).setCooldown(this.getItemStack().getType(), (int) (ability.getCooldown() * 20));
        }
    }

    /* public List<Ability> getAbilities(AbilityTrigger trigger) {
        if(this.abilities.containsKey(trigger)) {
            return this.abilities.get(trigger);
        }

        return new ArrayList<>();
    } */

    /* public void castAbility(AbilityTrigger trigger, Properties properties) {
        List<Ability> abilities = this.getAbilities(trigger);

        for (Ability ability : abilities) {
            if(ability == null) continue;

            ability.setCaster(this.getOwner());

            boolean success = ability.activate(properties);

            if (!success) continue;

            if(this.primaryAbility == trigger && this.getOwner().getType() == EntityType.PLAYER) {
                ((Player)this.getOwner()).setCooldown(this.getItemStack().getType(), (int) (ability.getCooldown() * 20));
            }
        }
    } */

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
        for (AbilityData abilityData : this.abilities) {
            Bolster.getAbilityManager().remove(this.getOwner(), abilityData.ability);
        }

        this.abilities.clear();
    }

    public static class AbilityData {
        public AbilityTrigger trigger;
        public Ability ability;
        public Boolean showCooldown;

        public AbilityData(AbilityTrigger trigger, Ability ability, Boolean showCooldown) {
            this.trigger = trigger;
            this.ability = ability;
            this.showCooldown = showCooldown;
        }
    }
}
