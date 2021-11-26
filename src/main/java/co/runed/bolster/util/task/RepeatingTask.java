package co.runed.bolster.util.task;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Supplier;

public class RepeatingTask extends Task {
    private long frequency;

    private int repeats = 0;
    private Duration duration = null;
    private Supplier<Boolean> runUntil = null;
    private Runnable onFinish = null;

    public RepeatingTask(long frequency) {
        this.frequency = frequency;
    }

    public RepeatingTask until(Supplier<Boolean> until) {
        this.runUntil = until;

        return this;
    }

    public RepeatingTask onFinish(Runnable onFinish) {
        this.onFinish = onFinish;

        return this;
    }

    public RepeatingTask repeats(int numberOfRepeats) {
        this.repeats = numberOfRepeats;

        return this;
    }

    public RepeatingTask duration(Duration duration) {
        this.duration = duration;

        return this;
    }

    @Override
    public RepeatingTask delay(long delay) {
        return (RepeatingTask) super.delay(delay);
    }

    @Override
    protected BukkitTask createInternalTask(@NotNull Runnable function) {
        if (runUntil != null) {
            return TaskUtil.runTaskTimerUntil(Bolster.getInstance(), function, runUntil, delay, frequency, this::cancel);
        }

        if (repeats > 0) {
            return TaskUtil.runRepeatingTaskTimer(Bolster.getInstance(), function, repeats, delay, frequency, this::cancel);
        }

        if (duration != null) {
            return TaskUtil.runDurationTaskTimer(Bolster.getInstance(), function, duration, delay, frequency, this::cancel);
        }

        return Bukkit.getScheduler().runTaskTimer(Bolster.getInstance(), function, delay, frequency);
    }

    @Override
    public RepeatingTask run(Runnable function) {
        return (RepeatingTask) super.run(function);
    }

    @Override
    public void cancel() {
        super.cancel();

        if (onFinish != null) {
            onFinish.run();
        }
    }
}
