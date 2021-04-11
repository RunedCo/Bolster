package co.runed.bolster.conditions;

import co.runed.bolster.util.Operator;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.World;

public class IsTimeCondition extends TargetedCondition<World> {
    Operator operator;
    long time;

    public IsTimeCondition(Target<World> target, Operator operator, long time) {
        super(target);

        this.operator = operator;
        this.time = time;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties) {
        World world = this.getTarget().get(properties);
        long worldTime = world.getTime();
        
        switch (operator) {
            case ABOVE: {
                return worldTime > this.time;
            }
            case ABOVE_OR_EQUAL: {
                return worldTime >= this.time;
            }
            case EQUAL: {
                return worldTime == this.time;
            }
            case BELOW_OR_EQUAL: {
                return worldTime <= this.time;
            }
            case BELOW: {
                return worldTime < this.time;
            }
        }

        return false;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted) {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted) {
        return null;
    }
}
