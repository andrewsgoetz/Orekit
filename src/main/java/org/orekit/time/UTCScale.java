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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.hipparchus.RealFieldElement;
import org.hipparchus.util.FastMath;
import org.orekit.annotation.DefaultDataContext;
import org.orekit.data.DataContext;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitInternalError;
import org.orekit.time.UTCFields.UTCField;
import org.orekit.time.UTCFields.UTCMicroOfDay;
import org.orekit.time.UTCFields.UTCMilliOfDay;
import org.orekit.time.UTCFields.UTCNanoOfDay;
import org.orekit.time.UTCFields.UTCSecondOfDay;
import org.orekit.time.UTCFields.UTCSecondOfMinute;
import org.orekit.utils.Constants;

/** Coordinated Universal Time.
 * <p>UTC is related to TAI using step adjustments from time to time
 * according to IERS (International Earth Rotation Service) rules. Before 1972,
 * these adjustments were piecewise linear offsets. Since 1972, these adjustments
 * are piecewise constant offsets, which require introduction of leap seconds.</p>
 * <p>Leap seconds are always inserted as additional seconds at the last minute
 * of the day, pushing the next day forward. Such minutes are therefore more
 * than 60 seconds long. In theory, there may be seconds removal instead of seconds
 * insertion, but up to now (2010) it has never been used. As an example, when a
 * one second leap was introduced at the end of 2005, the UTC time sequence was
 * 2005-12-31T23:59:59 UTC, followed by 2005-12-31T23:59:60 UTC, followed by
 * 2006-01-01T00:00:00 UTC.</p>
 * <p>This is intended to be accessed thanks to {@link TimeScales},
 * so there is no public constructor.</p>
 * @author Luc Maisonobe
 * @see AbsoluteDate
 */
public class UTCScale implements TimeScale {

    /** Serializable UID. */
    private static final long serialVersionUID = 20150402L;

    /** UTC-TAI offsets. */
    private UTCTAIOffset[] offsets;

    /** Default formatter. */
    private DateTimeFormatter defaultFormatter;

    /** Package private constructor for the factory.
     * Used to create the prototype instance of this class that is used to
     * clone all subsequent instances of {@link UTCScale}. Initializes the offset
     * table that is shared among all instances.
     * @param tai TAI time scale this UTC time scale references.
     * @param offsets UTC-TAI offsets
     */
    UTCScale(final TimeScale tai, final Collection<? extends OffsetModel> offsets) {
        // copy input so the original list is unmodified
        final List<OffsetModel> offsetModels = new ArrayList<>(offsets);
        offsetModels.sort(Comparator.comparing(OffsetModel::getStart));
        if (offsetModels.get(0).getStart().getYear() > 1968) {
            // the pre-1972 linear offsets are missing, add them manually
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
            offsetModels.add( 0, new OffsetModel(new DateComponents(1961,  1, 1), 37300, 1.4228180, 0.0012960));
            offsetModels.add( 1, new OffsetModel(new DateComponents(1961,  8, 1), 37300, 1.3728180, 0.0012960));
            offsetModels.add( 2, new OffsetModel(new DateComponents(1962,  1, 1), 37665, 1.8458580, 0.0011232));
            offsetModels.add( 3, new OffsetModel(new DateComponents(1963, 11, 1), 37665, 1.9458580, 0.0011232));
            offsetModels.add( 4, new OffsetModel(new DateComponents(1964,  1, 1), 38761, 3.2401300, 0.0012960));
            offsetModels.add( 5, new OffsetModel(new DateComponents(1964,  4, 1), 38761, 3.3401300, 0.0012960));
            offsetModels.add( 6, new OffsetModel(new DateComponents(1964,  9, 1), 38761, 3.4401300, 0.0012960));
            offsetModels.add( 7, new OffsetModel(new DateComponents(1965,  1, 1), 38761, 3.5401300, 0.0012960));
            offsetModels.add( 8, new OffsetModel(new DateComponents(1965,  3, 1), 38761, 3.6401300, 0.0012960));
            offsetModels.add( 9, new OffsetModel(new DateComponents(1965,  7, 1), 38761, 3.7401300, 0.0012960));
            offsetModels.add(10, new OffsetModel(new DateComponents(1965,  9, 1), 38761, 3.8401300, 0.0012960));
            offsetModels.add(11, new OffsetModel(new DateComponents(1966,  1, 1), 39126, 4.3131700, 0.0025920));
            offsetModels.add(12, new OffsetModel(new DateComponents(1968,  2, 1), 39126, 4.2131700, 0.0025920));
        }

        // create cache
        this.offsets = new UTCTAIOffset[offsetModels.size()];

        UTCTAIOffset previous = null;

        // link the offsets together
        for (int i = 0; i < offsetModels.size(); ++i) {

            final OffsetModel    o      = offsetModels.get(i);
            final DateComponents date   = o.getStart();
            final int            mjdRef = o.getMJDRef();
            final double         offset = o.getOffset();
            final double         slope  = o.getSlope();

            // start of the leap
            final double previousOffset    = (previous == null) ? 0.0 : previous.getOffset(date, TimeComponents.H00);
            final AbsoluteDate leapStart   = new AbsoluteDate(date, tai).shiftedBy(previousOffset);

            // end of the leap
            final double startOffset       = offset + slope * (date.getMJD() - mjdRef);
            final AbsoluteDate leapEnd     = new AbsoluteDate(date, tai).shiftedBy(startOffset);

            // leap computed at leap start and in UTC scale
            final double normalizedSlope   = slope / Constants.JULIAN_DAY;
            final double leap              = leapEnd.durationFrom(leapStart) / (1 + normalizedSlope);

            final AbsoluteDate reference = AbsoluteDate.createMJDDate(mjdRef, 0, tai)
                    .shiftedBy(offset);
            previous = new UTCTAIOffset(leapStart, date.getMJD(), leap, offset, mjdRef,
                    normalizedSlope, reference);
            this.offsets[i] = previous;

        }

    }

    /**
     * Returns the UTC-TAI offsets underlying this UTC scale.
     * <p>
     * Modifications to the returned list will not affect this UTC scale instance.
     * @return new non-null modifiable list of UTC-TAI offsets time-sorted from
     *         earliest to latest
     */
    public List<UTCTAIOffset> getUTCTAIOffsets() {
        final List<UTCTAIOffset> offsetList = new ArrayList<>(offsets.length);
        for (int i = 0; i < offsets.length; ++i) {
            offsetList.add(offsets[i]);
        }
        return offsetList;
    }

    /** {@inheritDoc} */
    @Override
    public double offsetFromTAI(final AbsoluteDate date) {
        final int offsetIndex = findOffsetIndex(date);
        if (offsetIndex < 0) {
            // the date is before the first known leap
            return 0;
        } else {
            return -offsets[offsetIndex].getOffset(date);
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> T offsetFromTAI(final FieldAbsoluteDate<T> date) {
        final int offsetIndex = findOffsetIndex(date.toAbsoluteDate());
        if (offsetIndex < 0) {
            // the date is before the first known leap
            return date.getField().getZero();
        } else {
            return offsets[offsetIndex].getOffset(date).negate();
        }
    }

    /** {@inheritDoc} */
    @Override
    public double offsetToTAI(final DateComponents date,
                              final TimeComponents time) {

        // take offset from local time into account, but ignoring seconds,
        // so when we parse an hour like 23:59:60.5 during leap seconds introduction,
        // we do not jump to next day
        final int minuteInDay = time.getHour() * 60 + time.getMinute() - time.getMinutesFromUTC();
        final int correction  = minuteInDay < 0 ? (minuteInDay - 1439) / 1440 : minuteInDay / 1440;

        // find close neighbors, assuming date in TAI, i.e a date earlier than real UTC date
        final int mjd = date.getMJD() + correction;
        final UTCTAIOffset offset = findOffset(mjd);
        if (offset == null) {
            // the date is before the first known leap
            return 0;
        } else {
            return offset.getOffset(date, time);
        }

    }

    /** {@inheritDoc} */
    @Override
    public AbsoluteDate temporalToDate(final TemporalAccessor temporal) {
        final int year = temporal.get(ChronoField.YEAR);
        final int monthOfYear = temporal.get(ChronoField.MONTH_OF_YEAR);
        final int dayOfMonth = temporal.get(ChronoField.DAY_OF_MONTH);
        final int hourOfDay = temporal.get(ChronoField.HOUR_OF_DAY);
        final int minuteOfHour = temporal.get(ChronoField.MINUTE_OF_HOUR);
        final int secondOfMinute = temporal.get(new UTCFields.UTCSecondOfMinute(this));
        final int nanoOfSecond = temporal.get(ChronoField.NANO_OF_SECOND);
        final double second = secondOfMinute + ((double) nanoOfSecond) / 1000000000.;
        final DateComponents dateComponents = new DateComponents(year, monthOfYear, dayOfMonth);
        final TimeComponents timeComponents = new TimeComponents(hourOfDay, minuteOfHour, second);
        final AbsoluteDate date = new AbsoluteDate(dateComponents, timeComponents, this);
        return date;
    }

    /** {@inheritDoc} */
    @Override
    public TemporalAccessor dateToTemporal(final AbsoluteDate date) {
        final DateTimeComponents dateTimeComponents = date.getComponents(this);
        return new UTCTemporalAccessor(this,
                dateTimeComponents.getDate().getYear(),
                dateTimeComponents.getDate().getMonth(),
                dateTimeComponents.getDate().getDay(),
                dateTimeComponents.getTime().getHour(),
                dateTimeComponents.getTime().getMinute(),
                dateTimeComponents.getTime().getSecond());
    }

    /** {@inheritDoc} */
    @Override
    public DateTimeFormatter getDefaultFormatter() {
        if (defaultFormatter == null) {
            defaultFormatter = new DateTimeFormatterBuilder()
                    .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                    .appendLiteral('-')
                    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                    .appendLiteral('-')
                    .appendValue(ChronoField.DAY_OF_MONTH, 2)
                    .appendLiteral('T')
                    .appendValue(ChronoField.HOUR_OF_DAY, 2)
                    .appendLiteral(':')
                    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                    .appendLiteral(':')
                    .appendValue(new UTCFields.UTCSecondOfMinute(this), 2, 19, SignStyle.NOT_NEGATIVE)
                    .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                    .toFormatter()
                    .withResolverStyle(ResolverStyle.STRICT)
                    .withChronology(IsoChronology.INSTANCE);
        }
        return defaultFormatter;
    }

    /** {@inheritDoc} */
    public String getName() {
        return "UTC";
    }

    /** {@inheritDoc} */
    public String toString() {
        return getName();
    }

    /** Get the date of the first known leap second.
     * @return date of the first known leap second
     */
    public AbsoluteDate getFirstKnownLeapSecond() {
        return offsets[0].getDate();
    }

    /** Get the date of the last known leap second.
     * @return date of the last known leap second
     */
    public AbsoluteDate getLastKnownLeapSecond() {
        return offsets[offsets.length - 1].getDate();
    }

    /** {@inheritDoc} */
    @Override
    public boolean insideLeap(final AbsoluteDate date) {
        final int offsetIndex = findOffsetIndex(date);
        if (offsetIndex < 0) {
            // the date is before the first known leap
            return false;
        } else {
            return date.compareTo(offsets[offsetIndex].getValidityStart()) < 0;
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> boolean insideLeap(final FieldAbsoluteDate<T> date) {
        return insideLeap(date.toAbsoluteDate());
    }

    /** {@inheritDoc} */
    @Override
    public int minuteDuration(final AbsoluteDate date) {
        final int offsetIndex = findOffsetIndex(date);
        final UTCTAIOffset offset;
        if (offsetIndex >= 0 &&
                date.compareTo(offsets[offsetIndex].getValidityStart()) < 0) {
            // the date is during the leap itself
            offset = offsets[offsetIndex];
        } else if (offsetIndex + 1 < offsets.length &&
            offsets[offsetIndex + 1].getDate().durationFrom(date) <= 60.0) {
            // the date is after a leap, but it may be just before the next one
            // the next leap will start in one minute, it will extend the current minute
            offset = offsets[offsetIndex + 1];
        } else {
            offset = null;
        }
        if (offset != null) {
            // since this method returns an int we can't return the precise duration in
            // all cases, but we can bound it. Some leaps are more than 1s. See #694
            return 60 + (int) FastMath.ceil(offset.getLeap());
        }
        // no leap is expected within the next minute
        return 60;
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> int minuteDuration(final FieldAbsoluteDate<T> date) {
        return minuteDuration(date.toAbsoluteDate());
    }

    /** {@inheritDoc} */
    @Override
    public double getLeap(final AbsoluteDate date) {
        final int offsetIndex = findOffsetIndex(date);
        if (offsetIndex < 0) {
            // the date is before the first known leap
            return 0;
        } else {
            return offsets[offsetIndex].getLeap();
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> T getLeap(final FieldAbsoluteDate<T> date) {
        return date.getField().getZero().add(getLeap(date.toAbsoluteDate()));
    }

    /**
     * Returns the adjustment, if any, to TAI-UTC made at the <em>beginning</em> of
     * the specified day.
     * @param mjd modified Julian day (UTC)
     * @return adjustment (seconds)
     */
    public double getTAIMinusUTCAdjustment(final int mjd) {
        // Reverse linear search instead of binary search is fastest when most queries
        // are for near-past, present, and future times.
        double adjustment = 0.;
        for (int i = offsets.length - 1; i >= 0; --i) {
            final UTCTAIOffset offset = offsets[i];
            if (mjd == offset.getMJD()) {
                // There was an adjustment at the beginning of the specified day.
                final double newTAIMinusUTC = offset.getOffset(mjd, 0.);
                final double oldTAIMinusUTC = i > 0 ? offsets[i - 1].getOffset(mjd, 0.) : 0.;
                adjustment = newTAIMinusUTC - oldTAIMinusUTC;
                break;
            }
            if (mjd > offset.getMJD()) {
                // If in our linear search backwards we've passed the specified date,
                // we can give up.
                break;
            }
        }
        return adjustment;
    }

    /** Find the index of the offset valid at some date.
     * @param date date at which offset is requested
     * @return index of the offset valid at this date, or -1 if date is before first offset.
     */
    private int findOffsetIndex(final AbsoluteDate date) {
        int inf = 0;
        int sup = offsets.length;
        while (sup - inf > 1) {
            final int middle = (inf + sup) >>> 1;
            if (date.compareTo(offsets[middle].getDate()) < 0) {
                sup = middle;
            } else {
                inf = middle;
            }
        }
        if (sup == offsets.length) {
            // the date is after the last known leap second
            return offsets.length - 1;
        } else if (date.compareTo(offsets[inf].getDate()) < 0) {
            // the date is before the first known leap
            return -1;
        } else {
            return inf;
        }
    }

    /** Find the offset valid at some date.
     * @param mjd Modified Julian Day of the date at which offset is requested
     * @return offset valid at this date, or null if date is before first offset.
     */
    private UTCTAIOffset findOffset(final int mjd) {
        int inf = 0;
        int sup = offsets.length;
        while (sup - inf > 1) {
            final int middle = (inf + sup) >>> 1;
            if (mjd < offsets[middle].getMJD()) {
                sup = middle;
            } else {
                inf = middle;
            }
        }
        if (sup == offsets.length) {
            // the date is after the last known leap second
            return offsets[offsets.length - 1];
        } else if (mjd < offsets[inf].getMJD()) {
            // the date is before the first known leap
            return null;
        } else {
            return offsets[inf];
        }
    }

    /** Replace the instance with a data transfer object for serialization.
     * @return data transfer object that will be serialized
     */
    @DefaultDataContext
    private Object writeReplace() {
        return new DataTransferObject();
    }

    /** Internal class used only for serialization. */
    @DefaultDataContext
    private static class DataTransferObject implements Serializable {

        /** Serializable UID. */
        private static final long serialVersionUID = 20131209L;

        /** Replace the deserialized data transfer object with a {@link UTCScale}.
         * @return replacement {@link UTCScale}
         */
        private Object readResolve() {
            try {
                return DataContext.getDefault().getTimeScales().getUTC();
            } catch (OrekitException oe) {
                throw new OrekitInternalError(oe);
            }
        }

    }

    /**
     * An implementation of {@link TemporalAccessor} appropriate for a UTC time.
     */
    final class UTCTemporalAccessor implements TemporalAccessor {

        /** UTC scale. */
        private final UTCScale utcScale;
        /** Local date-time with all fields correct except for second-of-minute. */
        private final LocalDateTime localDateTime; // with second-of-minute set equal to 0
        /** Hour-of-day. */
        private final int hourOfDay;
        /** Minute-of-hour. */
        private final int minuteOfHour;
        /** Second-of-minute. */
        private final int secondOfMinute;
        /** Nano-of-second. */
        private final int nanoOfSecond;

        /**
         * Constructs a {@link UtcTemporalAccessor} instance.
         * @param utcScale UTC scale, not null
         * @param year year
         * @param monthOfYear month of year (1-based)
         * @param dayOfMonth day of month (1-based)
         * @param hourOfDay hour of day (0-based)
         * @param minuteOfHour minute of hour (0-based)
         * @param seconds seconds in minute
         */
        private UTCTemporalAccessor(final UTCScale utcScale, final int year, final int monthOfYear, final int dayOfMonth,
                final int hourOfDay, final int minuteOfHour, final double seconds) {
            this.utcScale = Objects.requireNonNull(utcScale);
            this.hourOfDay = hourOfDay;
            this.minuteOfHour = minuteOfHour;
            this.secondOfMinute = (int) seconds;
            this.nanoOfSecond = (int) (1000000000. * (seconds - this.secondOfMinute)); // truncate, not round
            this.localDateTime = LocalDateTime.of(
                    year,
                    monthOfYear,
                    dayOfMonth,
                    hourOfDay,
                    minuteOfHour,
                    0, // second-of-minute not used
                    nanoOfSecond);
        }

        @Override
        public boolean isSupported(final TemporalField field) {
            if (field instanceof ChronoField) {
                switch ((ChronoField) field) {
                    case NANO_OF_DAY:
                    case MICRO_OF_DAY:
                    case MILLI_OF_DAY:
                    case SECOND_OF_MINUTE:
                    case SECOND_OF_DAY:
                        return false;
                    case OFFSET_SECONDS:
                        return true;
                    default:
                        return localDateTime.isSupported(field);
                }
            } else if (field instanceof UTCField) {
                return field instanceof UTCSecondOfMinute ||
                        field instanceof UTCSecondOfDay ||
                        field instanceof UTCNanoOfDay ||
                        field instanceof UTCMicroOfDay ||
                        field instanceof UTCMilliOfDay;
            }
            return field.isSupportedBy(this);
        }

        @Override
        public long getLong(final TemporalField field) {
            if (field instanceof ChronoField) {
                switch ((ChronoField) field) {
                    case NANO_OF_DAY:
                    case MICRO_OF_DAY:
                    case MILLI_OF_DAY:
                    case SECOND_OF_MINUTE:
                    case SECOND_OF_DAY:
                        throw new UnsupportedTemporalTypeException(
                            String.format("Unsupported field: %s, use corresponding UTCField instead", field));
                    case OFFSET_SECONDS:
                        return 0L;
                    default:
                        return localDateTime.getLong(field);
                }
            } else if (field instanceof UTCField) {
                final UTCField utcField = (UTCField) field;
                if (!this.utcScale.equals(utcField.getUTCScale())) {
                    throw new IllegalArgumentException("UTC scale of UTCField must match UTC scale of UTCTemporalAccessor");
                } else if (utcField instanceof UTCSecondOfMinute) {
                    return secondOfMinute;
                } else if (utcField instanceof UTCSecondOfDay) {
                    return 3600L * hourOfDay + 60L * minuteOfHour + secondOfMinute;
                } else if (utcField instanceof UTCNanoOfDay) {
                    return 3600000000000L * hourOfDay + 60000000000L * minuteOfHour + 1000000000L * secondOfMinute + nanoOfSecond;
                } else if (utcField instanceof UTCMicroOfDay) {
                    final int microOfSecond = nanoOfSecond / 1000;
                    return 3600000000L * hourOfDay + 60000000L * minuteOfHour + 1000000L * secondOfMinute + microOfSecond;
                } else if (utcField instanceof UTCMilliOfDay) {
                    final int milliOfSecond = nanoOfSecond / 1000000;
                    return 3600000L * hourOfDay + 60000L * minuteOfHour + 1000L * secondOfMinute + milliOfSecond;
                }
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
            return field.getFrom(this);
        }

    }

}
