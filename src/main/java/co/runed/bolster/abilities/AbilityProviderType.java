package co.runed.bolster.abilities;

public class AbilityProviderType
{
    public static final AbilityProviderType CLASS = new AbilityProviderType("class", true);
    public static final AbilityProviderType ITEM = new AbilityProviderType("item", false);
    public static final AbilityProviderType UPGRADE = new AbilityProviderType("upgrade", false);

    String id;
    boolean solo;

    public AbilityProviderType(String id, boolean solo)
    {
        this.id = id;
        this.solo = solo;
    }

    public boolean isSolo()
    {
        return solo;
    }
}
