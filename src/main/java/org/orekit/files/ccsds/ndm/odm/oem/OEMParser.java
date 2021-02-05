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
package org.orekit.files.ccsds.ndm.odm.oem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.linear.MatrixUtils;
import org.orekit.data.DataContext;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitMessages;
import org.orekit.files.ccsds.ndm.odm.OCommonParser;
import org.orekit.files.ccsds.ndm.odm.ODMHeader;
import org.orekit.files.ccsds.ndm.odm.ODMMetadataKey;
import org.orekit.files.ccsds.section.Header;
import org.orekit.files.ccsds.section.HeaderProcessingState;
import org.orekit.files.ccsds.section.KVNStructureProcessingState;
import org.orekit.files.ccsds.section.MetadataKey;
import org.orekit.files.ccsds.section.XMLStructureProcessingState;
import org.orekit.files.ccsds.utils.ParsingContext;
import org.orekit.files.ccsds.utils.lexical.FileFormat;
import org.orekit.files.ccsds.utils.lexical.ParseToken;
import org.orekit.files.ccsds.utils.lexical.TokenType;
import org.orekit.files.ccsds.utils.state.ProcessingState;
import org.orekit.files.general.EphemerisFileParser;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.TimeStampedPVCoordinates;

/**
 * A parser for the CCSDS OEM (Orbit Ephemeris Message).
 * @author sports
 * @since 6.1
 */
public class OEMParser extends OCommonParser<OEMFile, OEMParser> implements EphemerisFileParser {

    /** Root element for XML files. */
    private static final String ROOT = "oem";

    /** Pattern for splitting strings at blanks. */
    private static final Pattern SPLIT_AT_BLANKS = Pattern.compile("\\s+");

    /** File header. */
    private ODMHeader header;

    /** File segments. */
    private List<OEMSegment> segments;

    /** Metadata for current observation block. */
    private OEMMetadata metadata;

    /** Parsing context valid for current metadata. */
    private ParsingContext context;

    /** Current Ephemerides block being parsed. */
    private OEMData currentBlock;

    /** Current covariance matrix being parsed. */
    private CovarianceMatrix currentCovariance;

    /** Default interpolation degree. */
    private int defaultInterpolationDegree;

    /** Processor for global message structure. */
    private ProcessingState structureProcessor;

    /**
     * Complete constructor.
     * @param conventions IERS Conventions
     * @param simpleEOP if true, tidal effects are ignored when interpolating EOP
     * @param dataContext used to retrieve frames, time scales, etc.
     * @param missionReferenceDate reference date for Mission Elapsed Time or Mission Relative Time time systems
     * (may be null if time system is absolute)
     * @param mu gravitational coefficient
     * @param defaultInterpolationDegree default interpolation degree
     */
    public OEMParser(final IERSConventions conventions, final boolean simpleEOP,
                     final DataContext dataContext,
                     final AbsoluteDate missionReferenceDate, final double mu,
                     final int defaultInterpolationDegree) {
        super(OEMFile.FORMAT_VERSION_KEY, conventions, simpleEOP, dataContext,
              missionReferenceDate, mu);
        this.defaultInterpolationDegree  = defaultInterpolationDegree;
    }

    /** {@inheritDoc} */
    @Override
    public Header getHeader() {
        return header;
    }

    /** {@inheritDoc} */
    @Override
    public void reset(final FileFormat fileFormat) {
        header   = new ODMHeader();
        segments = new ArrayList<>();
        metadata = null;
        context  = null;
        if (getFileFormat() == FileFormat.XML) {
            structureProcessor = new XMLStructureProcessingState(ROOT, this);
            reset(fileFormat, structureProcessor);
        } else {
            structureProcessor = new KVNStructureProcessingState(this);
            reset(fileFormat, new HeaderProcessingState(getDataContext(), this));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void prepareHeader() {
        setFallback(new HeaderProcessingState(getDataContext(), this));
    }

    /** {@inheritDoc} */
    @Override
    public void inHeader() {
        setFallback(structureProcessor);
    }

    /** {@inheritDoc} */
    @Override
    public void finalizeHeader() {
        header.checkMandatoryEntries();
    }

    /** {@inheritDoc} */
    @Override
    public void prepareMetadata() {
        metadata  = new OEMMetadata(defaultInterpolationDegree);
        context   = new ParsingContext(this::getConventions,
                                       this::isSimpleEOP,
                                       this::getDataContext,
                                       this::getMissionReferenceDate,
                                       metadata::getTimeSystem);
        setFallback(this::processMetadataToken);
    }

    /** {@inheritDoc} */
    @Override
    public void inMetadata() {
        setFallback(getFileFormat() == FileFormat.XML ? structureProcessor : this::processDataToken);
    }

    /** {@inheritDoc} */
    @Override
    public void finalizeMetadata() {
        metadata.checkMandatoryEntries();
    }

    /** {@inheritDoc} */
    @Override
    public void prepareData() {
        currentBlock = new OEMData();
        setFallback(getFileFormat() == FileFormat.XML ? structureProcessor : this::processMetadataToken);
    }

    /** {@inheritDoc} */
    @Override
    public void inData() {
        setFallback(getFileFormat() == FileFormat.XML ? structureProcessor : this::processCovarianceToken);
    }

    /** {@inheritDoc} */
    @Override
    public void finalizeData() {
        if (metadata != null) {
            currentBlock.checkMandatoryEntries();
            segments.add(new OEMSegment(metadata, currentBlock,
                                        getConventions(), getDataContext(), getSelectedMu()));
        }
        metadata          = null;
        currentBlock      = null;
        currentCovariance = null;
        context           = null;
    }

    /** {@inheritDoc} */
    @Override
    public OEMFile build() {
        final OEMFile file = new OEMFile(header, segments, getConventions(), getDataContext());
        file.checkTimeSystems();
        return file;
    }

    /** Process one metadata token.
     * @param token token to process
     * @return true if token was processed, false otherwise
     */
    private boolean processMetadataToken(final ParseToken token) {
        inMetadata();
        try {
            return token.getName() != null &&
                   MetadataKey.valueOf(token.getName()).process(token, context, metadata);
        } catch (IllegalArgumentException iaeM) {
            try {
                return ODMMetadataKey.valueOf(token.getName()).process(token, context, metadata);
            } catch (IllegalArgumentException iaeD) {
                try {
                    return OEMMetadataKey.valueOf(token.getName()).process(token, context, metadata);
                } catch (IllegalArgumentException iaeE) {
                    // token has not been recognized
                    return false;
                }
            }
        }
    }

    /** Process one data token.
     * @param token token to process
     * @return true if token was processed, false otherwise
     */
    private boolean processDataToken(final ParseToken token) {
        inData();
        if ("COMMENT".equals(token.getName())) {
            return token.getType() == TokenType.ENTRY ? currentBlock.addComment(token.getContent()) : true;
        } else if (token.getType() == TokenType.RAW_LINE) {
            try {
                final String[] fields = SPLIT_AT_BLANKS.split(token.getContent());
                if (fields.length != 7 || fields.length != 10) {
                    throw new OrekitException(OrekitMessages.UNABLE_TO_PARSE_LINE_IN_FILE,
                                              token.getLineNumber(), token.getFileName(), token.getContent());
                }
                final boolean hasAcceleration = fields.length == 10;
                final AbsoluteDate epoch = context.getTimeScale().parseDate(fields[0],
                                                                            context.getConventions(),
                                                                            context.getMissionReferenceDate(),
                                                                            context.getDataContext().getTimeScales());
                final Vector3D position = new Vector3D(Double.parseDouble(fields[1]) * 1000,
                                                       Double.parseDouble(fields[2]) * 1000,
                                                       Double.parseDouble(fields[3]) * 1000);
                final Vector3D velocity = new Vector3D(Double.parseDouble(fields[4]) * 1000,
                                                       Double.parseDouble(fields[5]) * 1000,
                                                       Double.parseDouble(fields[6]) * 1000);
                final Vector3D acceleration = hasAcceleration ?
                                              Vector3D.ZERO :
                                              new Vector3D(Double.parseDouble(fields[7]) * 1000,
                                                           Double.parseDouble(fields[8]) * 1000,
                                                           Double.parseDouble(fields[9]) * 1000);
                return currentBlock.addData(new TimeStampedPVCoordinates(epoch, position, velocity, acceleration),
                                            hasAcceleration);
            } catch (NumberFormatException nfe) {
                throw new OrekitException(nfe, OrekitMessages.UNABLE_TO_PARSE_LINE_IN_FILE,
                                          token.getLineNumber(), token.getFileName(), token.getContent());
            }
        } else {
            // not a raw line, it is most probably the end of the data section
            return false;
        }
    }

    /** Process one covariance token.
     * @param token token to process
     * @return true if token was processed, false otherwise
     */
    private boolean processCovarianceToken(final ParseToken token) {
        setFallback(getFileFormat() == FileFormat.XML ? structureProcessor : this::processMetadataToken);
        if ("COVARIANCE".equals(token.getName())) {
            if (token.getType() == TokenType.ENTRY) {
                currentCovariance = new CovarianceMatrix(getMissionReferenceDate(), null, null, null);
            }
        } else if ("COVARIANCE_STOP".equals(token.getName())) {
            return token.getType() == TokenType.ENTRY ? currentBlock.addComment(token.getContent()) : true;
        } else if (token.getType() == TokenType.RAW_LINE) {
            try {
                final String[] fields = SPLIT_AT_BLANKS.split(token.getContent());
                if (fields.length != 7 || fields.length != 10) {
                    throw new OrekitException(OrekitMessages.UNABLE_TO_PARSE_LINE_IN_FILE,
                                              token.getLineNumber(), token.getFileName(), token.getContent());
                }
                final boolean hasAcceleration = fields.length == 10;
                final AbsoluteDate epoch = context.getTimeScale().parseDate(fields[0],
                                                                            context.getConventions(),
                                                                            context.getMissionReferenceDate(),
                                                                            context.getDataContext().getTimeScales());
                final Vector3D position = new Vector3D(Double.parseDouble(fields[1]) * 1000,
                                                       Double.parseDouble(fields[2]) * 1000,
                                                       Double.parseDouble(fields[3]) * 1000);
                final Vector3D velocity = new Vector3D(Double.parseDouble(fields[4]) * 1000,
                                                       Double.parseDouble(fields[5]) * 1000,
                                                       Double.parseDouble(fields[6]) * 1000);
                final Vector3D acceleration = hasAcceleration ?
                                              Vector3D.ZERO :
                                              new Vector3D(Double.parseDouble(fields[7]) * 1000,
                                                           Double.parseDouble(fields[8]) * 1000,
                                                           Double.parseDouble(fields[9]) * 1000);
                return currentBlock.addData(new TimeStampedPVCoordinates(epoch, position, velocity, acceleration),
                                            hasAcceleration);
            } catch (NumberFormatException nfe) {
                throw new OrekitException(nfe, OrekitMessages.UNABLE_TO_PARSE_LINE_IN_FILE,
                                          token.getLineNumber(), token.getFileName(), token.getContent());
            }
        } else {
            // not a raw line, it is most probably the end of the data section
            return false;
        }
    }

}
