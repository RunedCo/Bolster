package co.runed.bolster.util.task;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class Task {
    protected Runnable function;
    protected long delay = 0L;

    private BukkitTask internalTask;

    protected BukkitTask createInternalTask(@NotNull Runnable function) {
        return Bukkit.getScheduler().runTaskLater(Bolster.getInstance(), function, delay);
    }

    public Task delay(long delay) {
        this.delay = delay;

        return this;
    }

    public Task delay(Duration delay) {
        this.delay = TimeUtil.toTicks(delay);

        return this;
    }

    public Task run(Runnable function) {
        if (function != null) internalTask = createInternalTask(function);

        return this;
    }

    public void cancel() {
        if (internalTask == null) return;

        internalTask.cancel();
    }

    public BukkitTask getInternalTask() {
        return internalTask;
    }

    public static TaskUtil.TaskSeries series() {
        return new TaskUtil.TaskSeries();
    }
}
