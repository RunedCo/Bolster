package co.runed.bolster.state;

import co.runed.bolster.Bolster;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class EventState extends State implements Listener {
    @Override
    public void start() {
        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, Bolster.getInstance());

        super.start();
    }

    @Override
    public void end() {
        HandlerList.unregisterAll(this);

        super.end();
    }
}
