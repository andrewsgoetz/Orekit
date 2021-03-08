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

import java.util.Objects;
import java.util.TimeZone;

import org.hipparchus.RealFieldElement;
import org.hipparchus.util.FastMath;
import org.orekit.errors.OrekitIllegalArgumentException;
import org.orekit.utils.Constants;

/**
 * A continuous {@link TimeScale} without leaps in which every day has 86400
 * seconds.
 */
public abstract class ContinuousTimeScale implements TimeScale {

    /** Serializable UID. */
    private static final long serialVersionUID = -1243756924937497980L;

    /** Abbreviation for the time scale, e.g. TAI, UTC, UT1, etc. */
    private final String abbreviation;

    /**
     * Constructs a {@link ContinuousTimeScale} instance.
     * @param abbreviation abbrevation for the time scale, e.g. TAI, UTC, UT1, etc.,
     *                     not null
     */
    public ContinuousTimeScale(final String abbreviation) {
        this.abbreviation = Objects.requireNonNull(abbreviation);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return abbreviation;
    }

    /** {@inheritDoc} */
    @Override
    public AbsoluteDate createMJDDate(final int mjd, final double secondsInDay)
        throws OrekitIllegalArgumentException {
        final DateComponents dc = new DateComponents(DateComponents.MODIFIED_JULIAN_EPOCH, mjd);
        final TimeComponents tc;
        // TODO Simplify this because there are no leap seconds in this time scale.
        // TODO Left here because these mods are in the middle of a pure refactor.
        if (secondsInDay >= Constants.JULIAN_DAY) {
            // check we are really allowed to use this number of seconds
            final int    secondsA = 86399; // 23:59:59, i.e. 59s in the last minute of the day
            final double secondsB = secondsInDay - secondsA;
            final TimeComponents safeTC = new TimeComponents(secondsA, 0.0);
            final AbsoluteDate safeDate = new AbsoluteDate(dc, safeTC, this);
            if (60 > 59 + secondsB) {
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

    /** {@inheritDoc} */
    @Override
    public DateTimeComponents getComponents(final AbsoluteDate date) {

        final long epoch = date.getEpoch();
        final double offset = date.getOffset();

        if (Double.isInfinite(offset)) {
            // special handling for past and future infinity
            if (offset < 0) {
                return new DateTimeComponents(DateComponents.MIN_EPOCH, TimeComponents.H00);
            } else {
                return new DateTimeComponents(DateComponents.MAX_EPOCH,
                                              new TimeComponents(23, 59, 59.999));
            }
        }

        // compute offset from 2000-01-01T00:00:00 in specified time scale exactly,
        // using Møller-Knuth TwoSum algorithm without branching
        // the following statements must NOT be simplified, they rely on floating point
        // arithmetic properties (rounding and representable numbers)
        // at the end, the EXACT result of addition offset + timeScale.offsetFromTAI(this)
        // is sum + residual, where sum is the closest representable number to the exact
        // result and residual is the missing part that does not fit in the first number
        final double taiOffset = this.offsetFromTAI(date);
        final double sum       = offset + taiOffset;
        final double oPrime    = sum - taiOffset;
        final double dPrime    = sum - oPrime;
        final double deltaO    = offset - oPrime;
        final double deltaD    = taiOffset - dPrime;
        final double residual  = deltaO + deltaD;

        // split date and time
        final long   carry = (long) FastMath.floor(sum);
        double offset2000B = (sum - carry) + residual;
        long   offset2000A = epoch + carry + 43200l;
        if (offset2000B < 0) {
            offset2000A -= 1;
            offset2000B += 1;
        }
        long time = offset2000A % 86400l;
        if (time < 0l) {
            time += 86400l;
        }
        final int dateOffset = (int) ((offset2000A - time) / 86400l);

        // extract calendar elements
        final DateComponents dateComponents = new DateComponents(DateComponents.J2000_EPOCH, dateOffset);

        // extract time element
        final TimeComponents timeComponents =
                TimeComponents.fromSeconds((int) time, offset2000B, 0, 60);

        // build the components
        return new DateTimeComponents(dateComponents, timeComponents);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> DateTimeComponents getComponents(final FieldAbsoluteDate<T> date) {

        final long epoch = date.getEpoch();
        final T offset = date.getOffset();

        if (Double.isInfinite(offset.getReal())) {
            // special handling for past and future infinity
            if (offset.getReal() < 0) {
                return new DateTimeComponents(DateComponents.MIN_EPOCH, TimeComponents.H00);
            } else {
                return new DateTimeComponents(DateComponents.MAX_EPOCH,
                                              new TimeComponents(23, 59, 59.999));
            }
        }

        // compute offset from 2000-01-01T00:00:00 in specified time scale exactly,
        // using Møller-Knuth TwoSum algorithm without branching
        // the following statements must NOT be simplified, they rely on floating point
        // arithmetic properties (rounding and representable numbers)
        // at the end, the EXACT result of addition offset + timeScale.offsetFromTAI(this)
        // is sum + residual, where sum is the closest representable number to the exact
        // result and residual is the missing part that does not fit in the first number
        final double taiOffset = this.offsetFromTAI(date).getReal();
        final double sum       = offset.getReal() + taiOffset;
        final double oPrime    = sum - taiOffset;
        final double dPrime    = sum - oPrime;
        final double deltaO    = offset.getReal() - oPrime;
        final double deltaD    = taiOffset - dPrime;
        final double residual  = deltaO + deltaD;

        // split date and time
        final long   carry = (long) FastMath.floor(sum);
        double offset2000B = (sum - carry) + residual;
        long   offset2000A = epoch + carry + 43200l;
        if (offset2000B < 0) {
            offset2000A -= 1;
            offset2000B += 1;
        }
        long time = offset2000A % 86400l;
        if (time < 0l) {
            time += 86400l;
        }
        final int dateOffset = (int) ((offset2000A - time) / 86400l);

        // extract calendar elements
        final DateComponents dateComponents = new DateComponents(DateComponents.J2000_EPOCH, dateOffset);

        // extract time element
        final TimeComponents timeComponents =
                TimeComponents.fromSeconds((int) time, offset2000B, 0, 60);

        // build the components
        return new DateTimeComponents(dateComponents, timeComponents);
    }

    /** {@inheritDoc} */
    @Override
    public String dateToString(final AbsoluteDate date) {
        return date.getComponents(this).toString(60);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> String dateToString(final FieldAbsoluteDate<T> date) {
        return date.getComponents(this).toString(60);
    }

    /** {@inheritDoc} */
    @Override
    public String dateToString(final AbsoluteDate date, final TimeZone timeZone) {
        return date.getComponents(timeZone, this).toString(60);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> String dateToString(final FieldAbsoluteDate<T> date, final TimeZone timeZone) {
        return date.getComponents(timeZone, this).toString(60);
    }

    /** {@inheritDoc} */
    @Override
    public String dateToString(final AbsoluteDate date, final int minutesFromUTC) {
        return date.getComponents(minutesFromUTC, this).toString(60);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>>String dateToString(final FieldAbsoluteDate<T> date, final int minutesFromUTC) {
        return date.getComponents(minutesFromUTC, this).toString(60);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return abbreviation;
    }

}
