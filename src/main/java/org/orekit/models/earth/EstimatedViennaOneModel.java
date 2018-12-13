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
package org.orekit.models.earth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hipparchus.Field;
import org.hipparchus.RealFieldElement;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateTimeComponents;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.ParameterDriver;

/** The Vienna1 tropospheric delay model for radio techniques.
 * With this implementation, the hydrostatic and wet zenith delays as well
 * as the ah and aw coefficients are estimated as {@link org.orekit.utils.ParameterDriver ParameterDriver}.
 *
 * This version considered the height correction for the hydrostatic part
 * developed by Niell, 1996.
 *
 * @see Boehm, J., Werl, B., and Schuh, H., (2006),
 *      "Troposhere mapping functions for GPS and very long baseline
 *      interferometry from European Centre for Medium-Range Weather
 *      Forecasts operational analysis data," J. Geophy. Res., Vol. 111,
 *      B02406, doi:10.1029/2005JB003629
 *
 * @author Bryan Cazabonne
 */
public class EstimatedViennaOneModel implements DiscreteTroposphericModel {

    /** Name of one of the parameters of this model: the hydrostatic zenith delay. */
    public static final String HYDROSTATIC_ZENITH_DELAY = "hydrostatic zenith delay";

    /** Name of one of the parameters of this model: the wet zenith delay. */
    public static final String WET_ZENITH_DELAY = "wet zenith delay";

    /** Name of the parameters of this model: the mapping function coefficient a<sub>h</sub>. */
    public static final String AH_COEFFICIENT = "mapping function coefficient ah";

    /** Name of the parameters of this model: the mapping function coefficient a<sub>w</sub>. */
    public static final String AW_COEFFICIENT = "mapping function coefficient aw";

    /** Serializable UID. */
    private static final long serialVersionUID = 3421856575466386588L;

    /** Geodetic site latitude, radians.*/
    private final double latitude;

    /** Driver for hydrostatic tropospheric delay parameter. */
    private final ParameterDriver initDateDHZParameterDriver;

    /** Driver for hydrostatic tropospheric delay parameter. */
    private final ParameterDriver endDateDHZParameterDriver;

    /** Driver for wet tropospheric delay parameter. */
    private final ParameterDriver initDateDWZParameterDriver;

    /** Driver for wet tropospheric delay parameter. */
    private final ParameterDriver endDateDWZParameterDriver;

    /** Driver for hydrostatic coefficient a<sub>h</sub>. */
    private final ParameterDriver initDateAHParameterDriver;

    /** Driver for hydrostatic coefficient a<sub>h</sub>. */
    private final ParameterDriver endDateAHParameterDriver;

    /** Driver for wet coefficient a<sub>w</sub>. */
    private final ParameterDriver initDateAWParameterDriver;

    /** Driver for wet coefficient a<sub>w</sub>. */
    private final ParameterDriver enDateAWParameterDriver;

    /** Build a new instance.
     * <p>
     * By definition, init date and end date parameters have the same name.
     * It is recommended to change the name of the parameters by adding a prefix with the reference date.
     * </p>
     * @param hydroDelayInitDate initial value for the hydrostatic zenith delay (first date)
     * @param hydroDelayEndDate initial value for the hydrostatic zenith delay (end date)
     * @param wetDelayInitDate initial value for wet zenith delay (first date)
     * @param wetDelayEndDate initial value for wet zenith delay (end date)
     * @param ahInitDate initial value for coefficient a<sub>h</sub> (first date)
     * @param ahEndDate initial value for coefficient a<sub>h</sub> (end date)
     * @param awInitDate initial value for coefficient a<sub>w</sub> (first date)
     * @param awEndDate initial value for coefficient a<sub>w</sub> (end date)
     * @param latitude geodetic latitude of the station, in radians
     */
    public EstimatedViennaOneModel(final double hydroDelayInitDate, final double hydroDelayEndDate,
                                   final double wetDelayInitDate, final double wetDelayEndDate,
                                   final double ahInitDate, final double ahEndDate,
                                   final double awInitDate, final double awEndDate,
                                   final double latitude) {
        initDateDHZParameterDriver = new ParameterDriver(EstimatedViennaOneModel.HYDROSTATIC_ZENITH_DELAY,
                                                         hydroDelayInitDate, FastMath.scalb(1.0, -2), 0.0, 10.0);

        endDateDHZParameterDriver  = new ParameterDriver(EstimatedViennaOneModel.HYDROSTATIC_ZENITH_DELAY,
                                                         hydroDelayEndDate, FastMath.scalb(1.0, -2), 0.0, 10.0);

        initDateDWZParameterDriver = new ParameterDriver(EstimatedViennaOneModel.WET_ZENITH_DELAY,
                                                         wetDelayInitDate, FastMath.scalb(1.0, -3), 0.0, 1.0);

        endDateDWZParameterDriver  = new ParameterDriver(EstimatedViennaOneModel.WET_ZENITH_DELAY,
                                                         wetDelayEndDate, FastMath.scalb(1.0, -3), 0.0, 1.0);

        initDateAHParameterDriver  = new ParameterDriver(EstimatedViennaOneModel.AH_COEFFICIENT,
                                                         ahInitDate, FastMath.scalb(1.0, -12), 0.0, 0.1);

        endDateAHParameterDriver   = new ParameterDriver(EstimatedViennaOneModel.AH_COEFFICIENT,
                                                         ahEndDate, FastMath.scalb(1.0, -12), 0.0, 0.1);

        initDateAWParameterDriver  = new ParameterDriver(EstimatedViennaOneModel.AW_COEFFICIENT,
                                                         awInitDate, FastMath.scalb(1.0, -15), 0.0, 0.01);

        enDateAWParameterDriver    = new ParameterDriver(EstimatedViennaOneModel.AW_COEFFICIENT,
                                                         awEndDate, FastMath.scalb(1.0, -15), 0.0, 0.01);

        this.latitude  = latitude;
    }

    /** {@inheritDoc} */
    @Override
    public double pathDelay(final double elevation, final double height,
                            final double[] parameters, final AbsoluteDate date) {
        // zenith delay
        final double[] delays = computeZenithDelay(height, parameters, date);
        // mapping function
        final double[] mappingFunction = mappingFactors(elevation, height, parameters, date);
        // Tropospheric path delay
        return delays[0] * mappingFunction[0] + delays[1] * mappingFunction[1];
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> T pathDelay(final T elevation, final T height,
                                                       final T[] parameters, final FieldAbsoluteDate<T> date) {
        // zenith delay
        final T[] delays = computeZenithDelay(height, parameters, date);
        // mapping function
        final T[] mappingFunction = mappingFactors(elevation, height, parameters, date);
        // Tropospheric path delay
        return delays[0].multiply(mappingFunction[0]).add(delays[1].multiply(mappingFunction[1]));
    }

    /** {@inheritDoc} */
    @Override
    public double[] computeZenithDelay(final double height, final double[] parameters, final AbsoluteDate date) {
        // Time intervals
        final double dt1 = endDateDHZParameterDriver.getReferenceDate().durationFrom(date);
        final double dt0 = date.durationFrom(initDateDHZParameterDriver.getReferenceDate());
        final double dt  = dt1 + dt0;

        // Zenith delay
        final double[] delays = new double[2];

        if (FastMath.abs(dt) < 0.001) {
            // Constant model
            delays[0] = parameters[0];
            delays[1] = parameters[2];
        } else {
            // Linear model
            delays[0] = (parameters[0] * dt1 + parameters[1] * dt0) / dt;
            delays[1] = (parameters[2] * dt1 + parameters[3] * dt0) / dt;
        }

        return delays;
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> T[] computeZenithDelay(final T height, final T[] parameters,
                                                                  final FieldAbsoluteDate<T> date) {
        // Field
        final Field<T> field = date.getField();

        // Time intervals
        final T dt1 = date.durationFrom(endDateDHZParameterDriver.getReferenceDate()).negate();
        final T dt0 = date.durationFrom(initDateDHZParameterDriver.getReferenceDate());
        final T dt  = dt1.add(dt0);

        // Zenith delay
        final T[] delays = MathArrays.buildArray(field, 2);

        if (FastMath.abs(dt).getReal() < 0.001) {
            // Constant model
            delays[0] = parameters[0];
            delays[1] = parameters[2];
        } else {
            // Linear model
            delays[0] = (parameters[0].multiply(dt1).add(parameters[1].multiply(dt0))).divide(dt);
            delays[1] = (parameters[2].multiply(dt1).add(parameters[3].multiply(dt0))).divide(dt);
        }

        return delays;
    }

    /** {@inheritDoc} */
    @Override
    public double[] mappingFactors(final double elevation, final double height,
                                   final double[] parameters, final AbsoluteDate date) {
        // Day of year computation
        final DateTimeComponents dtc = date.getComponents(TimeScalesFactory.getUTC());
        final int dofyear = dtc.getDate().getDayOfYear();

        // General constants | Hydrostatic part
        final double bh  = 0.0029;
        final double c0h = 0.062;
        final double c10h;
        final double c11h;
        final double psi;

        // sin(latitude) > 0 -> northern hemisphere
        if (FastMath.sin(latitude) > 0) {
            c10h = 0.001;
            c11h = 0.005;
            psi  = 0;
        } else {
            c10h = 0.002;
            c11h = 0.007;
            psi  = FastMath.PI;
        }

        // Compute hydrostatique coefficient c
        final double coef = ((dofyear - 28) / 365) * 2 * FastMath.PI + psi;
        final double ch = c0h + ((FastMath.cos(coef) + 1) * (c11h / 2) + c10h) * (1 - FastMath.cos(latitude));

        // General constants | Wet part
        final double bw = 0.00146;
        final double cw = 0.04391;

        // ah and aw coefficients
        // Time intervals
        final double dt1 = endDateAHParameterDriver.getReferenceDate().durationFrom(date);
        final double dt0 = date.durationFrom(initDateAHParameterDriver.getReferenceDate());
        final double dt  = dt1 + dt0;

        // Zenith delay

        final double ah;
        final double aw;
        if (FastMath.abs(dt) < 0.001) {
            // Constant model
            ah = parameters[4];
            aw = parameters[6];
        } else {
            // Linear model
            ah = (parameters[4] * dt1 + parameters[5] * dt0) / dt;
            aw = (parameters[6] * dt1 + parameters[7] * dt0) / dt;
        }

        final double[] function = new double[2];
        function[0] = computeFunction(ah, bh, ch, elevation);
        function[1] = computeFunction(aw, bw, cw, elevation);

        // Apply height correction
        final double correction = computeHeightCorrection(elevation, height);
        function[0] = function[0] + correction;

        return function;
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RealFieldElement<T>> T[] mappingFactors(final T elevation, final T height,
                                                              final T[] parameters, final FieldAbsoluteDate<T> date) {
        final Field<T> field = date.getField();
        final T zero = field.getZero();

        // Day of year computation
        final DateTimeComponents dtc = date.getComponents(TimeScalesFactory.getUTC());
        final int dofyear = dtc.getDate().getDayOfYear();

        // General constants | Hydrostatic part
        final T bh  = zero.add(0.0029);
        final T c0h = zero.add(0.062);
        final T c10h;
        final T c11h;
        final T psi;

        // sin(latitude) > 0 -> northern hemisphere
        if (FastMath.sin(latitude) > 0) {
            c10h = zero.add(0.001);
            c11h = zero.add(0.005);
            psi  = zero;
        } else {
            c10h = zero.add(0.002);
            c11h = zero.add(0.007);
            psi  = zero.add(FastMath.PI);
        }

        // Compute hydrostatique coefficient c
        final T coef = psi.add(((dofyear - 28) / 365) * 2 * FastMath.PI);
        final T ch = c11h.divide(2.0).multiply(FastMath.cos(coef).add(1.0)).add(c10h).multiply(1 - FastMath.cos(latitude)).add(c0h);

        // General constants | Wet part
        final T bw = zero.add(0.00146);
        final T cw = zero.add(0.04391);

        // ah and aw coefficients
        // Time intervals
        final T dt1 = date.durationFrom(endDateAHParameterDriver.getReferenceDate()).negate();
        final T dt0 = date.durationFrom(initDateAHParameterDriver.getReferenceDate());
        final T dt  = dt1.add(dt0);

        final T ah;
        final T aw;
        if (FastMath.abs(dt).getReal() < 0.001) {
            // Constant model
            ah = parameters[4];
            aw = parameters[6];
        } else {
            // Linear model
            ah = (parameters[4].multiply(dt1).add(parameters[5].multiply(dt0))).divide(dt);
            aw = (parameters[6].multiply(dt1).add(parameters[7].multiply(dt0))).divide(dt);
        }

        final T[] function = MathArrays.buildArray(field, 2);
        function[0] = computeFunction(ah, bh, ch, elevation);
        function[1] = computeFunction(aw, bw, cw, elevation);

        // Apply height correction
        final T correction = computeHeightCorrection(elevation, height, field);
        function[0] = function[0].add(correction);

        return function;
    }

    /** {@inheritDoc} */
    @Override
    public List<ParameterDriver> getParametersDrivers() {
        final List<ParameterDriver> list = new ArrayList<>(8);
        list.add(0, initDateDHZParameterDriver);
        list.add(1, endDateDHZParameterDriver);
        list.add(2, initDateDWZParameterDriver);
        list.add(3, endDateDWZParameterDriver);
        list.add(4, initDateAHParameterDriver);
        list.add(5, endDateAHParameterDriver);
        list.add(6, initDateAWParameterDriver);
        list.add(7, enDateAWParameterDriver);
        return Collections.unmodifiableList(list);
    }

    /** Compute the mapping function related to the coefficient values and the elevation.
     * @param a a coefficient
     * @param b b coefficient
     * @param c c coefficient
     * @param elevation the elevation of the satellite, in radians.
     * @return the value of the function at a given elevation
     */
    private double computeFunction(final double a, final double b, final double c, final double elevation) {
        final double sinE = FastMath.sin(elevation);
        // Numerator
        final double numMP = 1 + a / (1 + b / (1 + c));
        // Denominateur
        final double denMP = sinE + a / (sinE + b / (sinE + c));

        final double felevation = numMP / denMP;

        return felevation;
    }

    /** Compute the mapping function related to the coefficient values and the elevation.
     * @param <T> type of the elements
     * @param a a coefficient
     * @param b b coefficient
     * @param c c coefficient
     * @param elevation the elevation of the satellite, in radians.
     * @return the value of the function at a given elevation
     */
    private <T extends RealFieldElement<T>> T computeFunction(final T a, final T b, final T c, final T elevation) {
        final T sinE = FastMath.sin(elevation);
        // Numerator
        final T numMP = a.divide(b.divide(c.add(1.0)).add(1.0)).add(1.0);
        // Denominateur
        final T denMP = a.divide(b.divide(c.add(sinE)).add(sinE)).add(sinE);

        final T felevation = numMP.divide(denMP);

        return felevation;
    }

    /** This method computes the height correction for the hydrostatic
     *  component of the mapping function.
     *  The formulas are given by Neill's paper, 1996:
     *<p>
     *      Niell A. E. (1996)
     *      "Global mapping functions for the atmosphere delay of radio wavelengths,”
     *      J. Geophys. Res., 101(B2), pp.  3227–3246, doi:  10.1029/95JB03048.
     *</p>
     * @param elevation the elevation of the satellite, in radians.
     * @param height the height of the station in m above sea level.
     * @return the height correction, in m
     */
    private double computeHeightCorrection(final double elevation, final double height) {
        final double sinE = FastMath.sin(elevation);
        // Ref: Eq. 4
        final double function = computeFunction(2.53e-5, 5.49e-3, 1.14e-3, elevation);
        // Ref: Eq. 6
        final double dmdh = (1 / sinE) - function;
        // Ref: Eq. 7
        final double correction = dmdh * (height / 1000);
        return correction;
    }

    /** This method computes the height correction for the hydrostatic
     *  component of the mapping function.
     *  The formulas are given by Neill's paper, 1996:
     *<p>
     *      Niell A. E. (1996)
     *      "Global mapping functions for the atmosphere delay of radio wavelengths,”
     *      J. Geophys. Res., 101(B2), pp.  3227–3246, doi:  10.1029/95JB03048.
     *</p>
     * @param <T> type of the elements
     * @param elevation the elevation of the satellite, in radians.
     * @param height the height of the station in m above sea level.
     * @param field field to which the elements belong
     * @return the height correction, in m
     */
    private <T extends RealFieldElement<T>> T computeHeightCorrection(final T elevation, final T height, final Field<T> field) {
        final T zero = field.getZero();
        final T sinE = FastMath.sin(elevation);
        // Ref: Eq. 4
        final T function = computeFunction(zero.add(2.53e-5), zero.add(5.49e-3), zero.add(1.14e-3), elevation);
        // Ref: Eq. 6
        final T dmdh = sinE.reciprocal().subtract(function);
        // Ref: Eq. 7
        final T correction = dmdh.multiply(height.divide(1000.0));
        return correction;
    }

}
