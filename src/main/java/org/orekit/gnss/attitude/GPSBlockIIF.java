/* Copyright 2002-2018 CS Systèmes d'Information
 * Licensed to CS Systèmes d'Information (CS) under one or more
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
package org.orekit.gnss.attitude;

import org.hipparchus.Field;
import org.hipparchus.RealFieldElement;
import org.hipparchus.util.FastMath;
import org.orekit.frames.Frame;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.ExtendedPVCoordinatesProvider;
import org.orekit.utils.TimeStampedAngularCoordinates;
import org.orekit.utils.TimeStampedFieldAngularCoordinates;

/**
 * Attitude providers for GPS block IIF navigation satellites.
 * <p>
 * This class is based on the May 2017 version of J. Kouba eclips.f
 * subroutine available at <a href="http://acc.igs.org/orbits">IGS Analysis
 * Center Coordinator site</a>. The eclips.f code itself is not used ; its
 * hard-coded data are used and its low level models are used, but the
 * structure of the code and the API have been completely rewritten.
 * </p>
 * <p>
 * WARNING: as of release 9.2, this feature is still considered experimental
 * </p>
 * @author J. Kouba original fortran routine
 * @author Luc Maisonobe Java translation
 * @since 9.2
 */
public class GPSBlockIIF extends AbstractGNSSAttitudeProvider {

    /** Serializable UID. */
    private static final long serialVersionUID = 20171114L;

    /** Satellite-Sun angle limit for a midnight turn maneuver. */
    private static final double NIGHT_TURN_LIMIT = FastMath.toRadians(180.0 - 13.25);

    /** Bias. */
    private static final double YAW_BIAS = FastMath.toRadians(-0.7);

    /** Yaw rates for all spacecrafts. */
    private static final double YAW_RATE = FastMath.toRadians(0.11);

    /** Margin on turn end. */
    private final double END_MARGIN = 1800.0;

    /** Simple constructor.
     * @param validityStart start of validity for this provider
     * @param validityEnd end of validity for this provider
     * @param sun provider for Sun position
     * @param inertialFrame inertial frame where velocity are computed
     */
    public GPSBlockIIF(final AbsoluteDate validityStart, final AbsoluteDate validityEnd,
                       final ExtendedPVCoordinatesProvider sun, final Frame inertialFrame) {
        super(validityStart, validityEnd, sun, inertialFrame);
    }

    /** {@inheritDoc} */
    @Override
    protected TimeStampedAngularCoordinates correctedYaw(final GNSSAttitudeContext context) {

        // noon beta angle limit from yaw rate
        final double aNoon  = FastMath.atan(context.getMuRate() / YAW_RATE);
        final double aNight = NIGHT_TURN_LIMIT;
        final double cNoon  = FastMath.cos(aNoon);
        final double cNight = FastMath.cos(aNight);

        if (context.setUpTurnRegion(cNight, cNoon)) {

            final double absBeta = FastMath.abs(context.getBeta());
            context.setHalfSpan(context.inSunSide() ?
                                absBeta * FastMath.sqrt(aNoon / absBeta - 1.0) :
                                context.inOrbitPlaneAbsoluteAngle(aNight - FastMath.PI));
            if (context.inTurnTimeRange(context.getDate(), END_MARGIN)) {

                // we need to ensure beta sign does not change during the turn
                final double beta     = context.getSecuredBeta();
                final double phiStart = context.getYawStart(beta);
                final double dtStart  = context.timeSinceTurnStart(context.getDate());
                final double phiDot;
                final double linearPhi;
                if (context.inSunSide()) {
                    // noon turn
                    if (beta > YAW_BIAS && beta < 0) {
                        // noon turn problem for small negative beta in block IIF
                        // rotation is in the wrong direction for these spacecrafts
                        phiDot    = FastMath.copySign(YAW_RATE, beta);
                        linearPhi = phiStart + phiDot * dtStart;
                    } else {
                        // regular noon turn
                        phiDot    = -FastMath.copySign(YAW_RATE, beta);
                        linearPhi = phiStart + phiDot * dtStart;
                    }
                } else {
                    // midnight turn
                    phiDot    = context.yawRate(beta);
                    linearPhi = phiStart + phiDot * dtStart;
                }

                return context.turnCorrectedAttitude(linearPhi, phiDot);

            }

        }

        // in nominal yaw mode
        return context.getNominalYaw();

    }

    /** {@inheritDoc} */
    @Override
    protected <T extends RealFieldElement<T>> TimeStampedFieldAngularCoordinates<T> correctedYaw(final GNSSFieldAttitudeContext<T> context) {

        final Field<T> field = context.getDate().getField();

        // noon beta angle limit from yaw rate
        final T      aNoon  = FastMath.atan(context.getMuRate().divide(YAW_RATE));
        final T      aNight = field.getZero().add(NIGHT_TURN_LIMIT);
        final double cNoon  = FastMath.cos(aNoon.getReal());
        final double cNight = FastMath.cos(aNight.getReal());

        if (context.setUpTurnRegion(cNight, cNoon)) {

            final T absBeta = FastMath.abs(context.getBeta());
            context.setHalfSpan(context.inSunSide() ?
                                absBeta.multiply(FastMath.sqrt(aNoon.divide(absBeta).subtract(1.0))) :
                                context.inOrbitPlaneAbsoluteAngle(aNight.subtract(FastMath.PI)));
            if (context.inTurnTimeRange(context.getDate(), END_MARGIN)) {

                // we need to ensure beta sign does not change during the turn
                final T beta     = context.getSecuredBeta();
                final T phiStart = context.getYawStart(beta);
                final T dtStart  = context.timeSinceTurnStart(context.getDate());
                final T phiDot;
                final T linearPhi;
                if (context.inSunSide()) {
                    // noon turn
                    if (beta.getReal() > YAW_BIAS && beta.getReal() < 0) {
                        // noon turn problem for small negative beta in block IIF
                        // rotation is in the wrong direction for these spacecrafts
                        phiDot    = field.getZero().add(FastMath.copySign(YAW_RATE, beta.getReal()));
                        linearPhi = phiStart.add(phiDot.multiply(dtStart));
                    } else {
                        // regular noon turn
                        phiDot    = field.getZero().add(-FastMath.copySign(YAW_RATE, beta.getReal()));
                        linearPhi = phiStart.add(phiDot.multiply(dtStart));
                    }
                } else {
                    // midnight turn
                    phiDot    = context.yawRate(beta);
                    linearPhi = phiStart.add(phiDot.multiply(dtStart));
                }

                return context.turnCorrectedAttitude(linearPhi, phiDot);

            }

        }

        // in nominal yaw mode
        return context.getNominalYaw();

    }

}
