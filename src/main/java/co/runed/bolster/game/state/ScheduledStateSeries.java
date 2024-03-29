package co.runed.bolster.game.state;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;

public class ScheduledStateSeries extends StateSeries {
    private final Plugin plugin;
    private final long interval;
    protected BukkitTask scheduledTask;

    protected List<Runnable> onUpdate = new LinkedList<>();

    public ScheduledStateSeries(Plugin plugin) {
        this(plugin, 1);
    }

    public ScheduledStateSeries(Plugin plugin, long interval) {
        this.plugin = plugin;
        this.interval = interval;
    }

    @Override
    public final void onStart() {
        super.onStart();

        this.scheduledTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            this.update();
            this.onUpdate.forEach(Runnable::run);
        }, 0L, interval);
    }

    @Override
    public final void onEnd() {
        super.onEnd();
        this.scheduledTask.cancel();
    }

    public final void addOnUpdate(Runnable runnable) {
        this.onUpdate.add(runnable);
    }
}