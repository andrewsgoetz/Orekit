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
import java.util.TimeZone;

import org.hipparchus.RealFieldElement;
import org.orekit.errors.OrekitIllegalArgumentException;
import org.orekit.utils.Constants;

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
    default AbsoluteDate createMJDDate(final int mjd, final double secondsInDay)
        throws OrekitIllegalArgumentException {
        final DateComponents dc = new DateComponents(DateComponents.MODIFIED_JULIAN_EPOCH, mjd);
        final TimeComponents tc;
        if (secondsInDay >= Constants.JULIAN_DAY) {
            // check we are really allowed to use this number of seconds
            final int    secondsA = 86399; // 23:59:59, i.e. 59s in the last minute of the day
            final double secondsB = secondsInDay - secondsA;
            final TimeComponents safeTC = new TimeComponents(secondsA, 0.0);
            final AbsoluteDate safeDate = new AbsoluteDate(dc, safeTC, this);
            if (this.minuteDuration(safeDate) > 59 + secondsB) {
                // we are within the last minute of the day, the number of seconds is OK
                return safeDate.shiftedBy(secondsB);
            } else {
                // let TimeComponents trigger an OrekitIllegalArgumentException
                // for the wrong number of seconds
                tc = new TimeComponents(secondsA, secondsB);
            }
        } else {
            tc = new TimeComponents(secondsInDay);
        }

        // create the date
        return new AbsoluteDate(dc, tc, this);
    }

    /** Get a String representation of the instant location.
     * @param date date
     * @return a string representation of the instance,
     * in ISO-8601 format with milliseconds accuracy
     */
    default String dateToString(final AbsoluteDate date) {
        return date.getComponents(this).toString(this.minuteDuration(date));
    }

    /** Get a String representation of the instant location.
     * @param <T> field element type
     * @param date date
     * @return a string representation of the instance,
     * in ISO-8601 format with milliseconds accuracy
     */
    default <T extends RealFieldElement<T>> String dateToString(final FieldAbsoluteDate<T> date) {
        return date.getComponents(this).toString(this.minuteDuration(date));
    }

    /**
     * Get a String representation of the instant location for a time zone.
     * @param date     date
     * @param timeZone time zone
     * @return string representation of the instance, in ISO-8601 format with milliseconds
     * accuracy
     */
    default String dateToString(final AbsoluteDate date, final TimeZone timeZone) {
        final int minuteDuration = this.minuteDuration(date);
        return date.getComponents(timeZone, this).toString(minuteDuration);
    }

    /**
     * Get a String representation of the instant location for a time zone.
     * @param <T> field element type
     * @param date     date
     * @param timeZone time zone
     * @return string representation of the instance, in ISO-8601 format with milliseconds
     * accuracy
     */
    default <T extends RealFieldElement<T>> String dateToString(final FieldAbsoluteDate<T> date, final TimeZone timeZone) {
        final int minuteDuration = this.minuteDuration(date);
        return date.getComponents(timeZone, this).toString(minuteDuration);
    }

    /**
     * Get a String representation of the instant location for a local time.
     * @param date           date
     * @param minutesFromUTC offset in <em>minutes</em> from UTC (positive Eastwards UTC,
     *                       negative Westward UTC).
     * @return string representation of the instance, in ISO-8601 format with milliseconds
     * accuracy
     */
    default String dateToString(final AbsoluteDate date, final int minutesFromUTC) {
        final int minuteDuration = this.minuteDuration(date);
        return date.getComponents(minutesFromUTC, this).toString(minuteDuration);
    }

    /**
     * Get a String representation of the instant location for a local time.
     * @param <T> field element type
     * @param date           date
     * @param minutesFromUTC offset in <em>minutes</em> from UTC (positive Eastwards UTC,
     *                       negative Westward UTC).
     * @return string representation of the instance, in ISO-8601 format with milliseconds
     * accuracy
     */
    default <T extends RealFieldElement<T>>String dateToString(final FieldAbsoluteDate<T> date, final int minutesFromUTC) {
        final int minuteDuration = this.minuteDuration(date);
        return date.getComponents(minutesFromUTC, this).toString(minuteDuration);
    }

    /** Get the name time scale.
     * @return name of the time scale
     */
    String getName();

}
