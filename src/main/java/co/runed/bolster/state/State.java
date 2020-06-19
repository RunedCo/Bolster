package co.runed.bolster.state;

import co.runed.bolster.Bolster;
import org.bukkit.Bukkit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;

public abstract class State {
    private boolean started = false;
    private boolean updating = false;
    private boolean ended = false;

    private boolean frozen = false;

    private Instant startInstant;
    private Duration duration;

    public void start() {
        if(started || ended)
            return;

        started = true;

        this.startInstant = Instant.now();

        try {
            this.onStart();
        }
        catch (Exception e) {
            // LOG
        }
    }

    protected abstract void onStart();

    public void update() {
        if(!started || ended || updating)
            return;

        updating = true;

        if(isReadyToEnd() && !frozen) {
            end();
            return;
        }

        try {
            this.onUpdate();
        }
        catch(Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();

            Bolster.getInstance().getLogger().severe(stacktrace);
        }
        updating = false;
    }

    protected abstract void onUpdate();

    public void end() {
        if(!started || ended)
            return;

        ended = true;

        try {
            this.onEnd();
        }
        catch(Exception e) {
            // LOG ERROR
        }
    }

    public boolean isReadyToEnd() {
        return ended || getRemainingDuration() == Duration.ZERO;
    }

    protected abstract void onEnd();

    public Duration getDuration() {
        return Duration.ZERO;
    }

    public Duration getRemainingDuration() {
        Duration sinceStart = Duration.between(startInstant, Instant.now());
        Duration remaining = this.getDuration().minus(sinceStart);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    public boolean getFrozen() {
        return this.frozen;
    }

    public void setFrozen(boolean freeze) {
        this.frozen = freeze;
    }
}
