/* Copyright 2002-2021 CS GROUP
 * Licensed to CS GROUP (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orekit.time;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hamcrest.MatcherAssert;
import org.hipparchus.random.RandomGenerator;
import org.hipparchus.random.Well1024a;
import org.hipparchus.util.Decimal64Field;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.orekit.OrekitMatchers;
import org.orekit.Utils;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitMessages;
import org.orekit.time.UTCFields.AbstractUTCField;
import org.orekit.time.UTCFields.UTCMicroOfDay;
import org.orekit.time.UTCFields.UTCMilliOfDay;
import org.orekit.time.UTCFields.UTCNanoOfDay;
import org.orekit.time.UTCFields.UTCSecondOfDay;
import org.orekit.time.UTCFields.UTCSecondOfMinute;
import org.orekit.utils.Constants;

public class UTCScaleTest {

    @Test
    public void testAfter() {
        AbsoluteDate d1 = new AbsoluteDate(new DateComponents(2020, 12, 31),
                                           new TimeComponents(23, 59, 59),
                                           utc);
        Assert.assertEquals("2020-12-31T23:59:59.000", d1.toString());
    }

    @Test
    public void testNoLeap() {
        Assert.assertEquals("UTC", utc.toString());
        AbsoluteDate d1 = new AbsoluteDate(new DateComponents(1999, 12, 31),
                                           new TimeComponents(23, 59, 59),
                                           utc);
        AbsoluteDate d2 = new AbsoluteDate(new DateComponents(2000, 01, 01),
                                           new TimeComponents(00, 00, 01),
                                           utc);
        Assert.assertEquals(2.0, d2.durationFrom(d1), 1.0e-10);
    }

    @Test
    public void testLeap2006() {
        AbsoluteDate leapDate =
            new AbsoluteDate(new DateComponents(2006, 01, 01), TimeComponents.H00, utc);
        AbsoluteDate d1 = leapDate.shiftedBy(-1);
        AbsoluteDate d2 = leapDate.shiftedBy(+1);
        Assert.assertEquals(2.0, d2.durationFrom(d1), 1.0e-10);

        AbsoluteDate d3 = new AbsoluteDate(new DateComponents(2005, 12, 31),
                                           new TimeComponents(23, 59, 59),
                                           utc);
        AbsoluteDate d4 = new AbsoluteDate(new DateComponents(2006, 01, 01),
                                           new TimeComponents(00, 00, 01),
                                           utc);
        Assert.assertEquals(3.0, d4.durationFrom(d3), 1.0e-10);
    }

    @Test
    public void testDuringLeap() {
        AbsoluteDate d = new AbsoluteDate(new DateComponents(1983, 06, 30),
                                          new TimeComponents(23, 59, 59),
                                          utc);
        Assert.assertEquals("1983-06-30T23:58:59.000", d.shiftedBy(-60).toString(utc));
        Assert.assertEquals(60, utc.minuteDuration(d.shiftedBy(-60)));
        Assert.assertFalse(utc.insideLeap(d.shiftedBy(-60)));
        Assert.assertEquals("1983-06-30T23:59:59.000", d.toString(utc));
        Assert.assertEquals(61, utc.minuteDuration(d));
        Assert.assertFalse(utc.insideLeap(d));
        d = d.shiftedBy(0.251);
        Assert.assertEquals("1983-06-30T23:59:59.251", d.toString(utc));
        Assert.assertEquals(61, utc.minuteDuration(d));
        Assert.assertFalse(utc.insideLeap(d));
        d = d.shiftedBy(0.251);
        Assert.assertEquals("1983-06-30T23:59:59.502", d.toString(utc));
        Assert.assertEquals(61, utc.minuteDuration(d));
        Assert.assertFalse(utc.insideLeap(d));
        d = d.shiftedBy(0.251);
        Assert.assertEquals("1983-06-30T23:59:59.753", d.toString(utc));
        Assert.assertEquals(61, utc.minuteDuration(d));
        Assert.assertFalse(utc.insideLeap(d));
        d = d.shiftedBy( 0.251);
        Assert.assertEquals("1983-06-30T23:59:60.004", d.toString(utc));
        Assert.assertEquals(61, utc.minuteDuration(d));
        Assert.assertTrue(utc.insideLeap(d));
        d = d.shiftedBy(0.251);
        Assert.assertEquals("1983-06-30T23:59:60.255", d.toString(utc));
        Assert.assertEquals(61, utc.minuteDuration(d));
        Assert.assertTrue(utc.insideLeap(d));
        d = d.shiftedBy(0.251);
        Assert.assertEquals("1983-06-30T23:59:60.506", d.toString(utc));
        Assert.assertEquals(61, utc.minuteDuration(d));
        d = d.shiftedBy(0.251);
        Assert.assertEquals("1983-06-30T23:59:60.757", d.toString(utc));
        Assert.assertEquals(61, utc.minuteDuration(d));
        Assert.assertTrue(utc.insideLeap(d));
        d = d.shiftedBy(0.251);
        Assert.assertEquals("1983-07-01T00:00:00.008", d.toString(utc));
        Assert.assertEquals(60, utc.minuteDuration(d));
        Assert.assertFalse(utc.insideLeap(d));
    }

    @Test
    public void testWrapBeforeLeap() {
        AbsoluteDate t = new AbsoluteDate("2015-06-30T23:59:59.999999", utc);
        Assert.assertEquals("2015-06-30T23:59:60.000", t.toString(utc));
    }

    @Test
    public void testMinuteDuration() {
        final AbsoluteDate t0 = new AbsoluteDate("1983-06-30T23:58:59.000", utc);
        for (double dt = 0; dt < 63; dt += 0.3) {
            if (dt < 1.0) {
                // before the minute of the leap
                Assert.assertEquals(60, utc.minuteDuration(t0.shiftedBy(dt)));
            } else if (dt < 62.0) {
                // during the minute of the leap
                Assert.assertEquals(61, utc.minuteDuration(t0.shiftedBy(dt)));
            } else {
                // after the minute of the leap
                Assert.assertEquals(60, utc.minuteDuration(t0.shiftedBy(dt)));
            }
        }
    }

    /**
     * Check the consistency of minute duration with the other data in each offset. Checks
     * table hard coded in UTCScale.
     *
     * @throws ReflectiveOperationException on error.
     */
    @Test
    public void testMinuteDurationConsistentWithLeap() throws ReflectiveOperationException {
        // setup
        // get the offsets array, makes this test easier to write
        Field field = UTCScale.class.getDeclaredField("offsets");
        field.setAccessible(true);
        UTCTAIOffset[] offsets = (UTCTAIOffset[]) field.get(utc);

        // action
        for (UTCTAIOffset offset : offsets) {
            // average of start and end of leap second, definitely inside
            final AbsoluteDate start = offset.getDate();
            final AbsoluteDate end = offset.getValidityStart();
            AbsoluteDate d = start.shiftedBy(end.durationFrom(start) / 2.0);
            int excess = utc.minuteDuration(d) - 60;
            double leap = offset.getLeap();
            // verify
            Assert.assertTrue(
                    "at MJD" + offset.getMJD() + ": " + leap + " <= " + excess,
                    leap <= excess);
            Assert.assertTrue(leap > (excess - 1));
            // before the leap starts but still in the same minute
            d = start.shiftedBy(-30);
            int newExcess = utc.minuteDuration(d) - 60;
            double newLeap = offset.getLeap();
            // verify
            Assert.assertTrue(
                    "at MJD" + offset.getMJD() + ": " + newLeap + " <= " + newExcess,
                    newLeap <= newExcess);
            Assert.assertTrue(leap > (excess - 1));
            Assert.assertEquals(excess, newExcess);
            Assert.assertEquals(leap, newLeap, 0.0);
            MatcherAssert.assertThat("" + offset.getValidityStart(), leap,
                    OrekitMatchers.numberCloseTo(end.durationFrom(start), 1e-16, 1));
        }
    }

    @Test
    public void testSymmetry() {
        TimeScale scale = TimeScalesFactory.getGPS();
        for (double dt = -10000; dt < 10000; dt += 123.456789) {
            AbsoluteDate date = AbsoluteDate.J2000_EPOCH.shiftedBy(dt * Constants.JULIAN_DAY);
            double dt1 = scale.offsetFromTAI(date);
            DateTimeComponents components = date.getComponents(scale);
            double dt2 = scale.offsetToTAI(components.getDate(), components.getTime());
            Assert.assertEquals( 0.0, dt1 + dt2, 1.0e-10);
        }
    }

    @Test
    public void testOffsets() {

        // we arbitrary put UTC == TAI before 1961-01-01
        checkOffset(1950,  1,  1,   0);

        // excerpt from UTC-TAI.history file:
        //  1961  Jan.  1 - 1961  Aug.  1     1.422 818 0s + (MJD - 37 300) x 0.001 296s
        //        Aug.  1 - 1962  Jan.  1     1.372 818 0s +        ""
        //  1962  Jan.  1 - 1963  Nov.  1     1.845 858 0s + (MJD - 37 665) x 0.001 123 2s
        //  1963  Nov.  1 - 1964  Jan.  1     1.945 858 0s +        ""
        //  1964  Jan.  1 -       April 1     3.240 130 0s + (MJD - 38 761) x 0.001 296s
        //        April 1 -       Sept. 1     3.340 130 0s +        ""
        //        Sept. 1 - 1965  Jan.  1     3.440 130 0s +        ""
        //  1965  Jan.  1 -       March 1     3.540 130 0s +        ""
        //        March 1 -       Jul.  1     3.640 130 0s +        ""
        //        Jul.  1 -       Sept. 1     3.740 130 0s +        ""
        //        Sept. 1 - 1966  Jan.  1     3.840 130 0s +        ""
        //  1966  Jan.  1 - 1968  Feb.  1     4.313 170 0s + (MJD - 39 126) x 0.002 592s
        //  1968  Feb.  1 - 1972  Jan.  1     4.213 170 0s +        ""
        checkOffset(1961,  1,  2,  -(1.422818 +   1 * 0.001296));  // MJD 37300 +   1
        checkOffset(1961,  8,  2,  -(1.372818 + 213 * 0.001296));  // MJD 37300 + 213
        checkOffset(1962,  1,  2,  -(1.845858 +   1 * 0.0011232)); // MJD 37665 +   1
        checkOffset(1963, 11,  2,  -(1.945858 + 670 * 0.0011232)); // MJD 37665 + 670
        checkOffset(1964,  1,  2,  -(3.240130 - 365 * 0.001296));  // MJD 38761 - 365
        checkOffset(1964,  4,  2,  -(3.340130 - 274 * 0.001296));  // MJD 38761 - 274
        checkOffset(1964,  9,  2,  -(3.440130 - 121 * 0.001296));  // MJD 38761 - 121
        checkOffset(1965,  1,  2,  -(3.540130 +   1 * 0.001296));  // MJD 38761 +   1
        checkOffset(1965,  3,  2,  -(3.640130 +  60 * 0.001296));  // MJD 38761 +  60
        checkOffset(1965,  7,  2,  -(3.740130 + 182 * 0.001296));  // MJD 38761 + 182
        checkOffset(1965,  9,  2,  -(3.840130 + 244 * 0.001296));  // MJD 38761 + 244
        checkOffset(1966,  1,  2,  -(4.313170 +   1 * 0.002592));  // MJD 39126 +   1
        checkOffset(1968,  2,  2,  -(4.213170 + 762 * 0.002592));  // MJD 39126 + 762

        // since 1972-01-01, offsets are only whole seconds
        checkOffset(1972,  3,  5, -10);
        checkOffset(1972,  7, 14, -11);
        checkOffset(1979, 12, 31, -18);
        checkOffset(1980,  1, 22, -19);
        checkOffset(2006,  7,  7, -33);

    }

    private void checkOffset(int year, int month, int day, double offset) {
        AbsoluteDate date = new AbsoluteDate(year, month, day, utc);
        Assert.assertEquals(offset, utc.offsetFromTAI(date), 1.0e-10);
    }

    @Test
    public void testCreatingInLeapDateUTC() {
        AbsoluteDate previous = null;
        final double step = 0.0625;
        for (double seconds = 59.0; seconds < 61.0; seconds += step) {
            final AbsoluteDate date = new AbsoluteDate(2008, 12, 31, 23, 59, seconds, utc);
            if (previous != null) {
                Assert.assertEquals(step, date.durationFrom(previous), 1.0e-12);
            }
            previous = date;
        }
        AbsoluteDate ad0 = new AbsoluteDate("2008-12-31T23:59:60", utc);
        Assert.assertTrue(ad0.toString(utc).startsWith("2008-12-31T23:59:"));
        AbsoluteDate ad1 = new AbsoluteDate("2008-12-31T23:59:59", utc).shiftedBy(1);
        Assert.assertEquals(0, ad1.durationFrom(ad0), 1.0e-15);
        Assert.assertEquals(1, new AbsoluteDate("2009-01-01T00:00:00", utc).durationFrom(ad0), 1.0e-15);
        Assert.assertEquals(2, new AbsoluteDate("2009-01-01T00:00:01", utc).durationFrom(ad0), 1.0e-15);
    }

    @Test
    public void testCreatingInLeapDateLocalTime50HoursWest() {
        // yes, I know, there are no time zones 50 hours West of UTC, this is a stress test
        AbsoluteDate previous = null;
        final double step = 0.0625;
        for (double seconds = 59.0; seconds < 61.0; seconds += step) {
            final AbsoluteDate date = new AbsoluteDate(new DateComponents(2008, 12, 29),
                                                       new TimeComponents(21, 59, seconds, -50 * 60),
                                                       utc);
            if (previous != null) {
                Assert.assertEquals(step, date.durationFrom(previous), 1.0e-12);
            }
            previous = date;
        }
        AbsoluteDate ad0 = new AbsoluteDate("2008-12-29T21:59:60-50:00", utc);
        Assert.assertTrue(ad0.toString(utc).startsWith("2008-12-31T23:59:"));
        AbsoluteDate ad1 = new AbsoluteDate("2008-12-29T21:59:59-50:00", utc).shiftedBy(1);
        Assert.assertEquals(0, ad1.durationFrom(ad0), 1.0e-15);
        Assert.assertEquals(1, new AbsoluteDate("2008-12-29T22:00:00-50:00", utc).durationFrom(ad0), 1.0e-15);
        Assert.assertEquals(2, new AbsoluteDate("2008-12-29T22:00:01-50:00", utc).durationFrom(ad0), 1.0e-15);
    }

    @Test
    public void testCreatingInLeapDateLocalTime50HoursEast() {
        // yes, I know, there are no time zones 50 hours East of UTC, this is a stress test
        AbsoluteDate previous = null;
        final double step = 0.0625;
        for (double seconds = 59.0; seconds < 61.0; seconds += step) {
            final AbsoluteDate date = new AbsoluteDate(new DateComponents(2009, 1, 3),
                                                       new TimeComponents(1, 59, seconds, +50 * 60),
                                                       utc);
            if (previous != null) {
                Assert.assertEquals(step, date.durationFrom(previous), 1.0e-12);
            }
            previous = date;
        }
        AbsoluteDate ad0 = new AbsoluteDate("2009-01-03T01:59:60+50:00", utc);
        Assert.assertTrue(ad0.toString(utc).startsWith("2008-12-31T23:59:"));
        AbsoluteDate ad1 = new AbsoluteDate("2009-01-03T01:59:59+50:00", utc).shiftedBy(1);
        Assert.assertEquals(0, ad1.durationFrom(ad0), 1.0e-15);
        Assert.assertEquals(1, new AbsoluteDate("2009-01-03T02:00:00+50:00", utc).durationFrom(ad0), 1.0e-15);
        Assert.assertEquals(2, new AbsoluteDate("2009-01-03T02:00:01+50:00", utc).durationFrom(ad0), 1.0e-15);
    }

    @Test
    public void testDisplayDuringLeap() {
        AbsoluteDate t0 = utc.getLastKnownLeapSecond().shiftedBy(-1.0);
        for (double dt = 0.0; dt < 3.0; dt += 0.375) {
            AbsoluteDate t = t0.shiftedBy(dt);
            double seconds = t.getComponents(utc).getTime().getSecond();
            if (dt < 2.0) {
                Assert.assertEquals(dt + 59.0, seconds, 1.0e-12);
            } else {
                Assert.assertEquals(dt - 2.0, seconds, 1.0e-12);
            }
        }
    }

    @Test
    public void testMultithreading() {

        // generate reference offsets using a single thread
        RandomGenerator random = new Well1024a(6392073424l);
        List<AbsoluteDate> datesList = new ArrayList<AbsoluteDate>();
        List<Double> offsetsList = new ArrayList<Double>();
        AbsoluteDate reference = utc.getFirstKnownLeapSecond().shiftedBy(-Constants.JULIAN_YEAR);
        double testRange = utc.getLastKnownLeapSecond().durationFrom(reference) + Constants.JULIAN_YEAR;
        for (int i = 0; i < 10000; ++i) {
            AbsoluteDate randomDate = reference.shiftedBy(random.nextDouble() * testRange);
            datesList.add(randomDate);
            offsetsList.add(utc.offsetFromTAI(randomDate));
        }

        // check the offsets in multi-threaded mode
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int i = 0; i < datesList.size(); ++i) {
            final AbsoluteDate date = datesList.get(i);
            final double offset = offsetsList.get(i);
            executorService.execute(new Runnable() {
                public void run() {
                    Assert.assertEquals(offset, utc.offsetFromTAI(date), 1.0e-12);
                }
            });
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            Assert.fail(ie.getLocalizedMessage());
        }

    }

    @Test
    public void testIssue89() {
        AbsoluteDate firstDayLastLeap = utc.getLastKnownLeapSecond().shiftedBy(10.0);
        AbsoluteDate rebuilt = new AbsoluteDate(firstDayLastLeap.toString(utc), utc);
        Assert.assertEquals(0.0, rebuilt.durationFrom(firstDayLastLeap), 1.0e-12);
    }

    @Test
    public void testOffsetToTAIBeforeFirstLeapSecond() {
        TimeScale scale = TimeScalesFactory.getUTC();
        // time before first leap second
        DateComponents dateComponents = new DateComponents(1950, 1, 1);
        double actual = scale.offsetToTAI(dateComponents, TimeComponents.H00);
        Assert.assertEquals(0.0, actual, 1.0e-10);
    }

    @Test
    public void testEmptyOffsets() {
        Utils.setDataRoot("no-data");

        TimeScalesFactory.addUTCTAIOffsetsLoader(new UTCTAIOffsetsLoader() {
            public List<OffsetModel> loadOffsets() {
                return Collections.emptyList();
            }
        });

        try {
            TimeScalesFactory.getUTC();
            Assert.fail("an exception should have been thrown");
        } catch (OrekitException oe) {
            Assert.assertEquals(OrekitMessages.NO_IERS_UTC_TAI_HISTORY_DATA_LOADED, oe.getSpecifier());
        }

    }

    @Test
    public void testInfinityRegularDate() {
        TimeScale scale = TimeScalesFactory.getUTC();
        Assert.assertEquals(-36.0,
                            scale.offsetFromTAI(AbsoluteDate.FUTURE_INFINITY),
                            1.0e-15);
        Assert.assertEquals(0.0,
                            scale.offsetFromTAI(AbsoluteDate.PAST_INFINITY),
                            1.0e-15);
    }

    @Test
    public void testInfinityFieldDate() {
        TimeScale scale = TimeScalesFactory.getUTC();
        Assert.assertEquals(-36.0,
                            scale.offsetFromTAI(FieldAbsoluteDate.getFutureInfinity(Decimal64Field.getInstance())).getReal(),
                            1.0e-15);
        Assert.assertEquals(0.0,
                            scale.offsetFromTAI(FieldAbsoluteDate.getPastInfinity(Decimal64Field.getInstance())).getReal(),
                            1.0e-15);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        UTCScale utc = TimeScalesFactory.getUTC();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(bos);
        oos.writeObject(utc);

        Assert.assertTrue(bos.size() > 50);
        Assert.assertTrue(bos.size() < 100);

        ByteArrayInputStream  bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream     ois = new ObjectInputStream(bis);
        UTCScale deserialized  = (UTCScale) ois.readObject();
        Assert.assertTrue(utc == deserialized);

    }

    @Test
    public void testFirstAndLast() {
        // action
        AbsoluteDate first = utc.getFirstKnownLeapSecond();
        AbsoluteDate last = utc.getLastKnownLeapSecond();

        // verify
        //AbsoluteDate d = new AbsoluteDate(1961, 1, 1, utc);
        Assert.assertEquals(new AbsoluteDate(2015, 6, 30, 23, 59, 60, utc), last);
        Assert.assertEquals(new AbsoluteDate(1960, 12, 31, 23, 59, 60, utc), first);
    }

    @Test
    public void testGetUTCTAIOffsets() {
        final List<UTCTAIOffset> offsets = utc.getUTCTAIOffsets();
        Assert.assertEquals(40, offsets.size());
        final UTCTAIOffset firstOffset = offsets.get(0);
        final UTCTAIOffset lastOffset = offsets.get(offsets.size() - 1);
        Assert.assertEquals(37300, firstOffset.getMJD()); // 1961-01-01
        Assert.assertEquals(57204, lastOffset.getMJD()); // 2015-07-01
    }

    @Test
    public void testGetTAIMinusUTCAdjustment() {
        Assert.assertEquals(1., utc.getTAIMinusUTCAdjustment(57204), 0.); // 2015-07-01
        Assert.assertEquals(0., utc.getTAIMinusUTCAdjustment(57203), 0.); // 2015-06-30
        Assert.assertEquals(1., utc.getTAIMinusUTCAdjustment(49169), 0.); // 1993-07-01
        Assert.assertEquals(0., utc.getTAIMinusUTCAdjustment(49168), 0.); // 1993-06-30
        Assert.assertEquals(1., utc.getTAIMinusUTCAdjustment(41499), 0.); // 1972-07-01
        Assert.assertEquals(0., utc.getTAIMinusUTCAdjustment(41498), 0.); // 1972-06-30
        Assert.assertEquals(0.107758, utc.getTAIMinusUTCAdjustment(41317), 1e-14); // 1972-01-01
        Assert.assertEquals(0., utc.getTAIMinusUTCAdjustment(41316), 0.); // 1971-12-31
        Assert.assertEquals(0.1, utc.getTAIMinusUTCAdjustment(38639), 2e-16); // 1964-09-01
        Assert.assertEquals(0., utc.getTAIMinusUTCAdjustment(38638), 0.); // 1964-08-31
        Assert.assertEquals(1.422818, utc.getTAIMinusUTCAdjustment(37300), 0.); // 1960-01-01
        Assert.assertEquals(0., utc.getTAIMinusUTCAdjustment(37299), 0.); // 1959-12-31

    }

    @Test
    public void testDateToTemporal() {
        // TemporalAccessor.isSupported(TemporalField)
        final TemporalAccessor temporal1 = utc.dateToTemporal(AbsoluteDate.ARBITRARY_EPOCH);
        final Set<ChronoField> unsupportedChronoFields = EnumSet.of(
                ChronoField.NANO_OF_DAY,
                ChronoField.MICRO_OF_DAY,
                ChronoField.MILLI_OF_DAY,
                ChronoField.SECOND_OF_MINUTE,
                ChronoField.SECOND_OF_DAY,
                ChronoField.INSTANT_SECONDS);
        for (ChronoField chronoField : ChronoField.values()) {
            Assert.assertEquals(!unsupportedChronoFields.contains(chronoField), temporal1.isSupported(chronoField));
        }
        Assert.assertTrue(temporal1.isSupported(new UTCMicroOfDay(utc)));
        Assert.assertTrue(temporal1.isSupported(new UTCMilliOfDay(utc)));
        Assert.assertTrue(temporal1.isSupported(new UTCNanoOfDay(utc)));
        Assert.assertTrue(temporal1.isSupported(new UTCSecondOfDay(utc)));
        Assert.assertTrue(temporal1.isSupported(new UTCSecondOfMinute(utc)));
        Assert.assertFalse(temporal1.isSupported(new AbstractUTCField(utc, ChronoUnit.CENTURIES, ChronoUnit.MILLENNIA) {
            @Override
            public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
                return null;
            }
        }));
        Assert.assertTrue(temporal1.isSupported(JulianFields.MODIFIED_JULIAN_DAY));

        // TemporalAccessor.getLong(TemporalField)
        for (ChronoField chronoField : unsupportedChronoFields) {
            Assert.assertThrows(UnsupportedTemporalTypeException.class, () -> temporal1.getLong(chronoField));
        }
        final UTCScale utc2 = new UTCScale(TimeScalesFactory.getTAI(),
                Collections.singletonList(new OffsetModel(new DateComponents(1972, 1, 1), 10)));
        final TemporalAccessor temporal2 = utc.dateToTemporal(AbsoluteDate.ARBITRARY_EPOCH);
        Assert.assertThrows(IllegalArgumentException.class, () -> temporal2.getLong(new UTCSecondOfDay(utc2)));
        Assert.assertThrows(UnsupportedTemporalTypeException.class,
                () -> temporal1.getLong(new AbstractUTCField(utc, ChronoUnit.CENTURIES, ChronoUnit.MILLENNIA) {
                    @Override
                    public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
                        return null;
                    }
                }));

        // 1971-12-31T23:59:59.500 UTC, 1972-01-01T00:00:09.392241985002414 TAI
        // TAI-UTC adjustment at end of day was +0.107758 seconds
        final AbsoluteDate date3 = new AbsoluteDate(1972, 1, 1, 0, 0, 9.392241985002414, TimeScalesFactory.getTAI());
        final TemporalAccessor temporal3 = utc.dateToTemporal(date3);
        Assert.assertEquals(3L, temporal3.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(1L, temporal3.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(5L, temporal3.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(53L, temporal3.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(1L, temporal3.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(11L, temporal3.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal3.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(31L, temporal3.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(5L, temporal3.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(365L, temporal3.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(729L, temporal3.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal3.getLong(ChronoField.ERA));
        Assert.assertEquals(11L, temporal3.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal3.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(500L, temporal3.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(1439L, temporal3.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(59L, temporal3.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(12L, temporal3.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(500_000_000L, temporal3.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal3.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_663L, temporal3.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1971L, temporal3.getLong(ChronoField.YEAR));
        Assert.assertEquals(1971L, temporal3.getLong(ChronoField.YEAR_OF_ERA));
        Assert.assertEquals(86_399_500_000L, temporal3.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(86_399_500L, temporal3.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(86_399_500_000_000L, temporal3.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(86_399L, temporal3.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(59L, temporal3.getLong(new UTCSecondOfMinute(utc)));
        Assert.assertEquals(41316L, temporal3.getLong(JulianFields.MODIFIED_JULIAN_DAY));

        // 1971-12-31T23:59:60.100 UTC, 1972-01-01T00:00:09.992242003011143 TAI
        // TAI-UTC adjustment at end of day was +0.107758 seconds
        final AbsoluteDate date4 = new AbsoluteDate(1972, 1, 1, 0, 0, 9.992242003011143, TimeScalesFactory.getTAI());
        final TemporalAccessor temporal4 = utc.dateToTemporal(date4);
        Assert.assertEquals(3L, temporal4.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(1L, temporal4.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(5L, temporal4.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(53L, temporal4.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(1L, temporal4.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(11L, temporal4.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal4.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(31L, temporal4.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(5L, temporal4.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(365L, temporal4.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(729L, temporal4.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal4.getLong(ChronoField.ERA));
        Assert.assertEquals(11L, temporal4.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal4.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(100L, temporal4.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(1439L, temporal4.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(59L, temporal4.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(12L, temporal4.getLong(ChronoField.MONTH_OF_YEAR));
        // FIXME The commented-out test is close to passing (nano-of-second is 100000003).
        // Assert.assertEquals(100_000_000L, temporal.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal4.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_663L, temporal4.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1971L, temporal4.getLong(ChronoField.YEAR));
        Assert.assertEquals(1971L, temporal4.getLong(ChronoField.YEAR_OF_ERA));
        Assert.assertEquals(86_400_100_000L, temporal4.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(86_400_100L, temporal4.getLong(new UTCMilliOfDay(utc)));
        // FIXME The commented-out test is close to passing (nano-of-day is 86400100000003).
        // Assert.assertEquals(86_400_100_000_000L, temporal.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(86_400L, temporal4.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(60L, temporal4.getLong(new UTCSecondOfMinute(utc)));
        Assert.assertEquals(41316L, temporal4.getLong(JulianFields.MODIFIED_JULIAN_DAY));

        // 1997-06-30T23:59:59.500 UTC, 1997-07-01T00:00:29.500 TAI
        // TAI-UTC adjustment at end of day was +1 seconds
        final AbsoluteDate date5 = new AbsoluteDate(1997, 7, 1, 0, 0, 29.5, TimeScalesFactory.getTAI());
        final TemporalAccessor temporal5 = utc.dateToTemporal(date5);
        Assert.assertEquals(2L, temporal5.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(6L, temporal5.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(5L, temporal5.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(26L, temporal5.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(1L, temporal5.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(11L, temporal5.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal5.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(30L, temporal5.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(1L, temporal5.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(181L, temporal5.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(10_042L, temporal5.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal5.getLong(ChronoField.ERA));
        Assert.assertEquals(11L, temporal5.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal5.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(500L, temporal5.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(1439L, temporal5.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(59L, temporal5.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(6L, temporal5.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(500_000_000L, temporal5.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal5.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_969L, temporal5.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1997L, temporal5.getLong(ChronoField.YEAR));
        Assert.assertEquals(1997L, temporal5.getLong(ChronoField.YEAR_OF_ERA));
        Assert.assertEquals(86_399_500_000L, temporal5.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(86_399_500L, temporal5.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(86_399_500_000_000L, temporal5.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(86_399L, temporal5.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(59L, temporal5.getLong(new UTCSecondOfMinute(utc)));
        Assert.assertEquals(50629L, temporal5.getLong(JulianFields.MODIFIED_JULIAN_DAY));

        // 1997-06-30T23:59:60.500 UTC, 1997-07-01T00:00:30.500 TAI
        // TAI-UTC adjustment at end of day was +1 seconds
        final AbsoluteDate date6 = new AbsoluteDate(1997, 7, 1, 0, 0, 30.5, TimeScalesFactory.getTAI());
        final TemporalAccessor temporal6 = utc.dateToTemporal(date6);
        Assert.assertEquals(2L, temporal6.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(6L, temporal6.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(5L, temporal6.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(26L, temporal6.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(1L, temporal6.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(11L, temporal6.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal6.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(30L, temporal6.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(1L, temporal6.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(181L, temporal6.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(10_042L, temporal6.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal6.getLong(ChronoField.ERA));
        Assert.assertEquals(11L, temporal6.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(23L, temporal6.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(500L, temporal6.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(1439L, temporal6.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(59L, temporal6.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(6L, temporal6.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(500_000_000L, temporal6.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal6.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_969L, temporal6.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1997L, temporal6.getLong(ChronoField.YEAR));
        Assert.assertEquals(1997L, temporal6.getLong(ChronoField.YEAR_OF_ERA));
        Assert.assertEquals(86_400_500_000L, temporal6.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(86_400_500L, temporal6.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(86_400_500_000_000L, temporal6.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(86_400L, temporal6.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(60L, temporal6.getLong(new UTCSecondOfMinute(utc)));
        Assert.assertEquals(50629L, temporal6.getLong(JulianFields.MODIFIED_JULIAN_DAY));

        // 1997-07-01T00:00:00.500 UTC, 1997-07-01T00:00:31.500 TAI
        // TAI-UTC adjustment at beginning of day was +1 seconds
        final AbsoluteDate date7 = new AbsoluteDate(1997, 7, 1, 0, 0, 31.5, TimeScalesFactory.getTAI());
        final TemporalAccessor temporal7 = utc.dateToTemporal(date7);
        Assert.assertEquals(1L, temporal7.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(7L, temporal7.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(1L, temporal7.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(26L, temporal7.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(0L, temporal7.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(12L, temporal7.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(24L, temporal7.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(1L, temporal7.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(2L, temporal7.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(182L, temporal7.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(10_043L, temporal7.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal7.getLong(ChronoField.ERA));
        Assert.assertEquals(0L, temporal7.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(0L, temporal7.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(500L, temporal7.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(0L, temporal7.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(0L, temporal7.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(7L, temporal7.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(500_000_000L, temporal7.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal7.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(23_970L, temporal7.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(1997L, temporal7.getLong(ChronoField.YEAR));
        Assert.assertEquals(1997L, temporal7.getLong(ChronoField.YEAR_OF_ERA));
        Assert.assertEquals(500_000L, temporal7.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(500L, temporal7.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(500_000_000L, temporal7.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(0L, temporal7.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(0L, temporal7.getLong(new UTCSecondOfMinute(utc)));
        Assert.assertEquals(50630L, temporal7.getLong(JulianFields.MODIFIED_JULIAN_DAY));

        // 2006-04-14T09:55:38.8327681089 UTC, 2006-04-14T09:56:11.8327681089 TAI
        // Test fields are truncated, not rounded.
        final AbsoluteDate date8 = new AbsoluteDate(2006, 4, 14, 9, 56, 11.8327681089, TimeScalesFactory.getTAI());
        final TemporalAccessor temporal8 = utc.dateToTemporal(date8);
        Assert.assertEquals(7L, temporal8.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        Assert.assertEquals(6L, temporal8.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        Assert.assertEquals(2L, temporal8.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        Assert.assertEquals(15L, temporal8.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        Assert.assertEquals(0L, temporal8.getLong(ChronoField.AMPM_OF_DAY));
        Assert.assertEquals(9L, temporal8.getLong(ChronoField.CLOCK_HOUR_OF_AMPM));
        Assert.assertEquals(9L, temporal8.getLong(ChronoField.CLOCK_HOUR_OF_DAY));
        Assert.assertEquals(14L, temporal8.getLong(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(5L, temporal8.getLong(ChronoField.DAY_OF_WEEK));
        Assert.assertEquals(104L, temporal8.getLong(ChronoField.DAY_OF_YEAR));
        Assert.assertEquals(13_252L, temporal8.getLong(ChronoField.EPOCH_DAY));
        Assert.assertEquals(1L, temporal8.getLong(ChronoField.ERA));
        Assert.assertEquals(9L, temporal8.getLong(ChronoField.HOUR_OF_AMPM));
        Assert.assertEquals(9L, temporal8.getLong(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(832L, temporal8.getLong(ChronoField.MILLI_OF_SECOND));
        Assert.assertEquals(595L, temporal8.getLong(ChronoField.MINUTE_OF_DAY));
        Assert.assertEquals(55L, temporal8.getLong(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(4L, temporal8.getLong(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(832_768_108L, temporal8.getLong(ChronoField.NANO_OF_SECOND));
        Assert.assertEquals(0L, temporal8.getLong(ChronoField.OFFSET_SECONDS));
        Assert.assertEquals(24_075L, temporal8.getLong(ChronoField.PROLEPTIC_MONTH));
        Assert.assertEquals(2006L, temporal8.getLong(ChronoField.YEAR));
        Assert.assertEquals(2006L, temporal8.getLong(ChronoField.YEAR_OF_ERA));
        Assert.assertEquals(35_738_832_768L, temporal8.getLong(new UTCMicroOfDay(utc)));
        Assert.assertEquals(35_738_832L, temporal8.getLong(new UTCMilliOfDay(utc)));
        Assert.assertEquals(35_738_832_768_108L, temporal8.getLong(new UTCNanoOfDay(utc)));
        Assert.assertEquals(35_738L, temporal8.getLong(new UTCSecondOfDay(utc)));
        Assert.assertEquals(38L, temporal8.getLong(new UTCSecondOfMinute(utc)));
        Assert.assertEquals(53839L, temporal8.getLong(JulianFields.MODIFIED_JULIAN_DAY));
    }

    @Before
    public void setUp() {
        Utils.setDataRoot("regular-data");
        utc = TimeScalesFactory.getUTC();
    }

    @After
    public void tearDown() {
        utc = null;
    }

    private UTCScale utc;

}
