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

/**
 * A continuous {@link TimeScale} without leaps in which every day has exactly 86400 seconds.
 */
public abstract class ContinuousTimeScale implements TimeScale {

    /** Serializable UID. */
    private static final long serialVersionUID = -1243756924937497980L;

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
    public String getName() {
        return abbreviation;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return abbreviation;
    }

}
