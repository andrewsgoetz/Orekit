package org.orekit.utils;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.geometry.euclidean.threed.FieldVector3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitMessages;
import org.orekit.frames.Frame;
import org.orekit.frames.Transform;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeStamped;

public class AbsolutePVCoordinates extends TimeStampedPVCoordinates implements TimeStamped, PVCoordinatesProvider {
	
	/** Frame in which are defined the coordinates. */
	private final Frame frame;
	
	/** Build from position, velocity, acceleration
	 * @param frame the frame in which the coordinates are defined
     * @param date coordinates date
     * @param position the position vector (m)
     * @param velocity the velocity vector (m/s)
     * @param acceleration the acceleration vector (m/s²)
     */
    public AbsolutePVCoordinates(final Frame frame, final AbsoluteDate date,
                                    final Vector3D position, final Vector3D velocity, final Vector3D acceleration) {
        super(date, position, velocity, acceleration);
        this.frame = frame;
    }
    
    /** Build from position and velocity. Acceleration is set to zero.
     * @param frame the frame in which the coordinates are defined
     * @param date coordinates date
     * @param position the position vector (m)
     * @param velocity the velocity vector (m/s)
     */
    public AbsolutePVCoordinates( final Frame frame, final AbsoluteDate date,
                                    final Vector3D position,
                                    final Vector3D velocity) {
        this(frame, date, position, velocity, Vector3D.ZERO);
    }
    
    /** Build from frame, date and PVA coordinates.
     * @param frame the frame in which the coordinates are defined
     * @param date date of the coordinates
     * @param PVCoordinates
     */
    public AbsolutePVCoordinates(final Frame frame, final AbsoluteDate date, final PVCoordinates pva) {
    	super(date, pva);
        this.frame = frame;
    }
    
    /** Build from frame and TimeStampedPVCoordinates.
     * @param pva the frame in which the coordinates are defined
     * @param pva TimeStampedPVCoordinates
     */
    public AbsolutePVCoordinates(final Frame frame, final TimeStampedPVCoordinates pva) {
    	super(pva.getDate(), pva);
        this.frame = frame;
    }
    
    /** Multiplicative constructor
     * <p>Build a AbsolutePVCoordinates from another one and a scale factor.</p>
     * <p>The TimeStampedPVCoordinates built will be a * AbsPva</p>
     * @param date date of the built coordinates
     * @param a scale factor
     * @param AbsPva base (unscaled) AbsolutePVCoordinates
     */
    public AbsolutePVCoordinates(final AbsoluteDate date,
                                    final double a, final AbsolutePVCoordinates AbsPva) {
        super(date, a, AbsPva);
        this.frame = AbsPva.frame;
    }
    
    /** Subtractive constructor
     * <p>Build a relative AbsolutePVCoordinates from a start and an end position.</p>
     * <p>The AbsolutePVCoordinates built will be end - start.</p>
     * <p>In case start and end use two different pseudo-inertial frames,
     * the new AbsolutePVCoordinates arbitrarily be defined in the start frame. </p>
     * @param date date of the built coordinates
     * @param start Starting AbsolutePVCoordinates
     * @param end ending AbsolutePVCoordinates
     */
    public AbsolutePVCoordinates(final AbsoluteDate date,
                                    final AbsolutePVCoordinates start, final AbsolutePVCoordinates end) {
        super(date, start, end);
        ensureIdenticalFrames(start, end);
        this.frame = start.frame;
    }
    
    /** Linear constructor
     * <p>Build a AbsolutePVCoordinates from two other ones and corresponding scale factors.</p>
     * <p>The AbsolutePVCoordinates built will be a1 * u1 + a2 * u2</p>
     * <p>In case the AbsolutePVCoordinates use different pseudo-inertial frames,
     * the new AbsolutePVCoordinates arbitrarily be defined in the first frame. </p>
     * @param date date of the built coordinates
     * @param a1 first scale factor
     * @param absPv1 first base (unscaled) AbsolutePVCoordinates
     * @param a2 second scale factor
     * @param absPv2 second base (unscaled) AbsolutePVCoordinates
     */
    public AbsolutePVCoordinates(final AbsoluteDate date,
                                    final double a1, final AbsolutePVCoordinates absPv1,
                                    final double a2, final AbsolutePVCoordinates absPv2) {
        super(date, a1, absPv1.getPVCoordinates(), a2, absPv2.getPVCoordinates());
        ensureIdenticalFrames(absPv1, absPv2);
        this.frame = absPv1.getFrame();
    }

    /** Linear constructor
     * <p>Build a AbsolutePVCoordinates from three other ones and corresponding scale factors.</p>
     * <p>The AbsolutePVCoordinates built will be a1 * u1 + a2 * u2 + a3 * u3</p>
     * <p>In case the AbsolutePVCoordinates use different pseudo-inertial frames,
     * the new AbsolutePVCoordinates arbitrarily be defined in the first frame. </p>
     * @param date date of the built coordinates
     * @param a1 first scale factor
     * @param absPv1 first base (unscaled) AbsolutePVCoordinates
     * @param a2 second scale factor
     * @param absPv2 second base (unscaled) AbsolutePVCoordinates
     * @param a3 third scale factor
     * @param absPv3 third base (unscaled) AbsolutePVCoordinates
     */
    public AbsolutePVCoordinates(final AbsoluteDate date,
                                    final double a1, final AbsolutePVCoordinates absPv1,
                                    final double a2, final AbsolutePVCoordinates absPv2,
                                    final double a3, final AbsolutePVCoordinates absPv3) {
        super(date, a1, absPv1.getPVCoordinates(), a2, absPv2.getPVCoordinates(),
        		a3, absPv3.getPVCoordinates());
        ensureIdenticalFrames(absPv1, absPv2);
        ensureIdenticalFrames(absPv1, absPv3);
        this.frame = absPv1.getFrame();
    }

    /** Linear constructor
     * <p>Build a AbsolutePVCoordinates from four other ones and corresponding scale factors.</p>
     * <p>The AbsolutePVCoordinates built will be a1 * u1 + a2 * u2 + a3 * u3 + a4 * u4</p>
     * <p>In case the AbsolutePVCoordinates use different pseudo-inertial frames,
     * the new AbsolutePVCoordinates arbitrarily be defined in the first frame. </p>
     * @param date date of the built coordinates
     * @param a1 first scale factor
     * @param absPv1 first base (unscaled) AbsolutePVCoordinates
     * @param a2 second scale factor
     * @param absPv2 second base (unscaled) AbsolutePVCoordinates
     * @param a3 third scale factor
     * @param absPv3 third base (unscaled) AbsolutePVCoordinates
     * @param a4 fourth scale factor
     * @param absPv4 fourth base (unscaled) AbsolutePVCoordinates
     */
    public AbsolutePVCoordinates(final AbsoluteDate date,
                                    final double a1, final AbsolutePVCoordinates absPv1,
                                    final double a2, final AbsolutePVCoordinates absPv2,
                                    final double a3, final AbsolutePVCoordinates absPv3,
                                    final double a4, final AbsolutePVCoordinates absPv4) {
        super(date, a1, absPv1.getPVCoordinates(), a2, absPv2.getPVCoordinates(),
        		a3, absPv3.getPVCoordinates(), a4, absPv4.getPVCoordinates());
        ensureIdenticalFrames(absPv1, absPv2);
        ensureIdenticalFrames(absPv1, absPv3);
        ensureIdenticalFrames(absPv1, absPv4);
        this.frame = absPv1.getFrame();
    }
    
    /** Builds a AbsolutePVCoordinates triplet from  a {@link FieldVector3D}&lt;{@link DerivativeStructure}&gt;.
     * <p>
     * The vector components must have time as their only derivation parameter and
     * have consistent derivation orders.
     * </p>
     * @param frame the frame in which the parameters are defined
     * @param date date of the built coordinates
     * @param p vector with time-derivatives embedded within the coordinates
     */
    public AbsolutePVCoordinates(final Frame frame, final AbsoluteDate date,
    		final FieldVector3D<DerivativeStructure> p) {
    	super(date, p);
    	this.frame = frame;
    }
    
    /** TODO: necessary?
     * Ensure the defining frame is a pseudo-inertial frame.
     * @param frame frame to check
     * @exception IllegalArgumentException if frame is not a {@link
     * Frame#isPseudoInertial pseudo-inertial frame}
     */
    protected static void ensurePseudoInertialFrame(final Frame frame)
    		throws IllegalArgumentException {
    	if (!frame.isPseudoInertial()) {
    		throw OrekitException.createIllegalArgumentException(
    				OrekitMessages.NON_PSEUDO_INERTIAL_FRAME_NOT_SUITABLE_FOR_DEFINING_ORBITS,
    				frame.getName());
    	}
    }
    
    /** Ensure that two frames are identical. Two pseudo-inertial frames are 
     * considered identical.
     * @param frame1 to check
     * @param frame2 to check
     * @throws IllegalArgumentException if frames are different
     */
    protected static void ensureIdenticalFrames(final Frame frame1, final Frame frame2)
    		throws IllegalArgumentException {
    	if (!frame1.isPseudoInertial() || !frame2.isPseudoInertial())
    		if (!frame1.equals(frame2)) {
    			throw OrekitException.createIllegalArgumentException(
    					OrekitMessages.INCOMPATIBLE_FRAMES, frame1.getName(), frame2.getName());
    		}
    }
    
    /** Ensure that the frames from two AbsolutePVCoordinates are identical.
     * Two pseudo-inertial frames are considered identical.
     * @param absPv1 first AbsolutePVCoordinates 
     * @param absPv2 first AbsolutePVCoordinates
     * @throws IllegalArgumentException if frames are different
     */
    protected static void ensureIdenticalFrames(final AbsolutePVCoordinates absPv1, final AbsolutePVCoordinates absPv2)
    		throws IllegalArgumentException {
    	ensureIdenticalFrames(absPv1.getFrame(), absPv2.getFrame());
    }
    
    /** TODO: ensurePseudoInertialFrame() necessary?
     * Get a time-shifted state.
     * <p>
     * The state can be slightly shifted to close dates. This shift is based on
     * a simple Taylor expansion. It is <em>not</em> intended as a replacement for
     * proper orbit propagation (it is not even Keplerian!) but should be sufficient
     * for either small time shifts or coarse accuracy.
     * </p>
     * @param dt time shift in seconds
     * @return a new state, shifted with respect to the instance (which is immutable)
     */
    public AbsolutePVCoordinates shiftedBy(final double dt) {
//    	ensurePseudoInertialFrame(frame);
        final TimeStampedPVCoordinates spv = super.shiftedBy(dt);
        return new AbsolutePVCoordinates(frame, spv);
    }
    
    /** TODO: ensurePseudoInertialFrame() necessary?
     * Create a local provider using simply Taylor expansion through {@link #shiftedBy(double)}.
     * <p>
     * The time evolution is based on a simple Taylor expansion. It is <em>not</em> intended as a
     * replacement for proper orbit propagation (it is not even Keplerian!) but should be sufficient
     * for either small time shifts or coarse accuracy.
     * </p>
     * @param instanceFrame frame in which the instance is defined
     * @return provider based on Taylor expansion, for small time shifts around instance date
     */
    public PVCoordinatesProvider toTaylorProvider() {
        return new PVCoordinatesProvider() {
            /** {@inheritDoc} */
            public TimeStampedPVCoordinates getPVCoordinates(final AbsoluteDate d,  final Frame f)
                throws OrekitException {
                final TimeStampedPVCoordinates shifted   = shiftedBy(d.durationFrom(getDate()));
                final Transform                transform = frame.getTransformTo(f, d);
                return transform.transformPVCoordinates(shifted);
            }
        };
    }

    // TODO: interpolate?
    
    // TODO: toString?
    
    // TODO: writeReplace?
    
    /** Get the frame in which the parameters are defined.
     * @return frame in which the parameters are defined
     */
    public Frame getFrame() {
        return frame;
    }
    
    /** Get the TimeStampedPVCoordinates.
     * @return TimeStampedPVCoordinates
     */
    public TimeStampedPVCoordinates getPVCoordinates() {
 	   return new TimeStampedPVCoordinates(getDate(), getPosition(), getVelocity(), getAcceleration());
    }
    
    /** Get the TimeStampedPVCoordinates in a specified frame.
     * @param outputFrame frame in which the position/velocity coordinates shall be computed
     * @return TimeStampedPVCoordinates
     * @exception OrekitException if transformation between frames cannot be computed
     * @see #getPVCoordinates()
     */
    public TimeStampedPVCoordinates getPVCoordinates(final Frame outputFrame) 
    		throws OrekitException {
    	// If output frame requested is the same as definition frame,
        // PV coordinates are returned directly
        if (outputFrame == frame) {
            return getPVCoordinates();
        }

        // Else, PV coordinates are transformed to output frame
        final Transform t = frame.getTransformTo(outputFrame, getDate());
        return t.transformPVCoordinates(getPVCoordinates());
    }

    /** {@inheritDoc} */
	@Override
	public TimeStampedPVCoordinates getPVCoordinates(AbsoluteDate otherDate, Frame otherFrame)
			throws OrekitException {
		return shiftedBy(otherDate.durationFrom(getDate())).getPVCoordinates(otherFrame);
	}
	
	// TODO: parameters and methods related to Jacobian?


}
