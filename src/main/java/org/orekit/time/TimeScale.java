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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.hipparchus.RealFieldElement;

/** Interface for time scales.
 * <p>This is the interface representing all time scales. Time scales are related
 * to each other by some offsets that may be discontinuous (for example
 * the {@link UTCScale UTC scale} with respect to the {@link TAIScale
 * TAI scale}).</p>
 * @author Luc Maisonobe
 * @see AbsoluteDate
 */
public interface TimeScale extends Serializable {

    /** Get the offset to convert locations from {@link TAIScale} to instance.
     * @param date conversion date
     * @return offset in seconds to add to a location in <em>{@link TAIScale}
     * time scale</em> to get a location in <em>instance time scale</em>
     * @see #offsetToTAI(DateComponents, TimeComponents)
     */
    double offsetFromTAI(AbsoluteDate date);

    /** Get the offset to convert locations from {@link TAIScale} to instance.
     * @param date conversion date
     * @param <T> type of the filed elements
     * @return offset in seconds to add to a location in <em>{@link TAIScale}
     * time scale</em> to get a location in <em>instance time scale</em>
     * @see #offsetToTAI(DateComponents, TimeComponents)
     * @since 9.0
     */
    <T extends RealFieldElement<T>> T offsetFromTAI(FieldAbsoluteDate<T> date);

    /** Get the offset to convert locations from instance to {@link TAIScale}.
     * @param date date location in the time scale
     * @param time time location in the time scale
     * @return offset in seconds to add to a location in <em>instance time scale</em>
     * to get a location in <em>{@link TAIScale} time scale</em>
     * @see #offsetFromTAI(AbsoluteDate)
     */
    default double offsetToTAI(final DateComponents date, final TimeComponents time) {
        final AbsoluteDate reference = new AbsoluteDate(date, time, new TAIScale());
        double offset = 0;
        for (int i = 0; i < 8; i++) {
            offset = -offsetFromTAI(reference.shiftedBy(offset));
        }
        return offset;
    }

    /**
     * Returns an {@link AbsoluteDate} obtained from the specified
     * {@link TemporalAccessor}.
     * @param temporal temporal accessor, not null
     * @return date, not null
     */
    AbsoluteDate temporalToDate(TemporalAccessor temporal);

    /**
     * Returns a {@link TemporalAccessor} for the specified date.
     * <p>
     * {@link TemporalAccessor}s provide access to integer-based {@link TemporalField}s. The value of
     * each field will be a <em>truncated</em> value rather than a rounded one. For example, the date
     * {@code 2014-08-22T13:46:58.826545910772} will have the following fields:
     * <ul>
     * <li>Year: 2014 (truncated to 2014, not rounded to 2015)</li>
     * <li>Month: August/08 (truncated to August/08, not rounded to September/09)</li>
     * <li>Day: 22 (truncated to 22, not rounded to 23)</li>
     * <li>Hour: 13 (truncated to 13, not rounded to 14)</li>
     * <li>Minute: 46 (truncated to 46, not rounded to 47)</li>
     * <li>Second: 58 (truncated to 58, not rounded to 59)</li>
     * <li>Milli-of-second: 826 (truncated to 826, not rounded to 827)</li>
     * <li>Micro-of-second: 826545 (truncated to 826545, not rounded to 826546)</li>
     * <li>Nano-of-second: 626545910 (truncated to 626545910, not rounded to 626545911)</li>
     * </ul>
     * (This list is not intended to be exhaustive.) For the fields such as year, month, and day which
     * are coarser subdivisions of time, this is usually the obvious desired behavior. For the fields
     * such as milli-of-second, micro-of-second, nano-of-second, it is less obvious what the desired
     * behavior should be, especially for the most precise field used in formatting the date into a
     * string. For consistency's sake, all values are truncated, not rounded. Therefore, when reading
     * the date above when formatted using a {@link TemporalAccessor} out to the nanosecond,
     * {@code 2014-08-22T13:46:58.826545910}, the proper interpretation is that the date falls between
     * {@code 2014-08-22T13:46:58.826545910} (inclusive) and {@code 2014-08-22T13:46:58.826545911}
     * (exclusive), <em>not</em> that {@code 2014-08-22T13:46:58.826545910} is the closest date with
     * nanosecond precision to the original date.
     * @param date date, not null
     * @return temporal accessor, not null
     */
    TemporalAccessor dateToTemporal(AbsoluteDate date);

    /**
     * Returns the default formatter for formatting dates in the time scale.
     * @return default formatter, not null
     */
    DateTimeFormatter getDefaultFormatter();

    /** Check if date is within a leap second introduction <em>in this time scale</em>.
     * <p>
     * This method will return false for all time scales that do <em>not</em>
     * implement leap seconds, even if the date corresponds to a leap second
     * in {@link UTCScale UTC scale}.
     * </p>
     * @param date date to check
     * @return true if time is within a leap second introduction
     */
    default boolean insideLeap(final AbsoluteDate date) {
        return false;
    }

    /** Check if date is within a leap second introduction <em>in this time scale</em>.
     * <p>
     * This method will return false for all time scales that do <em>not</em>
     * implement leap seconds, even if the date corresponds to a leap second
     * in {@link UTCScale UTC scale}.
     * </p>
     * @param date date to check
     * @param <T> type of the filed elements
     * @return true if time is within a leap second introduction
     * @since 9.0
     */
    default <T extends RealFieldElement<T>> boolean insideLeap(final FieldAbsoluteDate<T> date) {
        return false;
    }

    /** Check length of the current minute <em>in this time scale</em>.
     * <p>
     * This method will return 60 for all time scales that do <em>not</em>
     * implement leap seconds, even if the date corresponds to a leap second
     * in {@link UTCScale UTC scale}, and 61 for time scales that do implement
     * leap second when the current date is within the last minute before the
     * leap, or during the leap itself.
     * </p>
     * @param date date to check
     * @return 60 or 61 depending on leap seconds introduction
     */
    default int minuteDuration(final AbsoluteDate date) {
        return 60;
    }

    /** Check length of the current minute <em>in this time scale</em>.
     * <p>
     * This method will return 60 for all time scales that do <em>not</em>
     * implement leap seconds, even if the date corresponds to a leap second
     * in {@link UTCScale UTC scale}, and 61 for time scales that do implement
     * leap second when the current date is within the last minute before the
     * leap, or during the leap itself.
     * </p>
     * @param date date to check
     * @param <T> type of the filed elements
     * @return 60 or 61 depending on leap seconds introduction
     * @since 9.0
     */
    default <T extends RealFieldElement<T>> int minuteDuration(final FieldAbsoluteDate<T> date) {
        return 60;
    }

    /** Get the value of the previous leap.
     * <p>
     * This method will return 0.0 for all time scales that do <em>not</em>
     * implement leap seconds.
     * </p>
     * @param date date to check
     * @return value of the previous leap
     */
    default double getLeap(final AbsoluteDate date) {
        return 0;
    }

    /** Get the value of the previous leap.
     * <p>
     * This method will return 0.0 for all time scales that do <em>not</em>
     * implement leap seconds.
     * </p>
     * @param date date to check
     * @param <T> type of the filed elements
     * @return value of the previous leap
     * @since 9.0
     */
    default <T extends RealFieldElement<T>> T getLeap(final FieldAbsoluteDate<T> date) {
        return date.getField().getZero();
    }

    /** Get the name time scale.
     * @return name of the time scale
     */
    String getName();

}
