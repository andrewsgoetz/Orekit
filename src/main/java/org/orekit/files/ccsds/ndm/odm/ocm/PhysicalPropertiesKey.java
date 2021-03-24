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
package org.orekit.files.ccsds.ndm.odm.ocm;

import org.orekit.files.ccsds.definitions.Units;
import org.orekit.files.ccsds.utils.ContextBinding;
import org.orekit.files.ccsds.utils.lexical.ParseToken;
import org.orekit.files.ccsds.utils.lexical.TokenType;
import org.orekit.utils.units.Unit;


/** Keys for {@link PhysicalProperties physical properties data} entries.
 * @author Luc Maisonobe
 * @since 11.0
 */
public enum PhysicalPropertiesKey {

    /** Comment entry. */
    COMMENT((token, context, container) ->
            token.getType() == TokenType.ENTRY ? container.addComment(token.getContentAsNormalizedString()) : true),

    /** Satellite manufacturer name. */
    MANUFACTURER((token, context, container) -> token.processAsNormalizedString(container::setManufacturer)),

    /** Bus model name. */
    BUS_MODEL((token, context, container) -> token.processAsNormalizedString(container::setBusModel)),

    /** Other space objects this object is docked to. */
    DOCKED_WITH((token, context, container) -> token.processAsNormalizedList(container::setDockedWith)),

    /** Attitude-independent drag cross-sectional area, not already into attitude-dependent area along OEB. */
    DRAG_CONST_AREA((token, context, container) -> token.processAsDouble(Units.M2, container::setDragConstantArea)),

    /** Nominal drag coefficient. */
    DRAG_COEFF_NOM((token, context, container) -> token.processAsDouble(Unit.ONE, container::setNominalDragCoefficient)),

    /** Drag coefficient 1σ uncertainty. */
    DRAG_UNCERTAINTY((token, context, container) -> token.processAsDouble(Unit.PERCENT, container::setDragUncertainty)),

    /** Total mass at beginning of life. */
    INITIAL_WET_MASS((token, context, container) -> token.processAsDouble(Unit.KILOGRAM, container::setInitialWetMass)),

    /** Total mass at T₀. */
    WET_MASS((token, context, container) -> token.processAsDouble(Unit.KILOGRAM, container::setWetMass)),

    /** Mass without propellant. */
    DRY_MASS((token, context, container) -> token.processAsDouble(Unit.KILOGRAM, container::setDryMass)),

    /** Optimally Enclosing Box parent reference frame. */
    OEB_PARENT_FRAME((token, context, container) -> token.processAsFrame(container::setOebParentFrame, context, true, true, false)),

    /** Optimally Enclosing Box parent reference frame epoch. */
    OEB_PARENT_FRAME_EPOCH((token, context, container) -> token.processAsDate(container::setOebParentFrameEpoch, context)),

    /** Quaternion defining Optimally Enclosing Box (first vectorial component). */
    OEB_Q1((token, context, container) -> token.processAsIndexedDouble(1, Unit.ONE, container::setOebQ)),

    /** Quaternion defining Optimally Enclosing Box (second vectorial component). */
    OEB_Q2((token, context, container) -> token.processAsIndexedDouble(2, Unit.ONE, container::setOebQ)),

    /** Quaternion defining Optimally Enclosing Box (third vectorial component). */
    OEB_Q3((token, context, container) -> token.processAsIndexedDouble(3, Unit.ONE, container::setOebQ)),

    /** Quaternion defining Optimally Enclosing Box (scalar component). */
    OEB_QC((token, context, container) -> token.processAsIndexedDouble(0, Unit.ONE, container::setOebQ)),

    /** Dimensions of Optimally Enclosing Box along X-OEB (i.e max). */
    OEB_MAX((token, context, container) -> token.processAsDouble(Unit.METRE, container::setOebMax)),

    /** Dimensions of Optimally Enclosing Box along Y-OEB (i.e intermediate). */
    OEB_INT((token, context, container) -> token.processAsDouble(Unit.METRE, container::setOebIntermediate)),

    /** Dimensions of Optimally Enclosing Box along Z-OEB (i.e min). */
    OEB_MIN((token, context, container) -> token.processAsDouble(Unit.METRE, container::setOebMin)),

    /** Cross-sectional area of Optimally Enclosing Box along X-OEB. */
    AREA_ALONG_OEB_MAX((token, context, container) -> token.processAsDouble(Units.M2, container::setOebAreaAlongMax)),

    /** Cross-sectional area of Optimally Enclosing Box along Y-OEB. */
    AREA_ALONG_OEB_INT((token, context, container) -> token.processAsDouble(Units.M2, container::setOebAreaAlongIntermediate)),

    /** Cross-sectional area of Optimally Enclosing Box along Z-OEB. */
    AREA_ALONG_OEB_MIN((token, context, container) -> token.processAsDouble(Units.M2, container::setOebAreaAlongMin)),

    /** Minimum cross-sectional area for collision probability estimation purposes. */
    AREA_MIN_FOR_PC((token, context, container) -> token.processAsDouble(Units.M2, container::setMinAreaForCollisionProbability)),

    /** Maximum cross-sectional area for collision probability estimation purposes. */
    AREA_MAX_FOR_PC((token, context, container) -> token.processAsDouble(Units.M2, container::setMaxAreaForCollisionProbability)),

    /** Typical (50th percentile) cross-sectional area for collision probability estimation purposes. */
    AREA_TYP_FOR_PC((token, context, container) -> token.processAsDouble(Units.M2, container::setTypAreaForCollisionProbability)),

    /** Typical (50th percentile) radar cross-section. */
    RCS((token, context, container) -> token.processAsDouble(Units.M2, container::setRcs)),

    /** Minimum radar cross-section. */
    RCS_MIN((token, context, container) -> token.processAsDouble(Units.M2, container::setMinRcs)),

    /** Maximum radar cross-section. */
    RCS_MAX((token, context, container) -> token.processAsDouble(Units.M2, container::setMaxRcs)),

    /** Attitude-independent SRP area, not already into attitude-dependent area along OEB. */
    SRP_CONST_AREA((token, context, container) -> token.processAsDouble(Units.M2, container::setSrpConstantArea)),

    /** Nominal SRP coefficient. */
    SOLAR_RAD_COEFF((token, context, container) -> token.processAsDouble(Unit.ONE, container::setNominalSrpCoefficient)),

    /** SRP coefficient 1σ uncertainty. */
    SOLAR_RAD_UNCERTAINTY((token, context, container) -> token.processAsDouble(Unit.PERCENT, container::setSrpUncertainty)),

    /** Typical (50th percentile) visual magnitude. */
    VM_ABSOLUTE((token, context, container) -> token.processAsDouble(Unit.ONE, container::setVmAbsolute)),

    /** Minimum apparent visual magnitude. */
    VM_APPARENT_MIN((token, context, container) -> token.processAsDouble(Unit.ONE, container::setVmApparentMin)),

    /** Typical (50th percentile) apparent visual magnitude. */
    VM_APPARENT((token, context, container) -> token.processAsDouble(Unit.ONE, container::setVmApparent)),

    /** Maximum apparent visual magnitude. */
    VM_APPARENT_MAX((token, context, container) -> token.processAsDouble(Unit.ONE, container::setVmApparentMax)),

    /** Typical (50th percentile) coefficient of reflectivity. */
    REFLECTIVITY((token, context, container) -> token.processAsDouble(Unit.ONE, container::setReflectivity)),

    /** Attitude control mode. */
    ATT_CONTROL_MODE((token, context, container) -> token.processAsNormalizedString(container::setAttitudeControlMode)),

    /** Type of actuator for attitude control. */
    ATT_ACTUATOR_TYPE((token, context, container) -> token.processAsNormalizedString(container::setAttitudeActuatorType)),

    /** Accuracy of attitude knowledge. */
    ATT_KNOWLEDGE((token, context, container) -> token.processAsAngle(container::setAttitudeKnowledgeAccuracy)),

    /** Accuracy of attitude control. */
    ATT_CONTROL((token, context, container) -> token.processAsAngle(container::setAttitudeControlAccuracy)),

    /** Overall accuracy of spacecraft to maintain attitude. */
    ATT_POINTING((token, context, container) -> token.processAsAngle(container::setAttitudePointingAccuracy)),

    /** Average number of orbit or attitude maneuvers per year. */
    AVG_MANEUVER_FREQ((token, context, container) -> token.processAsDouble(Units.NB_PER_Y, container::setManeuversPerYear)),

    /** Maximum composite thrust the spacecraft can accomplish. */
    MAX_THRUST((token, context, container) -> token.processAsDouble(Unit.NEWTON, container::setMaxThrust)),

    /** Total ΔV capability at beginning of life. */
    DV_BOL((token, context, container) -> token.processAsDouble(Units.KM_PER_S, container::setBolDv)),

    /** Total ΔV remaining for spacecraft. */
    DV_REMAINING((token, context, container) -> token.processAsDouble(Units.KM_PER_S, container::setRemainingDv)),

    /** Moment of inertia about X-axis. */
    IXX((token, context, container) -> token.processAsDoublyIndexedDouble(0, 0, Units.KG_PER_M2, container::setInertiaMatrixEntry)),

    /** Moment of inertia about Y-axis. */
    IYY((token, context, container) -> token.processAsDoublyIndexedDouble(1, 1, Units.KG_PER_M2, container::setInertiaMatrixEntry)),

    /** Moment of inertia about Z-axis. */
    IZZ((token, context, container) -> token.processAsDoublyIndexedDouble(2, 2, Units.KG_PER_M2, container::setInertiaMatrixEntry)),

    /** Inertia cross product of the X and Y axes. */
    IXY((token, context, container) -> token.processAsDoublyIndexedDouble(0, 1, Units.KG_PER_M2, container::setInertiaMatrixEntry)),

    /** Inertia cross product of the X and Z axes. */
    IXZ((token, context, container) -> token.processAsDoublyIndexedDouble(0, 2, Units.KG_PER_M2, container::setInertiaMatrixEntry)),

    /** Inertia cross product of the Y and Z axes. */
    IYZ((token, context, container) -> token.processAsDoublyIndexedDouble(1, 2, Units.KG_PER_M2, container::setInertiaMatrixEntry));

    /** Processing method. */
    private final TokenProcessor processor;

    /** Simple constructor.
     * @param processor processing method
     */
    PhysicalPropertiesKey(final TokenProcessor processor) {
        this.processor = processor;
    }

    /** Process an token.
     * @param token token to process
     * @param context context binding
     * @param data data to fill
     * @return true of token was accepted
     */
    public boolean process(final ParseToken token, final ContextBinding context, final PhysicalProperties data) {
        return processor.process(token, context, data);
    }

    /** Interface for processing one token. */
    interface TokenProcessor {
        /** Process one token.
         * @param token token to process
         * @param context context binding
         * @param data data to fill
         * @return true of token was accepted
         */
        boolean process(ParseToken token, ContextBinding context, PhysicalProperties data);
    }

}