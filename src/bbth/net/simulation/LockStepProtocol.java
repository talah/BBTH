package bbth.net.simulation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import bbth.game.BBTHGame;
import bbth.net.bluetooth.Protocol;

/**
 * Reads and writes LockSteps and Events in a compressed format:
 * 
 * <pre>
 * struct Event {
 *     byte type; // highest bit for isOnBeat
 *     int time; // the number of fine timesteps from the start of the game
 *     short x; // float from 0 to BBTHGame.WIDTH is mapped as a short from 0 to 0xFFFF
 *     short y; // float from 0 to BBTHGame.HEIGHT is mapped as a short from 0 to 0xFFFF
 * }
 * </pre>
 */
public class LockStepProtocol implements Protocol {

	private BlockingQueue<LockStep> incoming = new PriorityBlockingQueue<LockStep>();
	private BlockingQueue<LockStep> outgoing = new PriorityBlockingQueue<LockStep>();

	public LockStep readLockStep() {
		return incoming.poll();
	}

	public void writeLockStep(LockStep step) {
		outgoing.add(step);
	}

	@Override
	public void readFrom(InputStream in) throws IOException, InterruptedException {
		LockStep step = new LockStep();
		int count = in.read();
		step.coarseTime = in.read();
		step.coarseTime |= in.read() << 8;
		step.coarseTime |= in.read() << 16;
		step.coarseTime |= in.read() << 24;

		for (int i = 0; i < count; i++) {
			Event event = new Event();

			// Deserialize the event
			int type = in.read();
			event.type = type & 0x3F;
			event.isOnBeat = (type & 0x80) != 0;
			event.isLocal = false;
			event.fineTime = in.read();
			event.fineTime |= in.read() << 8;
			event.fineTime |= in.read() << 16;
			event.fineTime |= in.read() << 24;
			int smallX = in.read();
			smallX |= in.read() << 8;
			int smallY = in.read();
			smallY |= in.read() << 8;
			event.x = smallX * BBTHGame.WIDTH / 0xFFFF;
			event.y = smallY * BBTHGame.HEIGHT / 0xFFFF;

			step.events.add(event);
		}

		incoming.put(step);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException, InterruptedException {
		LockStep step = outgoing.take();
		out.write(step.events.size());
		out.write(step.coarseTime);
		out.write(step.coarseTime >> 8);
		out.write(step.coarseTime >> 16);
		out.write(step.coarseTime >> 24);

		for (int i = 0, count = step.events.size(); i < count; i++) {
			Event event = step.events.get(i);

			// Serialize the event
			event.x = Math.max(0, Math.min(BBTHGame.WIDTH, event.x));
			event.y = Math.max(0, Math.min(BBTHGame.HEIGHT, event.y));
			int smallX = Math.round(event.x / BBTHGame.WIDTH * 0xFFFF);
			int smallY = Math.round(event.y / BBTHGame.HEIGHT * 0xFFFF);
			out.write(event.type | (event.isOnBeat ? 0x80 : 0));
			out.write(event.fineTime);
			out.write(event.fineTime >> 8);
			out.write(event.fineTime >> 16);
			out.write(event.fineTime >> 24);
			out.write(smallX);
			out.write(smallX >> 8);
			out.write(smallY);
			out.write(smallY >> 8);
		}
	}
}
