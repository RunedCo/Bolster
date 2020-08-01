package co.runed.bolster.status;

public class UntargetableStatusEffect extends InvulnerableStatusEffect
{
    public UntargetableStatusEffect(double duration)
    {
        super(duration);
    }

    @Override
    public String getName()
    {
        return null;
    }
}
