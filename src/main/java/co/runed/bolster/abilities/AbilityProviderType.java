package co.runed.bolster.abilities;

import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;

import java.util.Arrays;
import java.util.List;

public class AbilityProviderType
{
    public static final AbilityProviderType CLASS = new AbilityProviderType("class", true, Registries.CLASSES);
    public static final AbilityProviderType ITEM = new AbilityProviderType("item", false, Registries.ITEMS);
    public static final AbilityProviderType UPGRADE = new AbilityProviderType("upgrade", false, Registries.UPGRADES);

    String id;
    boolean solo;
    List<Registry> registries;

    public AbilityProviderType(String id, boolean solo, Registry... registries)
    {
        this.id = id;
        this.solo = solo;
        this.registries = Arrays.asList(registries);
    }

    public boolean isSolo()
    {
        return solo;
    }

    public List<Registry> getRegistries()
    {
        return registries;
    }
}
