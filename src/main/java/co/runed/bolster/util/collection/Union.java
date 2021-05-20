package co.runed.bolster.util.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Union
{
    public static <T> Set<T> set(Collection<T>... inputs)
    {
        return Stream.of(inputs)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public static <T> List<T> list(Collection<T>... inputs)
    {
        return Stream.of(inputs)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
