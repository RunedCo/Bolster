package co.runed.bolster.util;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeUtil
{
    /**
     * Format a duration to hh:mm:ss text
     *
     * @param duration
     * @return
     */
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

    public static String formatDate(ZonedDateTime date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d yyyy hh:mm a");
        return formatter.format(date);
    }

    private static String formatMsPretty(long ms)
    {
        return DurationFormatUtils.formatDurationWords(ms, true, true);
    }

    public static String formatDateRemainingPretty(ZonedDateTime date)
    {
        return formatMsPretty(Duration.between(Instant.now(), date).toMillis());
    }

    public static String formatDatePrettyRounded(ZonedDateTime date)
    {
        long ms = Duration.between(ZonedDateTime.now(Clock.systemUTC()), date).toMillis();
        // floor divide by duration of hour in ms to not show hours, mins, or seconds
        ms = Math.max(1, Math.floorDiv(ms, 3600000L)) * 3600000L;

        return formatMsPretty(ms);
    }

    public static long toTicks(Duration duration)
    {
        if (duration.getSeconds() >= Integer.MAX_VALUE) return Integer.MAX_VALUE;

        long seconds = duration.toMillis() / 1000;

        return seconds * 20;
    }

    public static double toSeconds(Duration duration)
    {
        return duration.toMillis() / 1000d;
    }

    public static Duration fromTicks(long ticks)
    {
        return fromSeconds(ticks * 20);
    }

    public static Duration fromSeconds(double seconds)
    {
        return Duration.ofMillis((long) (seconds * 1000));
    }

    public static Instant addSeconds(Instant instant, double seconds)
    {
        return instant.plusMillis((long) (seconds * 1000));
    }

    public static Instant plusInstants(Instant instant1, Instant instant2)
    {
        return instant1.plusMillis(instant2.toEpochMilli());
    }

    public static Instant minusInstants(Instant instant1, Instant instant2)
    {
        return instant1.minusMillis(instant2.toEpochMilli());
    }
}
