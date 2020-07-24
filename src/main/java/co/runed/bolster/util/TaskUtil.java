package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskUtil
{
    /**
     *
     * @param plugin the plugin instance
     * @param task the task
     * @param numberOfRepeats number of times to repeat the task
     * @param initialDelay the delay before running the first task
     * @param period the interval between repeats
     * @return
     */
    public static synchronized BukkitTask runRepeatingTaskTimer(Plugin plugin, Runnable task, int numberOfRepeats, long initialDelay, long period)
    {
        BukkitRunnable run = new BukkitRunnable()
        {
            int i = 0;

            @Override
            public void run()
            {
                task.run();

                i++;

                if (i >= numberOfRepeats)
                {
                    this.cancel();
                }
            }
        };

        return run.runTaskTimer(Bolster.getInstance(), initialDelay, period);
    }
}
