package bbth.net;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import bbth.game.BBTHGame;
import bbth.net.simulation.LockStepProtocol;
import bbth.net.simulation.SimulationBase;
import bbth.particles.ParticleSystem;

public class Simulation extends SimulationBase {

	private ParticleSystem particles = new ParticleSystem(1000);
	private int timestep;
	private Random random = new Random(0);

	public Simulation(LockStepProtocol protocol) {
		super(6, 0.1f, 2, protocol);
	}

	public int getTimestep() {
		return timestep;
	}

	@Override
	protected final void simulateTapDown(float x, float y, boolean isLocal, boolean isOnBeat) {
		particles.createParticle().position(x, y).radius(10).color(Color.RED).shrink(0.5f);
	}

	@Override
	protected final void simulateTapMove(float x, float y, boolean isLocal) {
		particles.createParticle().position(x, y).radius(5).color(Color.RED).shrink(0.5f);
	}

	@Override
	protected final void simulateTapUp(float x, float y, boolean isLocal) {
	}

	public void draw(Canvas canvas, Paint paint) {
		particles.draw(canvas, paint);
	}

	@Override
	protected void update(float seconds) {
		if ((timestep & 31) == 0) {
			float x = random.nextFloat() * BBTHGame.WIDTH;
			float y = random.nextFloat() * BBTHGame.HEIGHT;
			particles.createParticle().position(x, y).radius(10).color(Color.DKGRAY).shrink(0.5f);
		}

		particles.tick(seconds);
		timestep++;
	}
}
