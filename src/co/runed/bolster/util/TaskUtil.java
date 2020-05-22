package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import com.sun.istack.internal.NotNull;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskUtil {
    public static synchronized BukkitTask runRepeatingTaskTimer(@NotNull Plugin plugin, Runnable task, int numberOfRepeats, long initialDelay, long period) {
        BukkitRunnable run = new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                task.run();

                i++;

                if(i >= numberOfRepeats) {
                    this.cancel();
                }
            }
        };

        return run.runTaskTimer(Bolster.getInstance(),initialDelay, period);
    }
}
