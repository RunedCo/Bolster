package co.runed.bolster.util;

import java.time.Duration;

public class DurationUtil
{
    public static String formatDurationHhMmSs(Duration duration)
    {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);

        if (positive.startsWith("0:"))
        {
            positive = positive.substring(2);
        }

        return seconds < 0 ? "-" + positive : positive;
    }
}
