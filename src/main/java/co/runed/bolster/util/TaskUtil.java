package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;

public class TaskUtil
{
    /**
     * Runs a repeating task for a specified number of repeats
     */
    public static synchronized BukkitTask runRepeatingTaskTimer(Plugin plugin, Runnable task, int numberOfRepeats, long initialDelay, long period)
    {
        return runRepeatingTaskTimer(plugin, task, numberOfRepeats, initialDelay, period, null);
    }

    /**
     * Runs a repeating task for a specified number of repeats
     *
     * @param plugin          the plugin instance
     * @param task            the task
     * @param numberOfRepeats number of times to repeat the task
     * @param initialDelay    the delay before running the first task
     * @param period          the interval between repeats
     * @return
     */
    public static synchronized BukkitTask runRepeatingTaskTimer(Plugin plugin, Runnable task, int numberOfRepeats, long initialDelay, long period, Runnable onFinish)
    {
        BukkitRunnable run = new BolsterRunnable(onFinish)
        {
            int runs = 0;

            @Override
            public void run()
            {
                task.run();

                runs++;

                if (runs >= numberOfRepeats)
                {
                    this.cancel();
                }
            }
        };

        return run.runTaskTimer(plugin, initialDelay, period);
    }

    /**
     * Runs a repeating task for a specified duration
     */
    public static synchronized BukkitTask runDurationTaskTimer(Plugin plugin, Runnable task, Duration duration, long initialDelay, long period)
    {
        return runDurationTaskTimer(plugin, task, duration, initialDelay, period, null);
    }

    /**
     * Runs a repeating task for a specified duration
     *
     * @param plugin       the plugin instance
     * @param task         the task
     * @param duration     how long to run the task for
     * @param initialDelay the delay before running the first task
     * @param period       the interval between repeats
     * @param onFinish     function to run when task finishes
     * @return
     */
    public static synchronized BukkitTask runDurationTaskTimer(Plugin plugin, Runnable task, Duration duration, long initialDelay, long period, Runnable onFinish)
    {
        BukkitRunnable run = new BolsterRunnable(onFinish)
        {
            Instant startTime = Instant.now();

            @Override
            public void run()
            {
                task.run();

                if (startTime.plus(duration).isBefore(Instant.now()))
                {
                    this.cancel();
                }
            }
        };

        return run.runTaskTimer(plugin, initialDelay, period);
    }

    public static TaskSeries series()
    {
        return new TaskSeries();
    }

    public static class TaskSeries
    {
        int duration = 0;

        public TaskSeries add(Runnable task, int durationTicks)
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Bolster.getInstance(), task, this.duration);

            this.duration += durationTicks;

            return this;
        }
    }

    public static abstract class BolsterRunnable extends BukkitRunnable
    {
        Runnable onFinish;

        public BolsterRunnable()
        {
            this(null);
        }

        public BolsterRunnable(Runnable onFinish)
        {
            this.onFinish = onFinish;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException
        {
            if (this.onFinish != null) onFinish.run();
            super.cancel();
        }
    }
}
