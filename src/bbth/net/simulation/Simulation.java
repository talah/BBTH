package bbth.net.simulation;

import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

import android.util.Log;

/**
 * A lock-step simulation is a deterministic simulation that is run in sync on
 * both phones. It has two timesteps:
 * 
 * The fine timestep is the update rate that the user sees (when game entities
 * will be updated and when the delayed user inputs will be simulated).
 * 
 * The coarse timestep is the update rate at which updates are sent over the
 * network. In a lock-step simulation, only the user input is sent over the
 * network.
 */
public abstract class Simulation {

	private PriorityBlockingQueue<LockStep> incomingRemoteSteps = new PriorityBlockingQueue<LockStep>();
	private PriorityBlockingQueue<LockStep> incomingLocalSteps = new PriorityBlockingQueue<LockStep>();
	private PriorityQueue<Event> incomingEvents = new PriorityQueue<Event>();

	/**
	 * The number of fine timesteps per coarse timestep.
	 */
	private final int finePerCoarse;

	/**
	 * The number of seconds that a fine timestep takes.
	 */
	private final float secondsPerFine;

	/**
	 * The number of fine timesteps since the start of the simulation.
	 */
	private int currentFineTimestep;

	/**
	 * The number of coarse timesteps since the start of the simulation.
	 */
	private int currentCoarseTimestep;

	/**
	 * The number of coarse timesteps that user actions will be delayed. Must be
	 * greater than or equal to actual network lag.
	 */
	private final int coarseLag;

	/**
	 * Stores the number of seconds since the last fine timestep.
	 */
	private float accumulatedSeconds;

	/**
	 * Knows how to serialize and deserialize LockSteps.
	 */
	private LockStepProtocol protocol = new LockStepProtocol();

	/**
	 * Stores all Events for this timestep.
	 */
	private LockStep currentStep = new LockStep();

	/**
	 * @param finePerCoarse
	 *            The number of fine timesteps per coarse timestep.
	 * @param secondsPerCoarse
	 *            The number of seconds that a coarse timestep takes.
	 * @param coarseLag
	 *            The number of coarse timesteps that user actions will be
	 *            delayed. Must be greater than or equal to actual network lag.
	 */
	public Simulation(int finePerCoarse, float secondsPerCoarse, int coarseLag, LockStepProtocol protocol) {
		this.finePerCoarse = finePerCoarse;
		this.coarseLag = coarseLag;
		this.secondsPerFine = secondsPerCoarse / finePerCoarse;
		this.protocol = protocol;

		// Start with initial blank timesteps up to the lag buffer (pretending
		// like we had received them from before)
		for (int i = 0; i < coarseLag; i++) {
			LockStep empty = new LockStep();
			empty.coarseTime = i;
			incomingRemoteSteps.add(empty);
			incomingLocalSteps.add(empty);
		}
	}

	public final void onUpdate(float seconds) {
		accumulatedSeconds += seconds;

		// Add all new remote LockSteps
		LockStep step;
		while ((step = protocol.readLockStep()) != null) {
			incomingRemoteSteps.add(step);
		}

		// Only do a maximum of 10 fine timesteps per update to avoid the well
		// of despair
		for (int i = 0; i < 10 && accumulatedSeconds > 0; i++) {
			boolean isCoarse = (currentFineTimestep % finePerCoarse) == 0;

			// Check if we can simulate the next coarse step yet
			if (isCoarse) {
				LockStep nextRemoteStep = incomingRemoteSteps.peek();
				LockStep nextLocalStep = incomingLocalSteps.peek();

				if (nextLocalStep == null || nextRemoteStep == null || nextLocalStep.coarseTime != currentCoarseTimestep
						|| nextRemoteStep.coarseTime != currentCoarseTimestep) {
					// We can't simulate the next coarse step without data from
					// both the local side and the remote side
					return;
				}

				nextLocalStep = incomingLocalSteps.remove();
				nextRemoteStep = incomingRemoteSteps.remove();

				for (int j = 0, n = nextLocalStep.events.size(); j < n; j++) {
					incomingEvents.add(nextLocalStep.events.get(j));
				}
				for (int j = 0, n = nextRemoteStep.events.size(); j < n; j++) {
					incomingEvents.add(nextRemoteStep.events.get(j));
				}
			}

			// Process events for this timestep
			Event event;
			while ((event = incomingEvents.peek()) != null && event.fineTime == currentFineTimestep) {
				dispatchEvent(incomingEvents.remove());
			}
			if (event != null && event.fineTime < currentFineTimestep) {
				Log.e("net", "causality violated, event time " + event.fineTime + " < " + currentFineTimestep);
				System.exit(0);
			}

			// Update the game for this timestep
			update(secondsPerFine);

			// Update loop variables, sending our events to the remote side
			// every coarse timestep
			if ((++currentFineTimestep % finePerCoarse) == 0) {
				endCurrentTimestep();
				currentCoarseTimestep++;
			}
			accumulatedSeconds -= secondsPerFine;
		}
	}

	/**
	 * Called for every event, in order of the event's timestamps.
	 */
	private final void dispatchEvent(Event event) {
		switch (event.type) {
		case Event.TAP_DOWN:
			simulateTapDown(event.x, event.y, event.isLocal, event.isOnBeat);
			break;

		case Event.TAP_MOVE:
			simulateTapMove(event.x, event.y, event.isLocal);
			break;

		case Event.TAP_UP:
			simulateTapUp(event.x, event.y, event.isLocal);
			break;
		}
	}

	protected abstract void simulateTapDown(float x, float y, boolean isLocal, boolean isOnBeat);

	protected abstract void simulateTapMove(float x, float y, boolean isLocal);

	protected abstract void simulateTapUp(float x, float y, boolean isLocal);

	/**
	 * Called every fine timestep with the number of seconds since the last fine
	 * timestep.
	 */
	protected abstract void update(float seconds);

	/**
	 * Called every coarse timestep.
	 */
	private final void endCurrentTimestep() {
		// Set the timestep to go off in the future
		currentStep.coarseTime = currentCoarseTimestep + coarseLag;

		// Add the timestep to the local and remote queues
		incomingLocalSteps.add(currentStep);
		protocol.writeLockStep(currentStep);

		// Start off with an empty next timestep
		currentStep = new LockStep();
	}

	/**
	 * Helper function for recordTap*() functions.
	 */
	private final Event makeEvent(float x, float y, int type, boolean isOnBeat) {
		Event event = new Event();
		event.type = type;
		event.isLocal = true;
		event.isOnBeat = isOnBeat;
		event.fineTime = currentFineTimestep + coarseLag * finePerCoarse;
		event.x = x;
		event.y = y;
		return event;
	}

	public final void recordTapDown(float x, float y, boolean isOnBeat) {
		currentStep.events.add(makeEvent(x, y, Event.TAP_DOWN, isOnBeat));
	}

	public final void recordTapMove(float x, float y) {
		currentStep.events.add(makeEvent(x, y, Event.TAP_MOVE, false));
	}

	public final void recordTapUp(float x, float y) {
		currentStep.events.add(makeEvent(x, y, Event.TAP_UP, false));
	}
}
