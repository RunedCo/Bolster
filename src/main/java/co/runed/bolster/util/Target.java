package co.runed.bolster.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Target<T> implements Iterable<T> {
    private List<T> _cachedValue = null;

    private Supplier<Collection<T>> ignoreTargets = ArrayList::new;
    private Collection<Function<T, Boolean>> ignoreFunctions = new ArrayList<>();

    abstract Collection<T> getTargets();

    private Collection<T> getFilteredTargets() {
        var ignored = ignoreTargets.get();

        var out = getTargets().stream().filter(t -> !ignored.contains(t));

        for (var func : ignoreFunctions) {
            out = out.filter(t -> !func.apply(t));
        }

        return out.toList();
    }

    public Target<T> ignoreIf(Function<T, Boolean> ignoreFunction) {
        this.ignoreFunctions.add(ignoreFunction);

        return this;
    }

    public Target<T> ignoreIf(T ignoreTarget) {
        return this.ignoreIf(t -> t.equals(ignoreTarget));
    }

    public Target<T> ignoreIf(Supplier<Collection<T>> ignoreTargets) {
        this.ignoreTargets = ignoreTargets;

        return this;
    }

    public Target<T> run(Consumer<T> consumer) {
        for (var target : getFilteredTargets()) {
            consumer.accept(target);
        }

        return this;
    }

    public List<T> toList() {
        return new ArrayList<>(getFilteredTargets());
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        if (_cachedValue == null) _cachedValue = toList();

        return _cachedValue.iterator();
    }
}
