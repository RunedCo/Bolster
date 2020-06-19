package co.runed.bolster.state;

import co.runed.bolster.Bolster;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class EventState extends State implements Listener {
    public EventState() {
        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, Bolster.getInstance());
    }

    @Override
    public void end() {
        super.end();

        HandlerList.unregisterAll(this);
    }
}
