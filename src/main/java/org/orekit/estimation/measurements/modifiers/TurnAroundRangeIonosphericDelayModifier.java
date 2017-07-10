/* Copyright 2002-2017 CS Systèmes d'Information
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
package org.orekit.estimation.measurements.modifiers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitExceptionWrapper;
import org.orekit.estimation.measurements.EstimatedMeasurement;
import org.orekit.estimation.measurements.EstimationModifier;
import org.orekit.estimation.measurements.GroundStation;
import org.orekit.estimation.measurements.TurnAroundRange;
import org.orekit.models.earth.IonosphericModel;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.utils.Differentiation;
import org.orekit.utils.ParameterDriver;
import org.orekit.utils.ParameterFunction;
import org.orekit.utils.StateFunction;

/** Class modifying theoretical TurnAroundRange measurement with ionospheric delay.
 * The effect of ionospheric correction on the TurnAroundRange is directly computed
 * through the computation of the ionospheric delay.
 *
 * The ionospheric delay depends on the frequency of the signal (GNSS, VLBI, ...).
 * For optical measurements (e.g. SLR), the ray is not affected by ionosphere charged particles.
 *
 * @author Maxime Journot
 * @since 9.0
 */
public class TurnAroundRangeIonosphericDelayModifier implements EstimationModifier<TurnAroundRange> {

    /** Ionospheric delay model. */
    private final IonosphericModel ionoModel;

    /** Constructor.
     *
     * @param model  Ionospheric delay model appropriate for the current TurnAroundRange measurement method.
     */
    public TurnAroundRangeIonosphericDelayModifier(final IonosphericModel model) {
        ionoModel = model;
    }

    /** Compute the measurement error due to ionosphere.
     * @param station station
     * @param state spacecraft state
     * @return the measurement error due to ionosphere
     * @throws OrekitException  if frames transformations cannot be computed
     */
    private double rangeErrorIonosphericModel(final GroundStation station,
                                              final SpacecraftState state)
        throws OrekitException {

        // State position
        final Vector3D position = state.getPVCoordinates().getPosition();

        // Elevation of the satellite seen from the station
        final double elevation = station.getBaseFrame().getElevation(position,
                                                                     state.getFrame(),
                                                                     state.getDate());

        // Only consider measures above the horizon
        if (elevation > 0) {

            // Compute azimuth
            final double azimuth = station.getBaseFrame().getAzimuth(position,
                                                                     state.getFrame(),
                                                                     state.getDate());

            // Delay in meters
            final double delay = ionoModel.pathDelay(state.getDate(),
                                                     station.getBaseFrame().getPoint(),
                                                     elevation, azimuth);
            return delay;
        }

        return 0;
    }

    /** Compute the Jacobian of the delay term wrt state.
     *
     * @param station station
     * @param refstate reference spacecraft state
     *
     * @return Jacobian of the delay wrt state
     * @throws OrekitException  if frames transformations cannot be computed
     */
    private double[][] rangeErrorJacobianState(final GroundStation station,
                                               final SpacecraftState refstate)
        throws OrekitException {
        final double[][] finiteDifferencesJacobian =
                        Differentiation.differentiate(new StateFunction() {
                            public double[] value(final SpacecraftState state) throws OrekitException {
                                try {
                                    // evaluate target's elevation with a changed target position
                                    final double value = rangeErrorIonosphericModel(station, state);

                                    return new double[] {
                                        value
                                    };

                                } catch (OrekitException oe) {
                                    throw new OrekitExceptionWrapper(oe);
                                }
                            }
                        }, 1, OrbitType.CARTESIAN,
                        PositionAngle.TRUE, 15.0, 3).value(refstate);

        return finiteDifferencesJacobian;
    }


    /** Compute the derivative of the delay term wrt parameters.
     *
     * @param station ground station
     * @param driver driver for the station offset parameter
     * @param state spacecraft state
     * @return derivative of the delay wrt station offset parameter
     * @throws OrekitException  if frames transformations cannot be computed
     */
    private double rangeErrorParameterDerivative(final GroundStation station,
                                                 final ParameterDriver driver,
                                                 final SpacecraftState state)
        throws OrekitException {

        final ParameterFunction rangeError = new ParameterFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(final ParameterDriver parameterDriver) throws OrekitException {
                return rangeErrorIonosphericModel(station, state);
            }
        };

        final ParameterFunction rangeErrorDerivative =
                        Differentiation.differentiate(rangeError, driver, 3, 10.0);

        return rangeErrorDerivative.value(driver);

    }

    /** {@inheritDoc} */
    @Override
    public List<ParameterDriver> getParametersDrivers() {
        return Collections.emptyList();
    }

    @Override
    public void modify(final EstimatedMeasurement<TurnAroundRange> estimated)
        throws OrekitException {
        final TurnAroundRange measurement   = estimated.getObservedMeasurement();
        final GroundStation   masterStation = measurement.getMasterStation();
        final GroundStation   slaveStation  = measurement.getSlaveStation();
        final SpacecraftState state         = estimated.getStates()[0];

        final double[] oldValue = estimated.getEstimatedValue();

        // Update estimated value taking into account the ionospheric delay.
        // The ionospheric delay is directly added to the TurnAroundRange.
        final double masterDelay = rangeErrorIonosphericModel(masterStation, state);
        final double slaveDelay = rangeErrorIonosphericModel(slaveStation, state);
        final double[] newValue = oldValue.clone();
        newValue[0] = newValue[0] + masterDelay + slaveDelay;
        estimated.setEstimatedValue(newValue);

        // Update estimated derivatives with Jacobian of the measure wrt state
        final double[][] masterDjac = rangeErrorJacobianState(masterStation, state);
        final double[][] slaveDjac = rangeErrorJacobianState(slaveStation, state);
        final double[][] stateDerivatives = estimated.getStateDerivatives(0);
        for (int irow = 0; irow < stateDerivatives.length; ++irow) {
            for (int jcol = 0; jcol < stateDerivatives[0].length; ++jcol) {
                stateDerivatives[irow][jcol] += stateDerivatives[irow][jcol] +
                                                masterDjac[irow][jcol] +
                                                slaveDjac[irow][jcol];
            }
        }
        estimated.setStateDerivatives(0, stateDerivatives);

        // Update derivatives with respect to master station position
        for (final ParameterDriver driver : Arrays.asList(masterStation.getEastOffsetDriver(),
                                                          masterStation.getNorthOffsetDriver(),
                                                          masterStation.getZenithOffsetDriver())) {
            if (driver.isSelected()) {
                double parameterDerivative = estimated.getParameterDerivatives(driver)[0];
                parameterDerivative += rangeErrorParameterDerivative(masterStation, driver, state);
                estimated.setParameterDerivatives(driver, parameterDerivative);
            }
        }

        // Update derivatives with respect to slave station position
        for (final ParameterDriver driver : Arrays.asList(slaveStation.getEastOffsetDriver(),
                                                          slaveStation.getNorthOffsetDriver(),
                                                          slaveStation.getZenithOffsetDriver())) {
            if (driver.isSelected()) {
                double parameterDerivative = estimated.getParameterDerivatives(driver)[0];
                parameterDerivative += rangeErrorParameterDerivative(slaveStation, driver, state);
                estimated.setParameterDerivatives(driver, parameterDerivative);
            }
        }
    }

}
