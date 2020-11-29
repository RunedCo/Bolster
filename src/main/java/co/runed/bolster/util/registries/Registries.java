package co.runed.bolster.util.registries;

import co.runed.bolster.Bolster;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemSkin;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.wip.Currency;
import co.runed.bolster.wip.particles.ParticleSet;
import co.runed.bolster.wip.upgrade.Upgrade;

public class Registries
{
    public static final Registry<Item> ITEMS = new Registry<>(Bolster.getInstance(), "items");
    public static final Registry<BolsterClass> CLASSES = new Registry<>(Bolster.getInstance(), "classes");
    public static final Registry<ItemSkin> ITEM_SKINS = new Registry<>(Bolster.getInstance());
    public static final Registry<ParticleSet> PARTICLE_SETS = new Registry<>(Bolster.getInstance());
    public static final Registry<Upgrade> UPGRADES = new Registry<>(Bolster.getInstance());
    public static final Registry<Property> GAME_PROPERTIES = new Registry<>(Bolster.getInstance());
    public static final Registry<StatusEffect> STATUS_EFFECTS = new Registry<>(Bolster.getInstance());
    public static final Registry<Currency> CURRENCIES = new Registry<>(Bolster.getInstance());
}