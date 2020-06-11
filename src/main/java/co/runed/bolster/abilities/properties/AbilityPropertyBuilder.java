package co.runed.bolster.abilities.properties;

import co.runed.bolster.Bolster;
import org.bukkit.NamespacedKey;

public class AbilityPropertyBuilder<T> {
    T value = null;
    NamespacedKey id = null;

    public static AbilityPropertyBuilder key(NamespacedKey identifier) {
        AbilityPropertyBuilder builder = new AbilityPropertyBuilder();
        builder.id = identifier;

        return builder;
    }

    public static AbilityPropertyBuilder key(String key) {
        return key(new NamespacedKey(Bolster.getInstance(), key));
    }

    public AbilityPropertyBuilder<T> initial(T value) {
        this.value = value;

        return this;
    }

    public AbilityProperty<T> build() {
        return new AbilityProperty<>(this.id, this.value);
    }
}
