package co.runed.bolster.skills;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.util.registries.IRegisterable;

import java.util.ArrayList;
import java.util.List;

public class Upgrade extends AbilityProvider implements IRegisterable
{
    String id;

    int level = 0;
    List<Upgrade> children = new ArrayList<>();

    public void addChild(Upgrade upgrade)
    {
        this.children.add(upgrade);
    }

    public List<Upgrade> getChildren()
    {
        return children;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {

    }
}
