package co.runed.bolster.util.easing;

import java.util.function.BiFunction;

// See https://easings.net/ for examples
public enum Ease
{
    LINEAR((time, duration) -> time / duration),
    IN_SINE((time, duration) -> -Math.cos(time / duration * Ease._PiOver2) + 1),
    OUT_SINE((time, duration) -> Math.sin(time / duration * Ease._PiOver2)),
    IN_OUT_SINE((time, duration) -> -0.5d * (Math.cos(Math.PI * time / duration) - 1)),
    IN_QUAD((time, duration) -> (time /= duration) * time),
    OUT_QUAD((time, duration) -> -(time /= duration) * (time - 2)),
    IN_OUT_QUAD((time, duration) -> {
        if ((time /= duration * 0.5) < 1) return 0.5 * time * time;
        return -0.5 * ((--time) * (time - 2) - 1);
    }),
    IN_CUBIC((time, duration) -> (time /= duration) * time * time),
    OUT_CUBIC((time, duration) -> ((time = time / duration - 1) * time * time + 1)),
    IN_OUT_CUBIC((time, duration) -> {
        if ((time /= duration * 0.5) < 1) return 0.5 * time * time * time;
        return 0.5 * ((time -= 2) * time * time + 2);
    }),
    IN_QUART((time, duration) -> (time /= duration) * time * time * time),
    OUT_QUART((time, duration) -> -((time = time / duration - 1) * time * time * time - 1)),
    IN_OUT_QUART((time, duration) -> {
        if ((time /= duration * 0.5) < 1) return 0.5 * time * time * time * time;
        return -0.5 * ((time -= 2) * time * time * time - 2);
    }),
    IN_QUINT((time, duration) -> (time /= duration) * time * time * time * time),
    OUT_QUINT((time, duration) -> ((time = time / duration - 1) * time * time * time * time + 1)),
    IN_OUT_QUINT((time, duration) -> {
        if ((time /= duration * 0.5) < 1) return 0.5 * time * time * time * time * time;
        return 0.5 * ((time -= 2) * time * time * time * time + 2);
    }),
    IN_EXPO((time, duration) -> (time == 0) ? 0 : Math.pow(2, 10 * (time / duration - 1))),
    OUT_EXPO((time, duration) -> {
        if (time.equals(duration)) return 1.0;
        return (-Math.pow(2, -10 * time / duration) + 1);
    }),
    IN_OUT_EXPO((time, duration) -> {
        if (time == 0) return 0.0;
        if (time.equals(duration)) return 1.0;
        if ((time /= duration * 0.5f) < 1) return 0.5f * Math.pow(2, 10 * (time - 1));
        return 0.5f * (-Math.pow(2, -10 * --time) + 2);
    }),
    IN_CIRC((time, duration) -> -(Math.sqrt(1 - (time /= duration) * time) - 1)),
    OUT_CIRC((time, duration) -> Math.sqrt(1 - (time = time / duration - 1) * time)),
    IN_OUT_CIRC((time, duration) -> {
        if ((time /= duration * 0.5) < 1) return -0.5 * (Math.sqrt(1 - time * time) - 1);
        return 0.5 * (Math.sqrt(1 - (time -= 2) * time) + 1);
    });
//    IN_ELASTIC((time, duration) -> 0d),
//    OUT_ELASTIC((time, duration) -> 0d),
//    IN_OUT_ELASTIC((time, duration) -> 0d),
//    IN_BACK((time, duration) -> 0d),
//    OUT_BACK((time, duration) -> 0d),
//    IN_OUT_BACK((time, duration) -> 0d),
//    IN_BOUNCE((time, duration) -> 0d),
//    OUT_BOUNCE((time, duration) -> 0d),
//    IN_OUT_BOUNCE((time, duration) -> 0d);

    private static final double _PiOver2 = Math.PI * 0.5f;
    private static final double _TwoPi = Math.PI * 2;

    private final BiFunction<Double, Double, Double> easeFunction;

    Ease(BiFunction<Double, Double, Double> easeFunction)
    {
        this.easeFunction = easeFunction;
    }

    public BiFunction<Double, Double, Double> getEaseFunction()
    {
        return easeFunction;
    }
}
