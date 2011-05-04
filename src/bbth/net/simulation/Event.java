package bbth.net.simulation;

/**
 * Represents a touch event by either the local or the remote user.
 */
public class Event implements Comparable<Event> {

	public static final int TAP_DOWN = 0;
	public static final int TAP_MOVE = 1;
	public static final int TAP_UP = 2;

	/**
	 * Was the action performed on a hold beat or a tap beat?
	 */
	public static final int IS_HOLD = 0x20;

	/**
	 * Was the action on the beat on the phone on which it occurred at the time
	 * the user did it?
	 */
	public static final int IS_ON_BEAT = 0x40;

	/**
	 * Did the action originate as the server?
	 */
	public static final int IS_SERVER = 0x80;

	/**
	 * The size in bytes of this event when written to and read from a byte
	 * buffer.
	 */
	public static final int SIZE_IN_BYTES = 1 + 4 + 2 + 2;

	/**
	 * One of TAP_DOWN, TAP_MOVE, or TAP_UP.
	 */
	public int type;

	/**
	 * A combination of IS_HOLD, IS_ON_BEAT, and IS_SERVER.
	 */
	public int flags;

	/**
	 * The number of fine timesteps since the beginning of the simulation.
	 */
	public int fineTime;

	/**
	 * The x coordinate of the event in game space (not screen space).
	 */
	public float x;

	/**
	 * The y coordinate of the event in game space (not screen space).
	 */
	public float y;

	@Override
	public int compareTo(Event other) {
		return fineTime - other.fineTime;
	}
}
