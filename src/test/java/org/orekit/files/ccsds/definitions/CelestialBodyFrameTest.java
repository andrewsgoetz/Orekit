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
package org.orekit.files.ccsds.definitions;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hipparchus.geometry.euclidean.threed.FieldVector3D;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.Decimal64;
import org.hipparchus.util.Decimal64Field;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.orekit.Utils;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataContext;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitMessages;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.ITRFVersion;
import org.orekit.frames.TopocentricFrame;
import org.orekit.frames.Transform;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;


public class CelestialBodyFrameTest {

    /**
     * Check mapping frames to CCSDS frames.
     */
    @Test
    public void testMap() {
        // action + verify
        // check all non-LOF frames created by OEMParser
        for (CelestialBodyFrame ccsdsFrame : CelestialBodyFrame.values()) {
            Frame frame = ccsdsFrame.getFrame(IERSConventions.IERS_2010, true, DataContext.getDefault());
            CelestialBodyFrame actual = CelestialBodyFrame.map(frame);
            if (ccsdsFrame == CelestialBodyFrame.J2000) {
                // CCSDS allows both J2000 and EME2000 names
                // Orekit chose to use EME2000 when guessing name from frame instance
                MatcherAssert.assertThat(actual, CoreMatchers.is(CelestialBodyFrame.EME2000));
            } else  if (ccsdsFrame == CelestialBodyFrame.TDR) {
                // CCSDS allows both GTOD (in ADM section A3) and
                // TDR (in ODM table 5-3 and section A2) names
                // Orekit chose to use GTOD when guessing name from frame instance
                MatcherAssert.assertThat(actual, CoreMatchers.is(CelestialBodyFrame.GTOD));
            } else {
                MatcherAssert.assertThat(actual, CoreMatchers.is(ccsdsFrame));
            }
        }

        // check common Orekit frames from FramesFactory
        MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getGCRF()),
                                 CoreMatchers.is(CelestialBodyFrame.GCRF));
        MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getEME2000()),
                                 CoreMatchers.is(CelestialBodyFrame.EME2000));
        MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getITRFEquinox(IERSConventions.IERS_2010, true)),
                                 CoreMatchers.is(CelestialBodyFrame.GRC));
        MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getICRF()),
                                 CoreMatchers.is(CelestialBodyFrame.ICRF));
        MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getITRF(IERSConventions.IERS_2010, true)),
                                 CoreMatchers.is(CelestialBodyFrame.ITRF2014));
        MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getGTOD(true)),
                                 CoreMatchers.is(CelestialBodyFrame.GTOD));
        MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getTEME()),
                                 CoreMatchers.is(CelestialBodyFrame.TEME));
        MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getTOD(true)),
                                 CoreMatchers.is(CelestialBodyFrame.TOD));

        // check that guessed name loses the IERS conventions and simpleEOP flag
        for (ITRFVersion version : ITRFVersion.values()) {
            final String name = version.getName().replaceAll("-", "");
            for (final IERSConventions conventions : IERSConventions.values()) {
                MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getITRF(version, conventions, true)).name(),
                                         CoreMatchers.is(name));
                MatcherAssert.assertThat(CelestialBodyFrame.map(FramesFactory.getITRF(version, conventions, false)).name(),
                                         CoreMatchers.is(name));
            }
        }

        // check other names in Annex A
        MatcherAssert.assertThat(
                CelestialBodyFrame.map(CelestialBodyFactory.getMars().getInertiallyOrientedFrame()),
                CoreMatchers.is(CelestialBodyFrame.MCI));
        MatcherAssert.assertThat(CelestialBodyFrame.map(CelestialBodyFactory.getSolarSystemBarycenter().
                                 getInertiallyOrientedFrame()),
                                 CoreMatchers.is(CelestialBodyFrame.ICRF));
        // check some special CCSDS frames
        ModifiedFrame frame = new ModifiedFrame(FramesFactory.getEME2000(),
                                                          CelestialBodyFrame.EME2000,
                                                          CelestialBodyFactory.getMars(), "MARS");
        MatcherAssert.assertThat(CelestialBodyFrame.map(frame), CoreMatchers.is(CelestialBodyFrame.EME2000));
        Vector3D v = frame.getTransformProvider().getTransform(AbsoluteDate.J2000_EPOCH).getTranslation();
        FieldVector3D<Decimal64> v64 = frame.getTransformProvider().getTransform(FieldAbsoluteDate.getJ2000Epoch(Decimal64Field.getInstance())).getTranslation();
        Assert.assertEquals(0.0, FieldVector3D.distance(v64, v).getReal(), 1.0e-10);

        // check unknown frame
        try {
            Frame topo = new TopocentricFrame(new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                                                                   Constants.WGS84_EARTH_FLATTENING,
                                                                   FramesFactory.getITRF(IERSConventions.IERS_2010, true)),
                                              new GeodeticPoint(1.2, 2.3, 45.6),
                            "dummy");
            CelestialBodyFrame.map(topo);
            Assert.fail("an exception should have been thrown");
        } catch (OrekitException oe) {
            Assert.assertEquals(OrekitMessages.CCSDS_INVALID_FRAME, oe.getSpecifier());
            Assert.assertEquals("dummy", oe.getParts()[0]);
        }

        // check a fake ICRF
        Frame fakeICRF = new Frame(FramesFactory.getGCRF(), Transform.IDENTITY,
                                   CelestialBodyFactory.SOLAR_SYSTEM_BARYCENTER + "/inertial");
        MatcherAssert.assertThat(CelestialBodyFrame.map(fakeICRF), CoreMatchers.is(CelestialBodyFrame.ICRF));
    }

    /**
     * Check guessing names.
     */
    @Test
    public void testGuessFrame() {

        Frame itrf89 = FramesFactory.getITRF(ITRFVersion.ITRF_1989, IERSConventions.IERS_1996, true);
        Assert.assertEquals("ITRF1989", CelestialBodyFrame.guessFrame(itrf89));

        Frame topo = new TopocentricFrame(new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                                                               Constants.WGS84_EARTH_FLATTENING,
                                                               FramesFactory.getITRF(IERSConventions.IERS_2010, true)),
                                          new GeodeticPoint(1.2, 2.3, 45.6),
                        "dummy");
        Assert.assertEquals("dummy", CelestialBodyFrame.guessFrame(topo));
    }

    @Before
    public void setUp() {
        Utils.setDataRoot("regular-data");
    }

}
