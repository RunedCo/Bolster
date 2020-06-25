package co.runed.bolster.abilities;

public interface IAbilitySource {
    String getId();

    void onCastAbility(Ability ability, Boolean success);
}