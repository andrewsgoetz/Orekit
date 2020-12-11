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
package org.orekit.files.ccsds;

/** Keywords for CCSDS Navigation Data Messages.<p>
 * Only these should be used.<p>
 *  The enumeration is split in 3 parts:<p>
 *  - Common NDM keywords;<p>
 *  - Orbit Data Messages (ODM) specific keywords;<p>
 *  - Tracking Data Messages (TDM) specific keywords.<p>
 * References:<p>
 * - <a href="https://public.ccsds.org/Pubs/502x0b2c1.pdf">CCSDS 502.0-B-2 recommended standard</a> ("Orbit Data Messages", Blue Book, Issue 2.0, November 2009).<p>
 * - <a href="https://public.ccsds.org/Pubs/503x0b1c1.pdf">CCSDS 503.0-B-1 recommended standard</a> ("Tracking Data Message", Blue Book, Issue 1.0, November 2007).
 * @author sports
 * @author Maxime Journot
 * @since 6.1
 */
public enum Keyword {

    // ---------------------------------------------------
    // Common NDM (Navigation Data Message) CCSDS keywords
    // ---------------------------------------------------

    /** Comments specific to ODM and ADM file. */
    COMMENT,
    /** File creation date in UTC. */
    CREATION_DATE,
    /** Creating agency or operator. */
    ORIGINATOR,
    /** Unique ID identifying a message from a given originator.
     * @since 11.0
     */
    MESSAGE_ID,
    /** Classification of this message.
     * @since 11.0
     */
    CLASSIFICATION,
    /** Spacecraft name for which the orbit state is provided. */
    OBJECT_NAME,
    /** Alternate names fir this space object.
     * @since 11.0
     */
    ALTERNATE_NAMES,
    /** Object identifier of the object for which the orbit state is provided. */
    OBJECT_ID,
    /** Origin of reference frame. */
    CENTER_NAME,
    /** Time system used for state vector, maneuver, and covariance data. */
    TIME_SYSTEM,
    /** Epoch of state vector and optional Keplerian elements.
     *  Or epoch of a TDM observation.
     */
    EPOCH,
    /** Start of total time span covered by: <p>
     * - Ephemerides data and covariance data;<p>
     * - Tracking data session.
     */
    START_TIME,
    /** End of total time span covered by: <p>
     * - Ephemerides data and covariance data;<p>
     * - Tracking data session.
     */
    STOP_TIME,
    /** User defined parameter, where X is replaced by a variable length user specified character
     *  string. Any number of user defined parameters may be included, if necessary to provide
     *  essential information that cannot be conveyed in COMMENT statements. */
    USER_DEFINED_X,
    /** Keyword used to delineate the start of a Meta-data block. */
    META_START,
    /** Keyword used to delineate the end of a Meta-data block. */
    META_STOP,
    /** Maneuver duration (If = 0, impulsive maneuver). */
    MAN_DURATION,
    /** Coordinate system for velocity increment vector. Its value can either be RSW, RTN (both
     * indicating "Radial, Transverse, Normal") or TNW. */
    MAN_REF_FRAME,
    /** Start of useable time span covered by ephemerides data, it may be
     * necessary to allow for proper interpolation. */
    USEABLE_START_TIME,
    /** End of useable time span covered by ephemerides data, it may be
     * necessary to allow for proper interpolation. */
    USEABLE_STOP_TIME,
    /** The interpolation degree. */
    INTERPOLATION_DEGREE,

    // -------------------------------------------
    // Orbit Data Messages (ODM) specific keywords
    // -------------------------------------------

    /** CCSDS OPM format version. */
    CCSDS_OPM_VERS,
    /** CCSDS OMM format version. */
    CCSDS_OMM_VERS,
    /** CCSDS OEM format version. */
    CCSDS_OEM_VERS,
    /** CCSDS OCM format version.
     * @since 11.0
     */
    CCSDS_OCM_VERS,
    /** Programmatic Point Of Contact at originator.
     * @since 11.0
     */
    ORIGINATOR_POC,
    /** Position of Programmatic Point Of Contact at originator.
     * @since 11.0
     */
    ORIGINATOR_POSITION,
    /** Phone number of Programmatic Point Of Contact at originator.
     * @since 11.0
     */
    ORIGINATOR_PHONE,
    /** Address of Programmatic Point Of Contact at originator.
     * @since 11.0
     */
    ORIGINATOR_ADDRESS,
    /** Creating agency or operator.
     * @since 11.0
     */
    TECH_ORG,
    /** Technical Point Of Contact at originator.
     * @since 11.0
     */
    TECH_POC,
    /** Position of Technical Point Of Contact at originator.
     * @since 11.0
     */
    TECH_POSITION,
    /** Phone number of Technical Point Of Contact at originator.
     * @since 11.0
     */
    TECH_PHONE,
    /** Address of Technical Point Of Contact at originator.
     * @since 11.0
     */
    TECH_ADDRESS,
    /** Unique ID identifying previous message from a given originator.
     * @since 11.0
     */
    PREV_MESSAGE_ID,
    /** Creation date of previous message from a given originator.
     * @since 11.0
     */
    PREV_MESSAGE_EPOCH,
    /** Unique ID identifying next message from a given originator.
     * @since 11.0
     */
    NEXT_MESSAGE_ID,
    /** Creation date of next message from a given originator.
     * @since 11.0
     */
    NEXT_MESSAGE_EPOCH,
    /** Names of Attitude Data Messages link to this Orbit Data Message.
     * @since 11.0
     */
    ATT_MESSAGE_LINK,
    /** Names of Conjunction Data Messages link to this Orbit Data Message.
     * @since 11.0
     */
    CDM_MESSAGE_LINK,
    /** Names of Pointing Request Messages link to this Orbit Data Message.
     * @since 11.0
     */
    PRM_MESSAGE_LINK,
    /** Names of Reentry Data Messages link to this Orbit Data Message.
     * @since 11.0
     */
    RDM_MESSAGE_LINK,
    /** Names of Tracking Data Messages link to this Orbit Data Message.
     * @since 11.0
     */
    TDM_MESSAGE_LINK,
    /** International designator for the object as assigned by the UN Committee
     * on Space Research (COSPAR) and the US National Space Science Data Center (NSSDC).
     * @since 11.0
     */
    INTERNATIONAL_DESIGNATOR,
    /** Operator of the space object.
     * @since 11.0
     */
    OPERATOR,
    /** Owner of the space object.
     * @since 11.0
     */
    OWNER,
    /** Name of the space object mission.
     * @since 11.0
     */
    MISSION,
    /** Name of the constellation this space object belongs to.
     * @since 11.0
     */
    CONSTELLATION,
    /** Epoch of initial launch.
     * @since 11.0
     */
    LAUNCH_EPOCH,
    /** Country of launch.
     * @since 11.0
     */
    LAUNCH_COUNTRY,
    /** Site of launch.
     * @since 11.0
     */
    LAUNCH_SITE,
    /** Provider of launch.
     * @since 11.0
     */
    LAUNCH_PROVIDER,
    /** Integrator of launch.
     * @since 11.0
     */
    LAUNCH_INTEGRATOR,
    /** Pad of launch.
     * @since 11.0
     */
    LAUNCH_PAD,
    /** Launch platform.
     * @since 11.0
     */
    LAUNCH_PLATFORM,
    /** Epoch of the <em>most recent</em> deployement of this space object in the parent/child deployement sequence.
     * @since 11.0
     */
    RELEASE_EPOCH,
    /** Epoch of the beginning of mission operations.
     * @since 11.0
     */
    MISSION_START_EPOCH,
    /** Epoch of the cessation of mission operations.
     * @since 11.0
     */
    MISSION_END_EPOCH,
    /** Epoch (actual or estimated) of the space object reentry.
     * @since 11.0
     */
    REENTRY_EPOCH,
    /** Estimated remaining lifetime in days.
     * @since 11.0
     */
    LIFETIME,
    /** Specification of satellite catalog source.
     * @since 11.0
     */
    CATALOG_NAME,
    /** Type of object.
     * @see CCSDSObjectType
     * @since 11.0
     */
    OBJECT_TYPE,
    /** Operational status.
     * @see CCSDSOpsStatus
     * @since 11.0
     */
    OPS_STATUS,
    /** Orbit type.
     * @see CCSDSOrbitType
     * @since 11.0
     */
    ORBIT_TYPE,
    /** List of elements of information data blocks included in this message.
     * @since 11.0
     */
    OCM_DATA_ELEMENTS,
    /** Default epoch to which <em>all</em> relative times are referenced in data blocks,
     * unless overridden by block-specific {@link #EPOCH_TZERO} values.
     * @since 11.0
     */
    EPOCH_TZERO,
    /** Number of clock seconds occurring during one SI second.
     * @since 11.0
     */
    SEC_CLK_PER_SI_SEC,
    /** Number of SI seconds in the chosen central body’s “day”.
     * @since 11.0
     */
    SEC_PER_DAY,
    /** Time of the earliest data contained in the OCM.
     * @since 11.0
     */
    EARLIEST_TIME,
    /** Time of the latest data contained in the OCM.
     * @since 11.0
     */
    LATEST_TIME,
    /** Span of time that the OCM covers.
     * @since 11.0
     */
    TIME_SPAN,
    /** Difference (TAI – UTC) in seconds at epoch {@link #DEF_EPOCH_TZERO}.
     * @since 11.0
     */
    TAIMUTC_AT_TZERO,
    /** Difference (UT1 – UTC) in seconds at epoch {@link #DEF_EPOCH_TZERO}.
     * @since 11.0
     */
    UT1MUTC_AT_TZERO,
    /** Source and version of Earth Orientation Parameters.
     * @since 11.0
     */
    EOP_SOURCE,
    /** Interpolation method for Earth Orientation Parameters.
     * @since 11.0
     */
    INTERP_METHOD_EOP,
    /** Start of orbit data section.
     * @since 11.0
     */
    ORB_START,
    /** Stop of orbit data section.
     * @since 11.0
     */
    ORB_STOP,
    /** Orbit identification number.
     * @since 11.0
     */
    ORB_ID,
    /** Identification number of previous orbit.
     * @since 11.0
     */
    ORB_PREV_ID,
    /** Identification number of next orbit.
     * @since 11.0
     */
    ORB_NEXT_ID,
    /** Basis of this orbit state time history data.
     * @see CCSDSOrbitBasis
     * @since 11.0
     */
    ORB_BASIS,
    /** Identification number of the orbit determination or simulation upon which this orbit is based.
     * @since 11.0
     */
    ORB_BASIS_ID,
    /** Type of averaging (Osculating, mean Brouwer, other...).
     * @since 11.0
     */
    ORB_AVERAGING,
    /** Reference epoch for all relative times in the orbit state block.
     * @since 11.0
     */
    ORB_EPOCH_TZERO,
    /** Time system for {@link #ORB_EPOCH_TZERO}.
     * @since 11.0
     */
    ORB_TIME_SYSTEM,
    /** Reference frame of the orbit.
     * @since 11.0
     */
    ORB_REF_FRAME,
    /** Epoch of the {@link #ORB_REF_FRAME orbit reference frame}.
     * @since 11.0
     */
    ORB_FRAME_EPOCH,
    /** Orbit element set type.
     * @see CCSDSElementsType
     * @since 11.0
     */
    ORB_TYPE,
    /** Number of elements (excluding time) contain in the element set.
     * @since 11.0
     */
    ORB_N,
    /** Definition of orbit elements.
     * @since 11.0
     */
    ORB_ELEMENTS,
    /** Start of object physical characteristics data section.
     * @since 11.0
     */
    PHYS_START,
    /** Stop of object physical characteristics data section.
     * @since 11.0
     */
    PHYS_STOP,
    /** Start of covariance data section.
     * @since 11.0
     */
    COV_START,
    /** Stop of covariance data section.
     * @since 11.0
     */
    COV_STOP,
    /** Start of state transition matrix data section.
     * @since 11.0
     */
    STM_START,
    /** Stop of state transition matrix data section.
     * @since 11.0
     */
    STM_STOP,
    /** Start of maneuver data section.
     * @since 11.0
     */
    MAN_START,
    /** Stop of maneuver data section.
     * @since 11.0
     */
    MAN_STOP,
    /** Start of perturbations data section.
     * @since 11.0
     */
    PERT_START,
    /** Stop of perturbations data section.
     * @since 11.0
     */
    PERT_STOP,
    /** Start of orbit determination data section.
     * @since 11.0
     */
    OD_START,
    /** Stop of orbit determination data section.
     * @since 11.0
     */
    OD_STOP,
    /** Start of user-defined parameters data section.
     * @since 11.0
     */
    USER_START,
    /** Stop of user-defined parameters data section.
     * @since 11.0
     */
    USER_STOP,
    /** Name of the reference frame in which the state vector and optional Keplerian element data are given. */
    REF_FRAME,
    /** Epoch of reference frame, if not intrinsic to the definition of the reference frame. */
    REF_FRAME_EPOCH,
    /** Mean element theory. */
    MEAN_ELEMENT_THEORY,
    /** Position vector X-component. */
    X,
    /** Position vector Y-component. */
    Y,
    /** Position vector Z-component. */
    Z,
    /** Velocity vector X-component. */
    X_DOT,
    /** Velocity vector Y-component. */
    Y_DOT,
    /** Velocity vector Z-component. */
    Z_DOT,
    /** Orbit semi-major axis. */
    SEMI_MAJOR_AXIS,
    /** Mean Motion. */
    MEAN_MOTION,
    /** Orbit eccentricity. */
    ECCENTRICITY,
    /** Orbit inclination. */
    INCLINATION,
    /** Orbit right ascension of ascending node. */
    RA_OF_ASC_NODE,
    /** Orbit argument of pericenter. */
    ARG_OF_PERICENTER,
    /** Orbit true anomaly. */
    TRUE_ANOMALY,
    /** Orbit mean anomaly.*/
    MEAN_ANOMALY,
    /** Gravitational coefficient. */
    GM,
    /** Spacecraft mass. */
    MASS,
    /** Solar radiation pressure area. */
    SOLAR_RAD_AREA,
    /** Solar radiation pressure coefficient. */
    SOLAR_RAD_COEFF,
    /** Drag area. */
    DRAG_AREA,
    /** Drag coefficient. */
    DRAG_COEFF,
    /** Ephemeris type. */
    EPHEMERIS_TYPE,
    /** Classification type. */
    CLASSIFICATION_TYPE,
    /** NORAD catalogue number. */
    NORAD_CAT_ID,
    /** Element set number of the satellite. */
    ELEMENT_SET_NO,
    /** Revolution Number. */
    REV_AT_EPOCH,
    /** SGP/SGP4 drag-like coefficient. */
    BSTAR,
    /** First Time Derivative of the Mean Motion. */
    MEAN_MOTION_DOT,
    /** Second Time Derivative of the Mean Motion. */
    MEAN_MOTION_DDOT,
    /** Coordinate system for covariance matrix. Its value can either be RSW, RTN (both indicating
    /* "Radial, Transverse, Normal") or TNW. */
    COV_REF_FRAME,
    /** Covariance matrix [1, 1] element. */
    CX_X,
    /** Covariance matrix [2, 1] element. */
    CY_X,
    /** Covariance matrix [2, 2] element. */
    CY_Y,
    /** Covariance matrix [3, 1] element. */
    CZ_X,
    /** Covariance matrix [3, 2] element. */
    CZ_Y,
    /** Covariance matrix [3, 3] element. */
    CZ_Z,
    /** Covariance matrix [4, 1] element. */
    CX_DOT_X,
    /** Covariance matrix [4, 2] element. */
    CX_DOT_Y,
    /** Covariance matrix [4, 3] element. */
    CX_DOT_Z,
    /** Covariance matrix [4, 4] element. */
    CX_DOT_X_DOT,
    /** Covariance matrix [5, 1] element. */
    CY_DOT_X,
    /** Covariance matrix [5, 2] element. */
    CY_DOT_Y,
    /** Covariance matrix [5, 3] element. */
    CY_DOT_Z,
    /** Covariance matrix [5, 4] element. */
    CY_DOT_X_DOT,
    /** Covariance matrix [5, 5] element. */
    CY_DOT_Y_DOT,
    /** Covariance matrix [6, 1] element. */
    CZ_DOT_X,
    /** Covariance matrix [6, 2] element. */
    CZ_DOT_Y,
    /** Covariance matrix [6, 3] element. */
    CZ_DOT_Z,
    /** Covariance matrix [6, 4] element. */
    CZ_DOT_X_DOT,
    /** Covariance matrix [6, 5] element. */
    CZ_DOT_Y_DOT,
    /** Covariance matrix [6, 6] element. */
    CZ_DOT_Z_DOT,
    /** Epoch of ignition. */
    MAN_EPOCH_IGNITION,
    /** Mass change during maneuver (value is &lt; 0). */
    MAN_DELTA_MASS,
    /** First component of the velocity increment. */
    MAN_DV_1,
    /** Second component of the velocity increment. */
    MAN_DV_2,
    /** Third component of the velocity increment. */
    MAN_DV_3,
    /** This keyword must appear before the first line of the covariance matrix data. */
    COVARIANCE_START,
    /** The interpolation method to be used. */
    INTERPOLATION,
    /** This keyword must appear after the last line of the covariance matrix data. */
    COVARIANCE_STOP,

    // -------------------------------------------
    // Attitude Data Messages (ADM) specific keywords
    // -------------------------------------------

    /** CCSDS APM format version. */
    CCSDS_APM_VERS,
    /** CCSDS AEM format version. */
    CCSDS_AEM_VERS,
    /** Name of the reference frame specifying one frame of the transformation. */
    Q_FRAME_A,
    /** Name of the reference frame specifying the second portion of the transformation. */
    Q_FRAME_B,
    /** Rotation direction of the attitude quaternion. */
    Q_DIR,
    /** e1 * sin(φ/2)   φ = rotation angle. */
    Q1,
    /** e2 * sin(φ/2)   φ = rotation angle. */
    Q2,
    /** e3 * sin(φ/2)   φ = rotation angle. */
    Q3,
    /** cos(φ/2)   φ = rotation angle. */
    QC,
    /** Derivative of Q1. */
    Q1_DOT,
    /** Derivative of Q2. */
    Q2_DOT,
    /** Derivative of Q3. */
    Q3_DOT,
    /** Derivative of QC. */
    QC_DOT,
    /**  Name of the reference frame specifying one frame of the transformation. */
    EULER_FRAME_A,
    /** Name of the reference frame specifying the second portion of the transformation. */
    EULER_FRAME_B,
    /** Rotation direction of the attitude Euler angle. */
    EULER_DIR,
    /** Rotation order of the EULER_FRAME_A to EULER_FRAME_B or vice versa. */
    EULER_ROT_SEQ,
    /** The value of this keyword expresses the relevant keyword to use that denotes the
     *  frame of reference in which the X_RATE, Y_RATE and Z_RATE are expressed. */
    RATE_FRAME,
    /** X body rotation angle. */
    X_ANGLE,
    /** Y body rotation angle. */
    Y_ANGLE,
    /** Z body rotation angle. */
    Z_ANGLE,
    /** X body rotation rate. */
    X_RATE,
    /** Y body rotation rate. */
    Y_RATE,
    /** Z body rotation rate. */
    Z_RATE,
    /**  Name of the reference frame specifying one frame of the transformation. */
    SPIN_FRAME_A,
    /** Name of the reference frame specifying the second portion of the transformation. */
    SPIN_FRAME_B,
    /** Rotation direction of the Spin angles .*/
    SPIN_DIR,
    /** Right ascension of spin axis vector. */
    SPIN_ALPHA,
    /** Declination of the spin axis vector.*/
    SPIN_DELTA,
    /** Phase of the satellite about the spin axis. */
    SPIN_ANGLE,
    /** Angular velocity of satellite around spin axis. */
    SPIN_ANGLE_VEL,
    /** Nutation angle of spin axis. */
    NUTATION,
    /** Body nutation period of the spin axis. */
    NUTATION_PER,
    /** Inertial nutation phase. */
    NUTATION_PHASE,
    /** Coordinate system for the inertia tensor. */
    INERTIA_REF_FRAME,
    /** Moment of Inertia about the 1-axis. */
    I11,
    /** Moment of Inertia about the 2-axis. */
    I22,
    /** Moment of Inertia about the 3-axis. */
    I33,
    /** Inertia Cross Product of the 1 and 2 axes. */
    I12,
    /** Inertia Cross Product of the 1 and 3 axes. */
    I13,
    /** Inertia Cross Product of the 2 and 3 axes. */
    I23,
    /** Epoch of start of maneuver. */
    MAN_EPOCH_START,
    /** First component of the torque vector. */
    MAN_TOR_1,
    /** Second component of the torque vector. */
    MAN_TOR_2,
    /** Third component of the torque vector. */
    MAN_TOR_3,
    /** Name of the reference frame specifying one frame of the transformation. */
    REF_FRAME_A,
    /** Name of the reference frame specifying the second portion of the transformation. */
    REF_FRAME_B,
    /** Rotation direction of the attitude. */
    ATTITUDE_DIR,
    /** The format of the data lines in the message. */
    ATTITUDE_TYPE,
    /** The placement of the scalar portion of the quaternion (QC) in the attitude data. */
    QUATERNION_TYPE,
    /** Recommended interpolation method for attitude ephemeris data. */
    INTERPOLATION_METHOD,

    // ----------------------------------------------
    // Tracking Data Messages (TDM) specific keywords
    // ----------------------------------------------

    // TDM Header section
    // ------------------

    /** Header: TDM format version in the form of ‘x.y’, where ‘y’ shall be incremented for
     *  corrections and minor changes, and ‘x’ shall be incremented for major changes.
     *  <p>Obligatory: YES
     */
    CCSDS_TDM_VERS,

    // TDM meta-data section
    // ---------------------

    /** Meta-data: PARTICIPANT_n, n = {1, 2, 3, 4, 5}.<p>
     *  Participants in a tracking data sessions (spacecraft(s), ground station(s)...)
     * <p>Obligatory: YES (at least 1)
     */
    PARTICIPANT_1,
    /** Participant 2. */
    PARTICIPANT_2,
    /** Participant 3. */
    PARTICIPANT_3,
    /** Participant 4. */
    PARTICIPANT_4,
    /** Participant 5. */
    PARTICIPANT_5,
    /** Meta-data: Tracking mode associated with data section of the segment.<p>
     *  - SEQUENTIAL: Applies only for range, Doppler, angles, and LOS ionosphere calibrations.
     *                The name implies a sequential signal path between tracking participants;<p>
     *  - SINGLE_DIFF: Applies for differenced data;<p>
     *  - In other cases, such as troposphere, weather, clocks, etc., use of the MODE keyword does not apply.
     *  <p>Obligatory: NO
     */
    MODE,
    /** Meta-data: The PATH keywords shall reflect the signal path by listing the index of each PARTICIPANT
     * in order, separated by commas, with no inserted white space.<p>
     * The first entry in the PATH shall be the transmit participant.<p>
     * The non-indexed ‘PATH’ keyword shall be used if the MODE is SEQUENTIAL.<p>
     * The indexed ‘PATH_1’ and ‘PATH_2’ keywords shall be used where the MODE is SINGLE_DIFF.<p>
     * Examples:<p>
     *  - 1,2 = one-way;<p>
     *  - 2,1,2 = two-way;<p>
     *  - 3,2,1 = three-way;<p>
     *  - 1,2,3,4 = four-way;<p>
     *  - 1,2,3,2,1 = turn-around range with 1=primary station, 2=satellite, 3=secondary station.
     *  <p>Obligatory: NO
     */
    PATH,
    /** Path 1. */
    PATH_1,
    /** Path 2. */
    PATH_2,
    /** Frequency band for transmitted frequencies.
     * <p>Obligatory: NO
     */
    TRANSMIT_BAND,
    /** Meta-data: Frequency band for received frequencies.
     * <p>Obligatory: NO
     */
    RECEIVE_BAND,
    /** Meta-data: Turn-around ratio numerator. <p>
     *  Numerator of the turn-around ratio that is necessary to calculate the coherent downlink from the uplink frequency.
     *  <p>Obligatory: NO
     */
    TURNAROUND_NUMERATOR,
    /** Meta-data: Turn-around ratio denominator.
     * <p>Obligatory: NO
     */
    TURNAROUND_DENOMINATOR,
    /** Meta-data: Timetag reference.<p>
     *  Provides a reference for time tags in the tracking data.<p>
     *  It indicates whether the timetag associated with the data is the transmit time or the receive time.
     *  <p>Obligatory: NO
     */
    TIMETAG_REF,
    /** Meta-data: Integration interval.<p>
     *  Provides the Doppler count time in seconds for Doppler data or for the creation
     *  of normal points.
     *  <p>Obligatory: NO
     */
    INTEGRATION_INTERVAL,
    /** Meta-data: Integration reference.<p>
     *  Used in conjunction with timetag reference and integration interval.<p>
     *  Indicates whether the timetag represents the start, middle or end of the integration interval.
     *  <p>Obligatory: NO
     */
    INTEGRATION_REF,

    /** Meta-data: Frequency offset.<p>
     *  A frequency in Hz that must be added to every RECEIVE_FREQ to reconstruct it.
     *  <p>Obligatory: NO
     */
    FREQ_OFFSET,
    /** Meta-data: Range mode.<p>
     *  COHERENT, CONSTANT or ONE_WAY.
     *  <p>Obligatory: NO
     */
    RANGE_MODE,
    /** Meta-data: Range modulus.<p>
     *  Modulus of the range observable in the units as specified by the RANGE_UNITS keyword.
     *  <p>Obligatory: NO
     */
    RANGE_MODULUS,
    /** Meta-data: The RANGE_UNITS keyword specifies the units for the range observable.<p>
     * ‘km’ shall be used if the range is measured in kilometers.<p>
     * ‘s’ shall be used if the range is measured in seconds.<p>
     * 'RU' for "range units'
     * <p>Obligatory: NO
     */
    RANGE_UNITS,
    /** Meta-data: The ANGLE_TYPE keyword shall indicate the type of antenna geometry represented in the angle data (ANGLE_1 and ANGLE_2 keywords).<p>
     * The value shall be one of the following:<p>
     * - AZEL for azimuth, elevation (local horizontal);<p>
     * - RADEC for right ascension, declination or hour angle, declination (needs to be referenced to an inertial frame);<p>
     * - XEYN for x-east, y-north;<p>
     * - XSYE for x-south, y-east.
     * <p>Obligatory: NO
     */
    ANGLE_TYPE,
    /** Reference frame in which data are given: used in combination with ANGLE_TYPE=RADEC.
     * <p>Obligatory: NO
     */
    REFERENCE_FRAME,
    /** Meta-data: Transmit delays list (up to 5).<p>
     *  Specifies a fixed interval of time, in seconds, for the signal to travel from the transmitting
     *  electronics to the transmit point. Each item in the list corresponds to the each participants.
     *  <p>Obligatory: NO
     */
    TRANSMIT_DELAY_1,
    /** Second. */
    TRANSMIT_DELAY_2,
    /** Second. */
    TRANSMIT_DELAY_3,
    /** Second. */
    TRANSMIT_DELAY_4,
    /** Second. */
    TRANSMIT_DELAY_5,
    /** Meta-data: Receive delays list.<p>
     *  Specifies a fixed interval of time, in seconds, for the signal to travel from the tracking
     *  point to the receiving electronics. Each item in the list corresponds to the each participants.
     *  <p>Obligatory: NO
     */
    RECEIVE_DELAY_1,
    /** Second. */
    RECEIVE_DELAY_2,
    /** Second. */
    RECEIVE_DELAY_3,
    /** Second. */
    RECEIVE_DELAY_4,
    /** Second. */
    RECEIVE_DELAY_5,
    /** Meta-data: Data quality.<p>
     *  Estimate of the quality of the data: RAW, DEGRADED or VALIDATED.
     *  <p>Obligatory: NO
     */
    DATA_QUALITY,
    /** Meta-data: Correction angle 1.<p>
     *  Angle correction that has been added or should be added to the ANGLE_1 data.
     *  <p>Obligatory: NO
     */
    CORRECTION_ANGLE_1,
    /** Meta-data: Correction angle 2.<p>
     *  Angle correction that has been added or should be added to the ANGLE_2 data.
     *  <p>Obligatory: NO
     */
    CORRECTION_ANGLE_2,
    /** Meta-data: Correction Doppler.<p>
     *  Doppler correction that has been added or should be added to the DOPPLER data.
     *  <p>Obligatory: NO
     */
    CORRECTION_DOPPLER,
    /** Meta-data: Correction Range.<p>
     *  Range correction that has been added or should be added to the RANGE data.
     *  <p>Obligatory: NO
     */
    CORRECTION_RANGE,
    /** Meta-data: Correction receive.<p>
     *  Receive correction that has been added or should be added to the RECEIVE data.
     *  <p>Obligatory: NO
     */
    CORRECTION_RECEIVE,
    /** Meta-data: Correction transmit.<p>
     *  Transmit correction that has been added or should be added to the TRANSMIT data.
     *  <p>Obligatory: NO
     */
    CORRECTION_TRANSMIT,
    /** Meta-data: Correction applied ? YES/NO<p>
     *  Indicate whethers or not the values associated with the CORRECTION_* keywords have been
     *  applied to the tracking data.
     *  <p>Obligatory: NO
     */
    CORRECTIONS_APPLIED,


    // TDM Data section
    // ----------------

    // Signal related keywords.
    /** Data: Carrier power [dBW].<p>
     *  Strength of the radio signal transmitted by the spacecraft as received at the ground station or at another spacecraft.
     */
    CARRIER_POWER,
    /** Data: Doppler instantaneous [km/s].<p>
     *  Instantaneous range rate of the spacecraft.
     */
    DOPPLER_INSTANTANEOUS,
    /** Data: Doppler integrated [km/s].<p>
     *  Mean range rate of the spacecraft over the INTEGRATION_INTERVAL specified in the meta-data section.
     */
    DOPPLER_INTEGRATED,
    /** Data: Carrier power to noise spectral density ratio (Pc/No) [dBHz]. */
    PC_N0,
    /** Data: Ranging power to noise spectral density ratio (Pr/No) [dBHz]. */
    PR_N0,
    /** Data: Range value [km, s or RU].
     * @see #RANGE_UNITS
     */
    RANGE,
    /** Data: Received frequencies [Hz].<p>
     * The RECEIVE_FREQ keyword shall be used to indicate that the values represent measurements of the received frequency.<p>
     * The keyword is indexed to accommodate a scenario in which multiple downlinks are used.<p>
     * RECEIVE_FREQ_n (n = 1, 2, 3, 4, 5)
     */
    RECEIVE_FREQ_1,
    /** Received frequency 2. */
    RECEIVE_FREQ_2,
    /** Received frequency 3. */
    RECEIVE_FREQ_3,
    /** Received frequency 4. */
    RECEIVE_FREQ_4,
    /** Received frequency 5. */
    RECEIVE_FREQ_5,
    /** Data: Received frequency [Hz].<p>
     *  Case without an index; where the frequency cannot be associated with a particular participant.
     */
    RECEIVE_FREQ,
    /** Data: Transmitted frequencies [Hz].<p>
     * The TRANSMIT_FREQ keyword shall be used to indicate that the values represent measurements of a transmitted frequency, e.g., from an uplink operation.<p>
     * The TRANSMIT_FREQ keyword is indexed to accommodate scenarios in which multiple transmitters are used.<p>
     * TRANSMIT_FREQ_n (n = 1, 2, 3, 4, 5)
     */
    TRANSMIT_FREQ_1,
    /** Transmitted frequency 2. */
    TRANSMIT_FREQ_2,
    /** Transmitted frequency 3. */
    TRANSMIT_FREQ_3,
    /** Transmitted frequency 4. */
    TRANSMIT_FREQ_4,
    /** Transmitted frequency 5. */
    TRANSMIT_FREQ_5,
    /** Data: Transmitted frequencies rates [Hz/s].<p>
     * The value associated with the TRANSMIT_FREQ_RATE_n keyword is the linear rate of
     * change of the frequency TRANSMIT_FREQ_n starting at the timetag and continuing
     *  until the next TRANSMIT_FREQ_RATE_n timetag (or until the end of the data).<p>
     * TRANSMIT_FREQ_RATE_n (n = 1, 2, 3, 4, 5)
     */
    TRANSMIT_FREQ_RATE_1,
    /** Transmitted frequency rate 2. */
    TRANSMIT_FREQ_RATE_2,
    /** Transmitted frequency rate 3. */
    TRANSMIT_FREQ_RATE_3,
    /** Transmitted frequency rate 4. */
    TRANSMIT_FREQ_RATE_4,
    /** Transmitted frequency rate 5. */
    TRANSMIT_FREQ_RATE_5,

    // VLBI/Delta-DOR Related Keywords
    /** Data: DOR [s].<p>
     * the DOR keyword represents the range measured via PATH_2 minus the range measured via PATH_1.
     */
    DOR,
    /** Data: VLBI delay [s].<p>
     * The observable associated with the VLBI_DELAY keyword represents the time of signal
     * arrival via PATH_2 minus the time of signal arrival via PATH_1.
     */
    VLBI_DELAY,

    // Angle Related Keywords
    /** Data: ANGLE_1 in degrees and in [-180, +360[ [deg].<p>
     * The value assigned to the ANGLE_1 keyword represents the azimuth, right ascension, or ‘X’
     * angle of the measurement, depending on the value of the ANGLE_TYPE keyword.<p>
     * The angle measurement shall be a double precision value as follows: -180.0 &le; ANGLE_1 &lt; 360.0<p>
     * Units shall be degrees.<p>
     * See meta-data keyword ANGLE_TYPE for the definition of the angles.
     */
    ANGLE_1,
    /** Data: ANGLE_2 in degrees and in [-180, +360[ [deg].<p>
     * The value assigned to the ANGLE_2 keyword represents the elevation, declination, or ‘Y’
     * angle of the measurement, depending on the value of the ANGLE_TYPE keyword.<p>
     * The angle measurement shall be a double precision value as follows: -180.0 &le; ANGLE_2 &lt; 360.0.<p>
     * Units shall be degrees.<p>
     * See meta-data keyword ANGLE_TYPE for the definition of the angles.
     */
    ANGLE_2,

    // Time Related Keywords
    /** Data: Clock bias [s].<p>
     * The CLOCK_BIAS keyword can be used by the message recipient to adjust timetag
     * measurements by a specified amount with respect to a common reference.
     */
    CLOCK_BIAS,
    /** Data: Clock drift [s/s].<p>
     * The CLOCK_DRIFT keyword should be used to adjust timetag measurements by an amount that is a function of time with
     * respect to a common reference, normally UTC (as opposed to the CLOCK_BIAS, which is meant to be a constant adjustment).
     */
    CLOCK_DRIFT,

    // Media Related Keywords
    /** Data: STEC - Slant Total Electron Count [TECU].
     * The STEC keyword shall be used to convey the line of sight,
     * one way charged particle delay or total electron count (TEC) at the timetag associated with a
     * tracking measurement, which is calculated by integrating the electron density along the
     * propagation path (electrons/m2).
     */
    STEC,
    /** Data: TROPO DRY [m].<p>
     * Dry zenith delay through the troposphere measured at the timetag.
     */
    TROPO_DRY,
    /** Data: TROPO WET [m].<p>
     * Wet zenith delay through the troposphere measured at the timetag.
     */
    TROPO_WET,

    // Meteorological Related Keywords
    /** Data: Pressure [hPa].<p>
     * Atmospheric pressure observable as measured at the tracking participant.
     */
    PRESSURE,
    /** Data: Relative humidity [%].<p>
     * Relative humidity observable as measured at the tracking participant.
     */
    RHUMIDITY,
    /** Data: Temperature [K].<p>
     * Temperature observable as measured at the tracking participant.
     */
    TEMPERATURE,

    // Miscellaneous KEYVALUE keywords
    /** Keyword used to delineate the start of a Data block in Keyvalue files. */
    DATA_START,
    /** Keyword used to delineate the end of a Data block in Keyvalue files.. */
    DATA_STOP,

    // XML TDM start/end keywords
    /** TDM first keyword. */
    tdm,
    /** Header keyword. */
    header,
    /** Body keyword. */
    body,
    /** Segment keyword. */
    segment,
    /** Meta-data keyword. */
    metadata,
    /** Data keyword. */
    data,
    /** Observation keyword. */
    observation;
}
