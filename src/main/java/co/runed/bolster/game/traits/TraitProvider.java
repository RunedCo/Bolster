package co.runed.bolster.game.traits;

import co.runed.dayroom.gson.JsonExclude;
import co.runed.dayroom.properties.Properties;

import java.util.HashSet;
import java.util.Set;

public abstract class TraitProvider {
    @JsonExclude
    private final Properties traits = new Properties();

    private final Set<Trait<?>> disabledTraits = new HashSet<>();

    public Properties getTraits() {
        return traits;
    }

    public <T> void setTrait(Trait<T> key, T value) {
        this.traits.set(key, value);
    }

    public <T> T getTrait(Trait<T> key) {
        if (!isTraitEnabled(key)) return key.getDefault();

        return this.traits.get(key);
    }

    public void setTraitEnabled(Trait<?> trait, boolean enabled) {
        disabledTraits.remove(trait);

        if (!enabled) disabledTraits.add(trait);
    }

    public boolean isTraitEnabled(Trait<?> trait) {
        return !disabledTraits.contains(trait);
    }
}
