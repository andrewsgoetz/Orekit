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

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;

import org.orekit.time.UTCFields.UTCField;
import org.orekit.time.UTCFields.UTCMicroOfDay;
import org.orekit.time.UTCFields.UTCMilliOfDay;
import org.orekit.time.UTCFields.UTCNanoOfDay;
import org.orekit.time.UTCFields.UTCSecondOfDay;
import org.orekit.time.UTCFields.UTCSecondOfMinute;

/**
 * An implementation of {@link TemporalAccessor} appropriate for a UTC time.
 */
public final class UTCTemporalAccessor implements TemporalAccessor {

    /** UTC scale. */
    private final UTCScale utcScale;
    /** Local date-time with all fields correct except for second-of-minute. */
    private final LocalDateTime localDateTime; // with second-of-minute set equal to 0
    /** Year. */
    private final int year;
    /** Month-of-year. */
    private final int monthOfYear;
    /** Day-of-month. */
    private final int dayOfMonth;
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
     * @param date     date, not null
     * @param utcScale UTC scale, not null
     */
    public UTCTemporalAccessor(final AbsoluteDate date, final UTCScale utcScale) {
        this.utcScale = Objects.requireNonNull(utcScale);
        final DateTimeComponents dateTimeComponents = utcScale.getComponents(date);
        this.year = dateTimeComponents.getDate().getYear();
        this.monthOfYear = dateTimeComponents.getDate().getMonth();
        this.dayOfMonth = dateTimeComponents.getDate().getDay();
        this.hourOfDay = dateTimeComponents.getTime().getHour();
        this.minuteOfHour = dateTimeComponents.getTime().getMinute();
        final double second = dateTimeComponents.getTime().getSecond();
        this.secondOfMinute = (int) second;
        this.nanoOfSecond = (int) (1000000000. * (second - this.secondOfMinute));
        this.localDateTime = LocalDateTime.of(year, //
                monthOfYear, //
                dayOfMonth, //
                hourOfDay, //
                minuteOfHour, //
                0, // second-of-minute not used
                nanoOfSecond);
    }

    /**
     * Returns the UTC scale for this {@link UTCTemporalAccessor}.
     * @return UTC scale, not null
     */
    public UTCScale getUTCScale() {
        return utcScale;
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
            if (!this.getUTCScale().equals(utcField.getUTCScale())) {
                throw new IllegalArgumentException("UTC scale of UTCField must match UTC scale of UTCTemporalAccessor");
            }
            if (utcField instanceof UTCSecondOfMinute) {
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
