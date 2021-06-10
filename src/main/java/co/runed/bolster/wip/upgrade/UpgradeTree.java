package co.runed.bolster.wip.upgrade;

import java.util.ArrayList;
import java.util.List;

public class UpgradeTree
{
    Upgrade upgrade;
    List<UpgradeTree> children = new ArrayList<>();

    public UpgradeTree()
    {
        this(null);
    }

    public UpgradeTree(Upgrade upgrade)
    {
        this.upgrade = upgrade;
    }

    public Upgrade getUpgrade()
    {
        return upgrade;
    }

    public List<UpgradeTree> getChildren()
    {
        return children;
    }

    public UpgradeTree addChild(Upgrade upgrade)
    {
        this.children.add(new UpgradeTree(upgrade));

        return this;
    }

    public UpgradeTree addChild(UpgradeTree upgrade)
    {
        this.children.add(upgrade);

        return this;
    }

    public List<Upgrade> getAllUpgrades()
    {
        List<Upgrade> upgrades = new ArrayList<>();

        upgrades.add(this.upgrade);

        for (UpgradeTree child : this.children)
        {
            upgrades.addAll(child.getAllUpgrades());
        }

        return upgrades;
    }
}
