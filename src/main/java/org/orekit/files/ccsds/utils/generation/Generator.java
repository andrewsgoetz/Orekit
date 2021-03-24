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
package org.orekit.files.ccsds.utils.generation;

import java.io.IOException;

import org.orekit.files.ccsds.definitions.TimeConverter;
import org.orekit.files.ccsds.section.CommentsContainer;
import org.orekit.files.ccsds.utils.FileFormat;
import org.orekit.time.AbsoluteDate;

/** Generation interface for CCSDS messages.
 * @author Luc Maisonobe
 * @since 11.0
 */
public interface Generator extends AutoCloseable {

    /** Get the generated file format.
     * @return generated file format
     */
    FileFormat getFormat();

    /** Start CCSDS message.
     * @param messageTypeKey key for message type
     * @param version format version
     * @throws IOException if an I/O error occurs.
     */
    void startMessage(String messageTypeKey, double version) throws IOException;

    /** Write comment lines.
     * @param comments comments to write
     * @throws IOException if an I/O error occurs.
     */
    void writeComments(CommentsContainer comments) throws IOException;

    /** Write a single key/value entry.
     * @param key   the keyword to write
     * @param value the value to write
     * @param mandatory if true, null values triggers exception, otherwise they are silently ignored
     * @throws IOException if an I/O error occurs.
     */
    void writeEntry(String key, String value, boolean mandatory) throws IOException;

    /** Write a single key/value entry.
     * @param key   the keyword to write
     * @param value the value to write
     * @param mandatory if true, null values triggers exception, otherwise they are silently ignored
     * @throws IOException if an I/O error occurs.
     */
    void writeEntry(String key, Enum<?> value, boolean mandatory) throws IOException;

    /** Write a single key/value entry.
     * @param key   the keyword to write
     * @param converter converter to use for dates
     * @param date the date to write
     * @param mandatory if true, null values triggers exception, otherwise they are silently ignored
     * @throws IOException if an I/O error occurs.
     */
    void writeEntry(String key, TimeConverter converter, AbsoluteDate date, boolean mandatory) throws IOException;

    /** Write a single key/value entry.
     * @param key   the keyword to write
     * @param value the value to write
     * @param mandatory if true, null values triggers exception, otherwise they are silently ignored
     * @throws IOException if an I/O error occurs.
     */
    void writeEntry(String key, int value, boolean mandatory) throws IOException;

    /** Write a single key/value entry.
     * @param key   the keyword to write
     * @param value the value to write
     * @param mandatory if true, null values triggers exception, otherwise they are silently ignored
     * @throws IOException if an I/O error occurs.
     */
    void writeEntry(String key, double value, boolean mandatory) throws IOException;

    /** Write a single key/value entry.
     * @param key   the keyword to write
     * @param value the value to write
     * @param mandatory if true, null values triggers exception, otherwise they are silently ignored
     * @throws IOException if an I/O error occurs.
     */
    void writeEntry(String key, Double value, boolean mandatory) throws IOException;

    /** Finish current line.
     * @throws IOException if an I/O error occurs.
     */
    void newLine() throws IOException;

    /** Write raw data.
     * @param data raw data to write
     * @throws IOException if an I/O error occurs.
     */
    void writeRawData(char data) throws IOException;

    /** Write raw data.
     * @param data raw data to write
     * @throws IOException if an I/O error occurs.
     */
    void writeRawData(CharSequence data) throws IOException;

    /** Enter into a new section.
     * @param name section name
     * @throws IOException if an I/O error occurs.
     */
    void enterSection(String name) throws IOException;

    /** Exit last section.
     * @return section name
     * @throws IOException if an I/O error occurs.
     */
    String exitSection() throws IOException;

    /** Close the generator.
     * @throws IOException if an I/O error occurs.
     */
    void close() throws IOException;

    /** Convert a date to string value with high precision.
     * @param converter converter for dates
     * @param date date to write
     * @return date as a string
     */
    String dateToString(TimeConverter converter, AbsoluteDate date);

    /** Convert a date to string value with high precision.
     * @param year year
     * @param month month
     * @param day day
     * @param hour hour
     * @param minute minute
     * @param seconds seconds
     * @return date as a string
     */
    String dateToString(int year, int month, int day, int hour, int minute, double seconds);

    /** Convert a double to string value with high precision.
     * <p>
     * We don't want to loose internal accuracy when writing doubles
     * but we also don't want to have ugly representations like STEP = 1.25000000000000000
     * so we try a few simple formats first and fall back to scientific notation
     * if it doesn't work.
     * </p>
     * @param value value to format
     * @return formatted value, with all original value accuracy preserved, or null
     * if value is null or {@code Double.NaN}
     */
    String doubleToString(double value);

}