package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
            long runningTicks = 0L;

            @Override
            public void run()
            {
                if (this.isCancelled()) return;

                task.run();

                runningTicks += period;

                if (runningTicks >= TimeUtil.toTicks(duration))
                {
                    this.cancel();
                }
            }
        };

        return run.runTaskTimer(plugin, initialDelay, period);
    }

    /**
     * Runs a repeating task for a specified duration
     *
     * @param plugin       the plugin instance
     * @param task         the task
     * @param runUntil
     * @param initialDelay the delay before running the first task
     * @param period       the interval between repeats
     * @param onFinish     function to run when task finishes
     * @return
     */
    public static synchronized BukkitTask runTaskTimerUntil(Plugin plugin, Runnable task, Supplier<Boolean> runUntil, long initialDelay, long period, Runnable onFinish)
    {
        BukkitRunnable run = new BolsterRunnable(onFinish)
        {
            @Override
            public void run()
            {
                if (this.isCancelled()) return;

                task.run();

                if (this.shouldCancel())
                    this.cancel();
            }

            @Override
            public boolean shouldCancel()
            {
                return runUntil != null ?
                        runUntil.get() :
                        super.shouldCancel();
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
        Runnable cancelRunnable = null;
        List<BukkitTask> tasks = new ArrayList<>();
        long duration = 0;

        public TaskSeries add(Runnable task)
        {
            return this.add(task, 0);
        }

        public TaskSeries add(Runnable task, long duration)
        {
            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(Bolster.getInstance(), task, this.duration);

            this.tasks.add(bukkitTask);

            this.duration += duration;

            return this;
        }

        public TaskSeries addRepeating(Runnable task, long duration, long period)
        {
            BukkitTask bukkitTask = runDurationTaskTimer(Bolster.getInstance(), task, TimeUtil.fromSeconds(duration / 20d), this.duration, period);

            this.tasks.add(bukkitTask);

            this.duration += duration;

            return this;
        }

        public TaskSeries onCancel(Runnable task)
        {
            this.cancelRunnable = task;

            return this;
        }

        public void cancel()
        {
            for (BukkitTask task : this.tasks)
            {
                task.cancel();
            }

            if (this.cancelRunnable != null)
            {
                this.cancelRunnable.run();
            }
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

        public boolean shouldCancel()
        {
            return false;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException
        {
            if (this.onFinish != null) onFinish.run();
            super.cancel();
        }
    }
}
