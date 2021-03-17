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
import java.util.TimeZone;

import org.hipparchus.RealFieldElement;
import org.orekit.errors.OrekitIllegalArgumentException;

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
     * @param date date, not null
     * @return temporal accessor, not null
     */
    TemporalAccessor dateToTemporal(AbsoluteDate date);

    /**
     * Returns the default formatter for formatting dates in the time scale.
     * @return default formatter, not null
     */
    DateTimeFormatter getDefaultFormatter();

    /** Split a date into date/time components.
     * @param date date
     * @return date/time components
     */
    DateTimeComponents getComponents(AbsoluteDate date);

    /** Split a date into date/time components.
     * @param <T> field element type
     * @param date date
     * @return date/time components
     */
    <T extends RealFieldElement<T>> DateTimeComponents getComponents(FieldAbsoluteDate<T> date);

    /** Build an {@link AbsoluteDate} instance corresponding to a Modified Julian Day date.
     * @param mjd modified Julian day
     * @param secondsInDay seconds in the day
     * @return a new instant
     * @exception OrekitIllegalArgumentException if seconds number is out of range
     */
    AbsoluteDate createMJDDate(int mjd, double secondsInDay);

    /** Get a String representation of the instant location.
     * @param date date
     * @return a string representation of the instance,
     * in ISO-8601 format with milliseconds accuracy
     */
    String dateToString(AbsoluteDate date);

    /** Get a String representation of the instant location.
     * @param <T> field element type
     * @param date date
     * @return a string representation of the instance,
     * in ISO-8601 format with milliseconds accuracy
     */
    <T extends RealFieldElement<T>> String dateToString(FieldAbsoluteDate<T> date);

    /**
     * Get a String representation of the instant location for a time zone.
     * @param date     date
     * @param timeZone time zone
     * @return string representation of the instance, in ISO-8601 format with milliseconds
     * accuracy
     */
    String dateToString(AbsoluteDate date, TimeZone timeZone);

    /**
     * Get a String representation of the instant location for a time zone.
     * @param <T> field element type
     * @param date     date
     * @param timeZone time zone
     * @return string representation of the instance, in ISO-8601 format with milliseconds
     * accuracy
     */
    <T extends RealFieldElement<T>> String dateToString(FieldAbsoluteDate<T> date, TimeZone timeZone);

    /**
     * Get a String representation of the instant location for a local time.
     * @param date           date
     * @param minutesFromUTC offset in <em>minutes</em> from UTC (positive Eastwards UTC,
     *                       negative Westward UTC).
     * @return string representation of the instance, in ISO-8601 format with milliseconds
     * accuracy
     */
    String dateToString(AbsoluteDate date, int minutesFromUTC);

    /**
     * Get a String representation of the instant location for a local time.
     * @param <T> field element type
     * @param date           date
     * @param minutesFromUTC offset in <em>minutes</em> from UTC (positive Eastwards UTC,
     *                       negative Westward UTC).
     * @return string representation of the instance, in ISO-8601 format with milliseconds
     * accuracy
     */
    <T extends RealFieldElement<T>>String dateToString(FieldAbsoluteDate<T> date, int minutesFromUTC);

    /** Get the name time scale.
     * @return name of the time scale
     */
    String getName();

}
