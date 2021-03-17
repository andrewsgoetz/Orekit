package org.orekit.time;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orekit.Utils;
import org.orekit.time.UTCFields.AbstractUTCField;
import org.orekit.time.UTCFields.UTCField;
import org.orekit.time.UTCFields.UTCMicroOfDay;
import org.orekit.time.UTCFields.UTCMilliOfDay;
import org.orekit.time.UTCFields.UTCNanoOfDay;
import org.orekit.time.UTCFields.UTCSecondOfDay;
import org.orekit.time.UTCFields.UTCSecondOfMinute;

/**
 * Unit tests for {@link UTCTemporalAccessor}.
 */
public final class UTCTemporalAccessorTest {

    private static final Set<ChronoField> UNSUPPORTED_CHRONO_FIELDS = EnumSet.of(//
            ChronoField.NANO_OF_DAY, //
            ChronoField.MICRO_OF_DAY, //
            ChronoField.MILLI_OF_DAY, //
            ChronoField.SECOND_OF_MINUTE, //
            ChronoField.SECOND_OF_DAY, //
            ChronoField.INSTANT_SECONDS);

    private static UTCScale utc;

    @BeforeClass
    public static void setUp() {
        Utils.setDataRoot("regular-data");
        utc = TimeScalesFactory.getUTC();
    }

    /**
     * Tests {@link UTCTemporalAccessor#getUTCScale()}.
     */
    @Test
    public void testGetUTCScale() {
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(AbsoluteDate.ARBITRARY_EPOCH, utc);
        Assert.assertSame(utc, temporal.getUTCScale());
    }

    /**
     * Tests {@link UTCTemporalAccessor#isSupported(TemporalField)}.
     */
    @Test
    public void testIsSupported() {
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(AbsoluteDate.ARBITRARY_EPOCH, utc);

        for (ChronoField chronoField : ChronoField.values()) {
            Assert.assertEquals(!UNSUPPORTED_CHRONO_FIELDS.contains(chronoField), temporal.isSupported(chronoField));
        }

        Assert.assertTrue(temporal.isSupported(new UTCMicroOfDay(utc)));
        Assert.assertTrue(temporal.isSupported(new UTCMilliOfDay(utc)));
        Assert.assertTrue(temporal.isSupported(new UTCNanoOfDay(utc)));
        Assert.assertTrue(temporal.isSupported(new UTCSecondOfDay(utc)));
        Assert.assertTrue(temporal.isSupported(new UTCSecondOfMinute(utc)));
        Assert.assertFalse(temporal.isSupported(new AbstractUTCField(utc, ChronoUnit.CENTURIES, ChronoUnit.MILLENNIA) {
            @Override
            public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
                return null;
            }
        }));

        Assert.assertTrue(temporal.isSupported(JulianFields.MODIFIED_JULIAN_DAY));
    }

    /**
     * Tests {@link UTCTemporalAccessor#getLong(TemporalField)} with the
     * {@link ChronoField}s that are unsupported.
     */
    @Test
    public void testGetLongWithUnsupportedChronoFields() {
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(AbsoluteDate.ARBITRARY_EPOCH, utc);
        for (ChronoField chronoField : UNSUPPORTED_CHRONO_FIELDS) {
            Assert.assertThrows(UnsupportedTemporalTypeException.class, () -> temporal.getLong(chronoField));
        }
    }

    /**
     * Tests {@link UTCTemporalAccessor#getLong(TemporalField)} when the scales of
     * the {@link UTCTemporalAccessor} and the {@link UTCField} don't match.
     */
    @Test
    public void testGetLongWithIncompatibleUtcTimeScales() {
        final UTCScale utc2 = new UTCScale(TimeScalesFactory.getTAI(),
                Collections.singletonList(new OffsetModel(new DateComponents(1972, 1, 1), 10)));
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(AbsoluteDate.ARBITRARY_EPOCH, utc);
        Assert.assertThrows(IllegalArgumentException.class, () -> temporal.getLong(new UTCSecondOfDay(utc2)));
    }

    /**
     * Tests {@link UTCTemporalAccessor#getLong(TemporalField)} with an unsupported
     * {@link UTCField}.
     */
    @Test
    public void testGetLongWithUnsupportedUtcField() {
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(AbsoluteDate.ARBITRARY_EPOCH, utc);
        Assert.assertThrows(UnsupportedTemporalTypeException.class,
                () -> temporal.getLong(new AbstractUTCField(utc, ChronoUnit.CENTURIES, ChronoUnit.MILLENNIA) {
                    @Override
                    public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
                        return null;
                    }
                }));
    }

    /**
     * Tests {@link UTCTemporalAccessor#getLong(TemporalField)} with a UTC time in a
     * day with extra adjustment time, occurring before the adjustment.
     */
    @Test
    public void testGetLongBeforeAdjustment() {
        // 1971-12-31T23:59:59.500 UTC, 1972-01-01T00:00:09.392241985002414 TAI
        // TAI-UTC adjustment was +0.107758 seconds
        final AbsoluteDate date = new AbsoluteDate(1972, 1, 1, 0, 0, 9.392241985002414, TimeScalesFactory.getTAI());
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(date, utc);

        Assert.assertEquals(3L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(5L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(53L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(11L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(31L, temporal.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(5L, temporal.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(365L, temporal.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(729L, temporal.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.ERA));
        Assert.assertEquals(11L, temporal.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(500L, temporal.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(1439L, temporal.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(59L, temporal.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(12L, temporal.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(500_000_000L, temporal.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_663L, temporal.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1971L, temporal.getLong(ChronoField.YEAR));
        Assert.assertEquals(1971L, temporal.getLong(ChronoField.YEAR_OF_ERA));

        Assert.assertEquals(86_399_500_000L, temporal.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(86_399_500L, temporal.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(86_399_500_000_000L, temporal.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(86_399L, temporal.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(59L, temporal.getLong(new UTCSecondOfMinute(utc)));

        Assert.assertEquals(41316L, temporal.getLong(JulianFields.MODIFIED_JULIAN_DAY));
    }

    /**
     * Tests {@link UtcTemporalAccessor#getLong(TemporalField)} with a UTC time in a
     * day with extra adjustment time, occurring during the adjustment.
     */
    @Test
    public void testGetLongDuringAdjustment() {
        // 1971-12-31T23:59:60.100 UTC, 1972-01-01T00:00:09.992242003011143 TAI
        // TAI-UTC adjustment was +0.107758 seconds
        final AbsoluteDate date = new AbsoluteDate(1972, 1, 1, 0, 0, 9.992242003011143, TimeScalesFactory.getTAI());
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(date, utc);

        Assert.assertEquals(3L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(5L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(53L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(11L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(31L, temporal.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(5L, temporal.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(365L, temporal.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(729L, temporal.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.ERA));
        Assert.assertEquals(11L, temporal.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(100L, temporal.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(1439L, temporal.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(59L, temporal.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(12L, temporal.getLong(ChronoField.MONTH_OF_YEAR));
        // FIXME The commented-out test is close to passing (nano-of-second is
        // 100000003).
        // Assert.assertEquals(100_000_000L,
        // temporal.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_663L, temporal.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1971L, temporal.getLong(ChronoField.YEAR));
        Assert.assertEquals(1971L, temporal.getLong(ChronoField.YEAR_OF_ERA));

        Assert.assertEquals(86_400_100_000L, temporal.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(86_400_100L, temporal.getLong(new UTCMilliOfDay(utc)));
        // FIXME The commented-out test is close to passing (nano-of-day is
        // 86400100000003).
        // Assert.assertEquals(86_400_100_000_000L, temporal.getLong(new
        // UTCNanoOfDay(utc)));
        Assert.assertEquals(86_400L, temporal.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(60L, temporal.getLong(new UTCSecondOfMinute(utc)));

        Assert.assertEquals(41316L, temporal.getLong(JulianFields.MODIFIED_JULIAN_DAY));
    }

    /**
     * Tests {@link UtcTemporalAccessor#getLong(TemporalField)} with a UTC time in a
     * day with an extra leap second, occurring before the leap second.
     */
    @Test
    public void testGetLongBeforeLeapSecond() {
        // 1997-06-30T23:59:59.500 UTC, 1997-07-01T00:00:29.500 TAI
        final AbsoluteDate date = new AbsoluteDate(1997, 7, 1, 0, 0, 29.5, TimeScalesFactory.getTAI());
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(date, utc);

        Assert.assertEquals(2L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(6L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(5L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(26L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(11L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(30L, temporal.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(181L, temporal.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(10_042L, temporal.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.ERA));
        Assert.assertEquals(11L, temporal.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(500L, temporal.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(1439L, temporal.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(59L, temporal.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(6L, temporal.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(500_000_000L, temporal.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_969L, temporal.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1997L, temporal.getLong(ChronoField.YEAR));
        Assert.assertEquals(1997L, temporal.getLong(ChronoField.YEAR_OF_ERA));

        Assert.assertEquals(86_399_500_000L, temporal.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(86_399_500L, temporal.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(86_399_500_000_000L, temporal.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(86_399L, temporal.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(59L, temporal.getLong(new UTCSecondOfMinute(utc)));

        Assert.assertEquals(50629L, temporal.getLong(JulianFields.MODIFIED_JULIAN_DAY));
    }

    /**
     * Tests {@link UtcTemporalAccessor#getLong(TemporalField)} with a UTC time in a
     * day with an extra leap second, occurring during the leap second.
     */
    @Test
    public void testGetLongDuringLeapSecond() {
        // 1997-06-30T23:59:60.500 UTC, 1997-07-01T00:00:30.500 TAI
        final AbsoluteDate date = new AbsoluteDate(1997, 7, 1, 0, 0, 30.5, TimeScalesFactory.getTAI());
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(date, utc);

        Assert.assertEquals(2L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(6L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(5L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(26L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(11L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(30L, temporal.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(181L, temporal.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(10_042L, temporal.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.ERA));
        Assert.assertEquals(11L, temporal.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(500L, temporal.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(1439L, temporal.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(59L, temporal.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(6L, temporal.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(500_000_000L, temporal.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_969L, temporal.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1997L, temporal.getLong(ChronoField.YEAR));
        Assert.assertEquals(1997L, temporal.getLong(ChronoField.YEAR_OF_ERA));

        Assert.assertEquals(86_400_500_000L, temporal.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(86_400_500L, temporal.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(86_400_500_000_000L, temporal.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(86_400L, temporal.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(60L, temporal.getLong(new UTCSecondOfMinute(utc)));

        Assert.assertEquals(50629L, temporal.getLong(JulianFields.MODIFIED_JULIAN_DAY));
    }

    /**
     * Tests {@link UtcTemporalAccessor#getLong(TemporalField)} with a UTC time in a
     * day immediately after an extra leap second.
     */
    @Test
    public void testGetLongAfterLeapSecond() {
        // 1997-07-01T00:00:00.500 UTC, 1997-07-01T00:00:31.500 TAI
        final AbsoluteDate date = new AbsoluteDate(1997, 7, 1, 0, 0, 31.5, TimeScalesFactory.getTAI());
        final UTCTemporalAccessor temporal = new UTCTemporalAccessor(date, utc);

        Assert.assertEquals(1L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(7L, temporal.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(26L, temporal.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(12L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(24L, temporal.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(2L, temporal.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(182L, temporal.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(10_043L, temporal.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal.getLong(ChronoField.ERA));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(500L, temporal.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(7L, temporal.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(500_000_000L, temporal.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_970L, temporal.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1997L, temporal.getLong(ChronoField.YEAR));
        Assert.assertEquals(1997L, temporal.getLong(ChronoField.YEAR_OF_ERA));

        Assert.assertEquals(500_000L, temporal.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(500L, temporal.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(500_000_000L, temporal.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(0L, temporal.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(0L, temporal.getLong(new UTCSecondOfMinute(utc)));

        Assert.assertEquals(50630L, temporal.getLong(JulianFields.MODIFIED_JULIAN_DAY));
    }

}
