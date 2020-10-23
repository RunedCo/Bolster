package co.runed.bolster.util;

import java.util.*;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Collection<E> entries = new ArrayList<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(E value)
    {
        this();

        this.add(1, value);
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E value) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, value);

        if(!this.entries.contains(value)) this.entries.add(value);

        return this;
    }

    public boolean contains(E value)
    {
        return this.entries.contains(value);
    }

    public boolean isEmpty()
    {
        return this.entries.isEmpty();
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}
