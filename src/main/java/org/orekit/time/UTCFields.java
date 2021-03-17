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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.JulianFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

import org.hipparchus.util.FastMath;

/**
 * {@link TemporalField}s appropriate for a UTC time scale.
 */
public final class UTCFields {

    private UTCFields() { }

    /**
     * A {@link TemporalField} for a UTC time scale.
     */
    public interface UTCField extends TemporalField {

        /**
         * Returns the UTC time scale on which this field is based.
         * @return UTC scale, not null
         */
        UTCScale getUTCScale();

    }

    /**
     * An abstract {@link TemporalField} representing a UTC field.
     */
    public abstract static class AbstractUTCField implements UTCField {

        /**
         * Range of values which in each concrete field is refined in the
         * {@link #rangeRefinedBy(TemporalAccessor)} method. We do not restrict the
         * maximum value here.
         */
        private static final ValueRange RANGE = ValueRange.of(0L, 0L, Long.MAX_VALUE);

        /** UTC scale on which the field is based.*/
        private final UTCScale utcScale;

        /** Base unit. */
        private final ChronoUnit baseUnit;

        /** Range unit. */
        private final ChronoUnit rangeUnit;

        /**
         * Constructs an {@link AbstractUtcField} instance.
         * @param utcScale  UTC scale, not null
         * @param baseUnit  base unit, not null
         * @param rangeUnit range unit, not null
         */
        public AbstractUTCField(final UTCScale utcScale, final ChronoUnit baseUnit, final ChronoUnit rangeUnit) {
            this.utcScale = Objects.requireNonNull(utcScale);
            this.baseUnit = Objects.requireNonNull(baseUnit);
            this.rangeUnit = Objects.requireNonNull(rangeUnit);
        }

        @Override
        public final UTCScale getUTCScale() {
            return utcScale;
        }

        @Override
        public final TemporalUnit getBaseUnit() {
            return baseUnit;
        }

        @Override
        public final TemporalUnit getRangeUnit() {
            return rangeUnit;
        }

        @Override
        public final ValueRange range() {
            return RANGE;
        }

        @Override
        public final boolean isDateBased() {
            return false;
        }

        @Override
        public final boolean isTimeBased() {
            return true;
        }

        @Override
        public final boolean isSupportedBy(final TemporalAccessor temporal) {
            return temporal instanceof UTCTemporalAccessor;
        }

        @Override
        public final long getFrom(final TemporalAccessor temporal) {
            if (temporal instanceof UTCTemporalAccessor) {
                return temporal.getLong(this);
            }
            throw new UnsupportedTemporalTypeException("Unsupported type: " + temporal);
        }

        @Override
        public final <R extends Temporal> R adjustInto(final R temporal, final long newValue) {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the adjustment to Delta(AT) = TAI-UTC made at the <em>end</em> of the
         * day containing the specified time.
         * @param temporal temporal, not null
         * @return adjustment (seconds)
         */
        public final double getAdjustment(final TemporalAccessor temporal) {
            final LocalDate localDate = LocalDate.from(temporal);
            final int mjd = (int) localDate.getLong(JulianFields.MODIFIED_JULIAN_DAY);
            return utcScale.getTAIMinusUTCAdjustment(mjd + 1);
        }

    }

    /**
     * A {@link TemporalField} representing a UTC microsecond-of-day.
     */
    public static final class UTCMicroOfDay extends AbstractUTCField {

        /**
         * Constructs a {@link UTCMicroOfDay} instance.
         * @param utcScale UTC scale, not null
         */
        public UTCMicroOfDay(final UTCScale utcScale) {
            super(utcScale, ChronoUnit.MILLIS, ChronoUnit.DAYS);
        }

        @Override
        public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
            final double adjustment = getAdjustment(temporal);
            final long max = 86399999999L + (long) FastMath.ceil(1000000. * adjustment);
            return ValueRange.of(0L, max);
        }

    }

    /**
     * A {@link TemporalField} representing a UTC millisecond-of-day.
     */
    public static final class UTCMilliOfDay extends AbstractUTCField {

        /**
         * Constructs a {@link UTCMilliOfDay} instance.
         * @param utcScale UTC scale, not null
         */
        public UTCMilliOfDay(final UTCScale utcScale) {
            super(utcScale, ChronoUnit.MILLIS, ChronoUnit.DAYS);
        }

        @Override
        public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
            final double adjustment = getAdjustment(temporal);
            final long max = 86399999L + (long) FastMath.ceil(1000. * adjustment);
            return ValueRange.of(0L, max);
        }

    }

    /**
     * A {@link TemporalField} representing a UTC nanosecond-of-day.
     */
    public static final class UTCNanoOfDay extends AbstractUTCField {

        /**
         * Constructs a {@link UTCNanoOfDay} instance.
         * @param utcScale UTC scale, not null
         */
        public UTCNanoOfDay(final UTCScale utcScale) {
            super(utcScale, ChronoUnit.MILLIS, ChronoUnit.DAYS);
        }

        @Override
        public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
            final double adjustment = getAdjustment(temporal);
            final long max = 86399999999999L + (long) FastMath.ceil(1000000000. * adjustment);
            return ValueRange.of(0L, max);
        }

    }

    /**
     * A {@link TemporalField} representing a UTC second-of-day.
     */
    public static final class UTCSecondOfDay extends AbstractUTCField {

        /**
         * Constructs a {@link UTCSecondOfDay} instance.
         * @param utcScale UTC scale, not null
         */
        public UTCSecondOfDay(final UTCScale utcScale) {
            super(utcScale, ChronoUnit.SECONDS, ChronoUnit.DAYS);
        }

        @Override
        public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
            final double adjustment = getAdjustment(temporal);
            final long max = 86399L + (long) Math.ceil(adjustment);
            return ValueRange.of(0L, max);
        }

    }

    /**
     * A {@link TemporalField} representing a UTC second-of-minute.
     */
    public static final class UTCSecondOfMinute extends AbstractUTCField {

        /**
         * Constructs a {@link UtcSecondOfMinute} instance.
         * @param utcScale UTC scale, not null
         */
        public UTCSecondOfMinute(final UTCScale utcScale) {
            super(utcScale, ChronoUnit.SECONDS, ChronoUnit.MINUTES);
        }

        @Override
        public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
            final double adjustment = getAdjustment(temporal);
            final long max = 59L + (long) Math.ceil(adjustment);
            return ValueRange.of(0L, max);
        }

    }

}
