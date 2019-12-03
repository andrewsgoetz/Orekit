package org.orekit.frames;

import org.orekit.frames.EOPHistoryLoader.Parser;
import org.orekit.time.TimeScale;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.IERSConventions.NutationCorrectionConverter;

/**
 * Abstract class that holds common data used by several implementations of {@link
 * Parser}.
 *
 * @author Evan Ward
 * @since 10.1
 */
abstract class AbstractEopParser implements Parser {

    /** Converter for nutation corrections. */
    private final IERSConventions.NutationCorrectionConverter converter;
    /** Configuration for ITRF versions. */
    private final ITRFVersionLoader itrfVersionLoader;
    /** UTC time scale. */
    private final TimeScale utc;

    /**
     * Simple constructor.
     *
     * @param converter         converter to use
     * @param itrfVersionLoader to use for determining the ITRF version of the EOP.
     * @param utc               time scale for parsing dates.
     */
    protected AbstractEopParser(final NutationCorrectionConverter converter,
                                final ITRFVersionLoader itrfVersionLoader,
                                final TimeScale utc) {
        this.converter = converter;
        this.itrfVersionLoader = itrfVersionLoader;
        this.utc = utc;
    }

    /**
     * Get the nutation converter.
     *
     * @return the nutation converter.
     */
    protected NutationCorrectionConverter getConverter() {
        return converter;
    }

    /**
     * Get the ITRF version loader.
     *
     * @return ITRF version loader.
     */
    protected ITRFVersionLoader getItrfVersionLoader() {
        return itrfVersionLoader;
    }

    /**
     * Get the UTC time scale.
     *
     * @return UTC time scale.
     */
    protected TimeScale getUtc() {
        return utc;
    }

}
