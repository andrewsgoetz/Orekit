package org.orekit.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orekit.Utils;
import org.orekit.time.UTCFields.UTCMicroOfDay;
import org.orekit.time.UTCFields.UTCMilliOfDay;
import org.orekit.time.UTCFields.UTCNanoOfDay;
import org.orekit.time.UTCFields.UTCSecondOfDay;
import org.orekit.time.UTCFields.UTCSecondOfMinute;

/**
 * Unit tests for {@link UTCFields}.
 */
public final class UTCFieldsTest {

    private static UTCScale utc;

    @BeforeClass
    public static void setUp() {
        Utils.setDataRoot("regular-data");
        utc = TimeScalesFactory.getUTC();
    }

    /**
     * Tests {@link UTCFields.AbstractUTCField}.
     */
    @Test
    public void testAbstractUTCField() {
        final UTCSecondOfDay utcSecondOfDay = new UTCSecondOfDay(utc);

        Assert.assertSame(utc, utcSecondOfDay.getUTCScale());

        Assert.assertSame(ChronoUnit.SECONDS, utcSecondOfDay.getBaseUnit());

        Assert.assertSame(ChronoUnit.DAYS, utcSecondOfDay.getRangeUnit());

        final ValueRange range = utcSecondOfDay.range();
        Assert.assertEquals(0L, range.getMinimum());
        Assert.assertEquals(0L, range.getSmallestMaximum());
        Assert.assertEquals(Long.MAX_VALUE, range.getMaximum());

        Assert.assertFalse(utcSecondOfDay.isDateBased());

        Assert.assertTrue(utcSecondOfDay.isTimeBased());

        Assert.assertTrue(utcSecondOfDay.isSupportedBy(new UTCTemporalAccessor(AbsoluteDate.ARBITRARY_EPOCH, utc)));
        Assert.assertFalse(utcSecondOfDay.isSupportedBy(Instant.EPOCH));

        // 2000-01-01T12:00:00 UTC, 2000-01-01T12:00:32 TAI
        final AbsoluteDate date = new AbsoluteDate(2000, 1, 1, 12, 0, 32, TimeScalesFactory.getTAI());
        Assert.assertEquals(43200L, utcSecondOfDay.getFrom(new UTCTemporalAccessor(date, utc)));
        Assert.assertThrows(UnsupportedTemporalTypeException.class, () -> utcSecondOfDay.getFrom(Instant.EPOCH));

        Assert.assertThrows(UnsupportedOperationException.class, () -> utcSecondOfDay.adjustInto(Instant.EPOCH, 0L));

        Assert.assertEquals(0., utcSecondOfDay.getAdjustment(LocalDate.of(1972, 12, 30)), 0.);
        Assert.assertEquals(1., utcSecondOfDay.getAdjustment(LocalDate.of(1972, 12, 31)), 0.);
        Assert.assertEquals(0., utcSecondOfDay.getAdjustment(LocalDate.of(1973, 1, 1)), 0.);
    }

    /**
     * Tests {@link UTCFields.UTCMicroOfDay}.
     */
    @Test
    public void testUTCMicroOfDay() {
        final UTCMicroOfDay utcMicroOfDay = new UTCMicroOfDay(utc);

        final LocalDate localDate1 = LocalDate.of(1971, 12, 31);
        final ValueRange valueRange1 = utcMicroOfDay.rangeRefinedBy(localDate1);
        Assert.assertTrue(valueRange1.isFixed());
        Assert.assertEquals(0L, valueRange1.getMinimum());
        Assert.assertEquals(86400107757L, valueRange1.getMaximum());

        final LocalDate localDate2 = LocalDate.of(1972, 1, 1);
        final ValueRange valueRange2 = utcMicroOfDay.rangeRefinedBy(localDate2);
        Assert.assertTrue(valueRange2.isFixed());
        Assert.assertEquals(0L, valueRange2.getMinimum());
        Assert.assertEquals(86399999999L, valueRange2.getMaximum());

        final LocalDate localDate3 = LocalDate.of(1997, 6, 30);
        final ValueRange valueRange3 = utcMicroOfDay.rangeRefinedBy(localDate3);
        Assert.assertTrue(valueRange3.isFixed());
        Assert.assertEquals(0L, valueRange3.getMinimum());
        Assert.assertEquals(86400999999L, valueRange3.getMaximum());

        final LocalDate localDate4 = LocalDate.of(1997, 7, 1);
        final ValueRange valueRange4 = utcMicroOfDay.rangeRefinedBy(localDate4);
        Assert.assertTrue(valueRange4.isFixed());
        Assert.assertEquals(0L, valueRange4.getMinimum());
        Assert.assertEquals(86399999999L, valueRange4.getMaximum());
    }

    /**
     * Tests {@link UTCFields.UTCMilliOfDay}.
     */
    @Test
    public void testUTCMilliOfDay() {
        final UTCMilliOfDay utcMilliOfDay = new UTCMilliOfDay(utc);

        final LocalDate localDate1 = LocalDate.of(1971, 12, 31);
        final ValueRange valueRange1 = utcMilliOfDay.rangeRefinedBy(localDate1);
        Assert.assertTrue(valueRange1.isFixed());
        Assert.assertEquals(0L, valueRange1.getMinimum());
        Assert.assertEquals(86400107L, valueRange1.getMaximum());

        final LocalDate localDate2 = LocalDate.of(1972, 1, 1);
        final ValueRange valueRange2 = utcMilliOfDay.rangeRefinedBy(localDate2);
        Assert.assertTrue(valueRange2.isFixed());
        Assert.assertEquals(0L, valueRange2.getMinimum());
        Assert.assertEquals(86399999L, valueRange2.getMaximum());

        final LocalDate localDate3 = LocalDate.of(1997, 6, 30);
        final ValueRange valueRange3 = utcMilliOfDay.rangeRefinedBy(localDate3);
        Assert.assertTrue(valueRange3.isFixed());
        Assert.assertEquals(0L, valueRange3.getMinimum());
        Assert.assertEquals(86400999L, valueRange3.getMaximum());

        final LocalDate localDate4 = LocalDate.of(1997, 7, 1);
        final ValueRange valueRange4 = utcMilliOfDay.rangeRefinedBy(localDate4);
        Assert.assertTrue(valueRange4.isFixed());
        Assert.assertEquals(0L, valueRange4.getMinimum());
        Assert.assertEquals(86399999L, valueRange4.getMaximum());
    }

    /**
     * Tests {@link UTCFields.UTCNanoOfDay}.
     */
    @Test
    public void testUTCNanoOfDay() {
        final UTCNanoOfDay utcNanoOfDay = new UTCNanoOfDay(utc);

        final LocalDate localDate1 = LocalDate.of(1971, 12, 31);
        final ValueRange valueRange1 = utcNanoOfDay.rangeRefinedBy(localDate1);
        Assert.assertTrue(valueRange1.isFixed());
        Assert.assertEquals(0L, valueRange1.getMinimum());
        Assert.assertEquals(86400107757999L, valueRange1.getMaximum());

        final LocalDate localDate2 = LocalDate.of(1972, 1, 1);
        final ValueRange valueRange2 = utcNanoOfDay.rangeRefinedBy(localDate2);
        Assert.assertTrue(valueRange2.isFixed());
        Assert.assertEquals(0L, valueRange2.getMinimum());
        Assert.assertEquals(86399999999999L, valueRange2.getMaximum());

        final LocalDate localDate3 = LocalDate.of(1997, 6, 30);
        final ValueRange valueRange3 = utcNanoOfDay.rangeRefinedBy(localDate3);
        Assert.assertTrue(valueRange3.isFixed());
        Assert.assertEquals(0L, valueRange3.getMinimum());
        Assert.assertEquals(86400999999999L, valueRange3.getMaximum());

        final LocalDate localDate4 = LocalDate.of(1997, 7, 1);
        final ValueRange valueRange4 = utcNanoOfDay.rangeRefinedBy(localDate4);
        Assert.assertTrue(valueRange4.isFixed());
        Assert.assertEquals(0L, valueRange4.getMinimum());
        Assert.assertEquals(86399999999999L, valueRange4.getMaximum());
    }

    /**
     * Tests {@link UTCFields.UTCSecondOfDay}.
     */
    @Test
    public void testUTCSecondOfDay() {
        final UTCSecondOfDay utcSecondOfDay = new UTCSecondOfDay(utc);

        final LocalDate localDate1 = LocalDate.of(1971, 12, 31);
        final ValueRange valueRange1 = utcSecondOfDay.rangeRefinedBy(localDate1);
        Assert.assertTrue(valueRange1.isFixed());
        Assert.assertEquals(0L, valueRange1.getMinimum());
        Assert.assertEquals(86400L, valueRange1.getMaximum());

        final LocalDate localDate2 = LocalDate.of(1972, 1, 1);
        final ValueRange valueRange2 = utcSecondOfDay.rangeRefinedBy(localDate2);
        Assert.assertTrue(valueRange2.isFixed());
        Assert.assertEquals(0L, valueRange2.getMinimum());
        Assert.assertEquals(86399L, valueRange2.getMaximum());

        final LocalDate localDate3 = LocalDate.of(1997, 6, 30);
        final ValueRange valueRange3 = utcSecondOfDay.rangeRefinedBy(localDate3);
        Assert.assertTrue(valueRange3.isFixed());
        Assert.assertEquals(0L, valueRange3.getMinimum());
        Assert.assertEquals(86400L, valueRange3.getMaximum());

        final LocalDate localDate4 = LocalDate.of(1997, 7, 1);
        final ValueRange valueRange4 = utcSecondOfDay.rangeRefinedBy(localDate4);
        Assert.assertTrue(valueRange4.isFixed());
        Assert.assertEquals(0L, valueRange4.getMinimum());
        Assert.assertEquals(86399L, valueRange4.getMaximum());
    }

    /**
     * Tests {@link UTCFields.UTCSecondOfMinute}.
     */
    @Test
    public void testUTCSecondOfMinute() {
        final UTCSecondOfMinute utcSecondOfMinute = new UTCSecondOfMinute(utc);

        final LocalDate localDate1 = LocalDate.of(1971, 12, 31);
        final ValueRange valueRange1 = utcSecondOfMinute.rangeRefinedBy(localDate1);
        Assert.assertTrue(valueRange1.isFixed());
        Assert.assertEquals(0L, valueRange1.getMinimum());
        Assert.assertEquals(60L, valueRange1.getMaximum());

        final LocalDate localDate2 = LocalDate.of(1972, 1, 1);
        final ValueRange valueRange2 = utcSecondOfMinute.rangeRefinedBy(localDate2);
        Assert.assertTrue(valueRange2.isFixed());
        Assert.assertEquals(0L, valueRange2.getMinimum());
        Assert.assertEquals(59L, valueRange2.getMaximum());

        final LocalDate localDate3 = LocalDate.of(1997, 6, 30);
        final ValueRange valueRange3 = utcSecondOfMinute.rangeRefinedBy(localDate3);
        Assert.assertTrue(valueRange3.isFixed());
        Assert.assertEquals(0L, valueRange3.getMinimum());
        Assert.assertEquals(60L, valueRange3.getMaximum());

        final LocalDate localDate4 = LocalDate.of(1997, 7, 1);
        final ValueRange valueRange4 = utcSecondOfMinute.rangeRefinedBy(localDate4);
        Assert.assertTrue(valueRange4.isFixed());
        Assert.assertEquals(0L, valueRange4.getMinimum());
        Assert.assertEquals(59L, valueRange4.getMaximum());
    }

}
