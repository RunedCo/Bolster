package co.runed.bolster.util.registries;

import co.runed.bolster.Bolster;
import co.runed.bolster.common.properties.Property;
import co.runed.bolster.fx.particles.ParticleSet;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.game.shop.Shop;
import co.runed.bolster.status.StatusEffect;

public final class Registries {
    public static final Registry<ParticleSet> PARTICLE_SETS = new Registry<>(Bolster.getInstance(), "particles");
    public static final Registry<Property<?>> GAME_PROPERTIES = new Registry<>(Bolster.getInstance());
    public static final Registry<StatusEffect> STATUS_EFFECTS = new Registry<>(Bolster.getInstance());
    public static final Registry<Currency> CURRENCIES = new Registry<>(Bolster.getInstance(), "currencies");
    public static final Registry<Property<?>> SETTINGS = new Registry<>(Bolster.getInstance());
    public static final Registry<GameMode> GAME_MODES = new Registry<>(Bolster.getInstance());
    public static final Registry<Shop> SHOPS = new Registry<>(Bolster.getInstance(), "shops");
    public static final Registry<Property<?>> TRAITS = new Registry<>(Bolster.getInstance());
}
