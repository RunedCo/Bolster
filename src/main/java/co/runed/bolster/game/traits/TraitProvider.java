package co.runed.bolster.game.traits;

import co.runed.dayroom.gson.JsonExclude;
import co.runed.dayroom.properties.Properties;

public abstract class TraitProvider {
    @JsonExclude
    private final Properties traits = new Properties();

    public Properties getTraits() {
        return traits;
    }

    public <T> void setTrait(Trait<T> key, T value) {
        this.traits.set(key, value);
    }

    public <T> T getTrait(Trait<T> key) {
        return this.traits.get(key);
    }
}
