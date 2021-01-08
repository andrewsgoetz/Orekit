/* Copyright 2002-2020 CS GROUP
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

package org.orekit.files.ccsds.ndm.odm.omm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.orekit.files.ccsds.ndm.odm.OStateData;
import org.orekit.orbits.CartesianOrbit;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;

/** This class gathers the informations present in the Orbital Mean-Elements Message (OMM),
 * and contains methods to generate a {@link CartesianOrbit}, a {@link KeplerianOrbit},
 * a {@link SpacecraftState} and, eventually, a {@link TLE}.
 * @author sports
 * @since 6.1
 */
public class OMMData extends OStateData  {

    /** Mean motion (the Keplerian Mean motion in revolutions per day). To be used instead of semi-major
     * axis if MEAN_ELEMENT_THEORY = SGP/SGP4. */
    private double meanMotion;

    /** Ephemeris Type, only required if MEAN_ELEMENT_THEORY = SGP/SGP4. Some sources suggest the coding for
     * the EPHEMERIS_TYPE keyword: 1 = SGP, 2 = SGP4, 3 = SDP4, 4 = SGP8, 5 = SDP8. Default value = 0.
     */
    private int ephemerisType;

    /** Classification Type, only required if MEAN_ELEMENT_THEORY = SGP/SGP4. Some sources suggest the
     *  following coding for the CLASSIFICATION_TYPE keyword: U = unclassified, S = secret. Default value = U.
     */
    private char classificationType;

    /** NORAD Catalog Number ("Satellite Number"), an integer of up to nine digits. */
    private Integer noradID;

    /** Element set number for this satellite, only required if MEAN_ELEMENT_THEORY = SGP/SGP4.
     * Normally incremented sequentially, but may be out of sync if it is generated from a backup source.
     * Used to distinguish different TLEs, and therefore only meaningful if TLE based data is being exchanged. */
    private String elementSetNo;

    /** Revolution Number, only required if MEAN_ELEMENT_THEORY = SGP/SGP4. */
    private int revAtEpoch;

    /** SGP/SGP4 drag-like coefficient (in units 1/[Earth radii]), only required if MEAN_ELEMENT_THEORY = SGP/SGP4. */
    private Double bStar;

    /** First Time Derivative of the Mean Motion, only required if MEAN_ELEMENT_THEORY = SGP. */
    private Double meanMotionDot;

    /** Second Time Derivative of Mean Motion, only required if MEAN_ELEMENT_THEORY = SGP. */
    private Double meanMotionDotDot;

    /** TLE related parameters comments. The list contains a string for each line of comment. */
    private List<String> dataTleRelatedParametersComment;

    /** Create an empty data set.
     */
    OMMData() {
        dataTleRelatedParametersComment = Collections.emptyList();
    }

    /** Get the orbit mean motion.
     * @return the orbit mean motion
     */
    public double getMeanMotion() {
        return meanMotion;
    }

    /** Set the orbit mean motion.
     * @param motion the mean motion to be set
     */
    void setMeanMotion(final double motion) {
        this.meanMotion = motion;
    }

    /** Get the ephemeris type.
     * @return the ephemerisType
     */
    public int getEphemerisType() {
        return ephemerisType;
    }

    /** Set the ephemeris type.
     * @param ephemerisType the ephemeris type to be set
     */
    void setEphemerisType(final int ephemerisType) {
        this.ephemerisType = ephemerisType;
    }

    /** Get the classification type.
     * @return the classificationType
     */
    public char getClassificationType() {
        return classificationType;
    }

    /** Set the classification type.
     * @param classificationType the classification type to be set
     */
    void setClassificationType(final char classificationType) {
        this.classificationType = classificationType;
    }

    /** Get the NORAD Catalog Number ("Satellite Number").
     * @return the NORAD Catalog Number
     */
    public Integer getNoradID() {
        return noradID;
    }

    /** Set the NORAD Catalog Number ("Satellite Number").
     * @param noradID the element set number to be set
     */
    void setNoradID(final Integer noradID) {
        this.noradID = noradID;
    }

    /** Get the element set number for this satellite.
     * @return the element set number for this satellite
     */
    public String getElementSetNumber() {
        return elementSetNo;
    }

    /** Set the element set number for this satellite.
     * @param elementSetNo the element set number to be set
     */
    void setElementSetNo(final String elementSetNo) {
        this.elementSetNo = elementSetNo;
    }

    /** Get the revolution rumber.
     * @return the revolution rumber
     */
    public int getRevAtEpoch() {
        return revAtEpoch;
    }

    /** Set the revolution rumber.
     * @param revAtEpoch the Revolution Number to be set
     */
    void setRevAtEpoch(final int revAtEpoch) {
        this.revAtEpoch = revAtEpoch;
    }

    /** Get the SGP/SGP4 drag-like coefficient.
     * @return the SGP/SGP4 drag-like coefficient
     */
    public double getBStar() {
        return bStar;
    }

    /** Set the SGP/SGP4 drag-like coefficient.
     * @param bStar the SGP/SGP4 drag-like coefficient to be set
     */
    void setbStar(final double bStar) {
        this.bStar = bStar;
    }

    /** Get the first time derivative of the mean motion.
     * @return the first time derivative of the mean motion
     */
    public double getMeanMotionDot() {
        return meanMotionDot;
    }

    /** Set the first time derivative of the mean motion.
     * @param meanMotionDot the first time derivative of the mean motion to be set
     */
    void setMeanMotionDot(final double meanMotionDot) {
        this.meanMotionDot = meanMotionDot;
    }

    /** Get the second time derivative of the mean motion.
     * @return the second time derivative of the mean motion
     */
    public double getMeanMotionDotDot() {
        return meanMotionDotDot;
    }

    /** Set the second time derivative of the mean motion.
     * @param meanMotionDotDot the second time derivative of the mean motion to be set
     */
    void setMeanMotionDotDot(final double meanMotionDotDot) {
        this.meanMotionDotDot = meanMotionDotDot;
    }

    /** Get the comment for TLE related parameters.
     * @return comment for TLE related parameters
     */
    public List<String> getTLERelatedParametersComment() {
        return Collections.unmodifiableList(dataTleRelatedParametersComment);
    }

    /** Set the comment for TLE related parameters.
     * @param comment comment to set
     */
    void setTLERelatedParametersComment(final List<String> comment) {
        dataTleRelatedParametersComment = new ArrayList<>(comment);
    }

}
