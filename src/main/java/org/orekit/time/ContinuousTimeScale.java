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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;
import org.hipparchus.util.MathUtils.SumAndResidual;

/**
 * A continuous {@link TimeScale} without leaps in which every day has exactly 86400 seconds.
 */
public abstract class ContinuousTimeScale implements TimeScale {

    /** Serializable UID. */
    private static final long serialVersionUID = -1243756924937497980L;

    /** J2000 epoch as a {@link LocalDateTime}. */
    private static final LocalDateTime J2000 = LocalDateTime.of(2000, 01, 01, 12, 00, 00);

    /** Abbreviation for the time scale, e.g. TAI, TT, UT1, etc. */
    private final String abbreviation;

    /**
     * Constructs a {@link ContinuousTimeScale} instance.
     * @param abbreviation abbreviation for the time scale, e.g. TAI, UTC, UT1, etc., not null
     */
    public ContinuousTimeScale(final String abbreviation) {
        this.abbreviation = Objects.requireNonNull(abbreviation);
    }

    /** {@inheritDoc} */
    @Override
    public AbsoluteDate temporalToDate(final TemporalAccessor temporal) {
        final LocalDateTime localDateTime = LocalDateTime.from(temporal);
        final int mjd = localDateTime.get(JulianFields.MODIFIED_JULIAN_DAY);
        final double secondsInDay = localDateTime.get(ChronoField.NANO_OF_DAY) / 1000000000.;
        final AbsoluteDate date = AbsoluteDate.createMJDDate(mjd, secondsInDay, this);
        return date;
    }

    /** {@inheritDoc} */
    @Override
    public TemporalAccessor dateToTemporal(final AbsoluteDate date) {
        final long epoch = date.getEpoch();
        final double offsetSeconds1 = date.getOffset();
        final double offsetSeconds2 = this.offsetFromTAI(date);

        // Special handling for past and future infinity.
        if (Double.isInfinite(offsetSeconds1)) {
            return offsetSeconds1 < 0 ? LocalDateTime.MIN : LocalDateTime.MAX;
        }

        // Use 2Sum for high precision.
        final SumAndResidual sumAndResidual = MathUtils.twoSum(offsetSeconds1, offsetSeconds2);

        final long longSeconds1 = epoch;
        final long longSeconds2 = FastMath.round(sumAndResidual.getSum());
        final double fractionalSeconds = (sumAndResidual.getSum() - longSeconds2) + sumAndResidual.getResidual();
        final long nanoSeconds = (long) FastMath.floor(1000000000. * fractionalSeconds); // truncate, not round

        final LocalDateTime localDateTime = J2000.plusSeconds(longSeconds1 + longSeconds2).plusNanos(nanoSeconds);

        return localDateTime;
    }

    /** {@inheritDoc} */
    @Override
    public DateTimeFormatter getDefaultFormatter() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return abbreviation;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return abbreviation;
    }

}
