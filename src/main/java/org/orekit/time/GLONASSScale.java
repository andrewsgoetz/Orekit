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

import org.hipparchus.RealFieldElement;
import org.hipparchus.util.FastMath;

/** GLONASS time scale.
 * <p>By convention, TGLONASS = UTC + 3 hours.</p>
 * <p>The time scale is defined in <a
 * href="http://www.spacecorp.ru/upload/iblock/1c4/cgs-aaixymyt%205.1%20ENG%20v%202014.02.18w.pdf">
 * Global Navigation Sattelite System GLONASS - Interface Control document</a>, version 5.1 2008
 * (the typo in the title is in the original document title).
 * </p>
 * <p>This is intended to be accessed thanks to {@link TimeScales},
 * so there is no public constructor.</p>
 * @author Luc Maisonobe
 * @see AbsoluteDate
 */
public class GLONASSScale implements TimeScale {

    /** Serializable UID. */
    private static final long serialVersionUID = 20160331L;

    /** Constant offset with respect to UTC (3 hours). */
    private static final double OFFSET = 10800;

    /** UTC time scale. */
    private final UTCScale utc;

    /** Package private constructor for the factory.
     * @param utc underlying UTC scale
     */
    GLONASSScale(final UTCScale utc) {
        this.utc = utc;
    }

    /** {@inheritDoc} */
    @Override
    public double offsetFromTAI(final AbsoluteDate date) {
        return OFFSET + utc.offsetFromTAI(date);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> T offsetFromTAI(final FieldAbsoluteDate<T> date) {
        return utc.offsetFromTAI(date).add(OFFSET);
    }

    /** {@inheritDoc} */
    @Override
    public double offsetToTAI(final DateComponents date, final TimeComponents time) {
        final DateTimeComponents utcComponents =
                        new DateTimeComponents(new DateTimeComponents(date, time), -OFFSET);
        return utc.offsetToTAI(utcComponents.getDate(), utcComponents.getTime()) - OFFSET;
    }

    /** Check if date is within a leap second introduction.
     * @param date date to check
     * @return true if time is within a leap second introduction
     */
    public boolean insideLeap(final AbsoluteDate date) {
        return utc.insideLeap(date);
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

        // extract time element, accounting for leap seconds
        final double leap = this.insideLeap(date) ? this.getLeap(date) : 0;
        final int minuteDuration = this.minuteDuration(date);
        final TimeComponents timeComponents =
                TimeComponents.fromSeconds((int) time, offset2000B, leap, minuteDuration);

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
        // extract time element, accounting for leap seconds
        final double leap =
                this.insideLeap(date.toAbsoluteDate()) ? this.getLeap(date.toAbsoluteDate()) : 0;
        final int minuteDuration = this.minuteDuration(date);
        final TimeComponents timeComponents =
                TimeComponents.fromSeconds((int) time, offset2000B, leap, minuteDuration);

        // build the components
        return new DateTimeComponents(dateComponents, timeComponents);
    }

    /** Check if date is within a leap second introduction.
     * @param <T> field element type
     * @param date date to check
     * @return true if time is within a leap second introduction
     */
    public <T extends RealFieldElement<T>> boolean insideLeap(final FieldAbsoluteDate<T> date) {
        return utc.insideLeap(date);
    }

    /** {@inheritDoc} */
    @Override
    public int minuteDuration(final AbsoluteDate date) {
        return utc.minuteDuration(date);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> int minuteDuration(final FieldAbsoluteDate<T> date) {
        return utc.minuteDuration(date);
    }

    /** Get the value of the previous leap.
     * @param date date to check
     * @return value of the previous leap
     */
    public double getLeap(final AbsoluteDate date) {
        return utc.getLeap(date);
    }

    /** Get the value of the previous leap.
     * @param <T> field element type
     * @param date date to check
     * @return value of the previous leap
     */
    public <T extends RealFieldElement<T>> T getLeap(final FieldAbsoluteDate<T> date) {
        return utc.getLeap(date);
    }

    /** {@inheritDoc} */
    public String getName() {
        return "GLONASS";
    }

    /** {@inheritDoc} */
    public String toString() {
        return getName();
    }

}
