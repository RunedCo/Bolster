package co.runed.bolster;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;

public class PlayerClass extends AbilityProvider {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success) {

    }
}
