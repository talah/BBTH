/*
 * An AI for the opposing player in a single-player match.
 */

package bbth.game.ai;

import bbth.engine.sound.Beat.BeatType;
import bbth.engine.util.MathUtils;
import bbth.game.*;

public class PlayerAI {
	private Player m_player;
	private Player m_enemy;
	private BeatTrack m_beats;
	
	private static final float DEBUG_SPAWN_TIMER = 3.f;
	private float elapsedTime = 0;
	
	private float m_difficulty = 1.0f;
	private boolean m_spawned_this_beat = false;
	private boolean m_walled_this_beat;
	private BBTHSimulation m_simulation;
	
	public PlayerAI(BBTHSimulation simulation, Player player, Player enemy, BeatTrack beatTrack, float difficulty) {
		m_simulation = simulation;
		m_player = player;
		m_enemy = enemy;
		m_beats = beatTrack;
		m_difficulty = difficulty;
	}
	
	public void update(float seconds) {
		if (m_beats.getTouchZoneBeat() == BeatType.TAP && !m_spawned_this_beat) {
			if (MathUtils.randInRange(0, 100) < m_difficulty * 100.0f) {
				m_player.spawnUnit(BBTHSimulation.randInRange(0 + BeatTrack.BEAT_TRACK_WIDTH, BBTHSimulation.GAME_WIDTH),
						BBTHSimulation.GAME_HEIGHT - 50);
				m_player.setCombo(m_player.getCombo()+1);
			} else {
				m_player.setCombo(0);
			}
			m_spawned_this_beat  = true;
			m_walled_this_beat = false;
		} else if (m_beats.getTouchZoneBeat() == BeatType.HOLD && !m_walled_this_beat) {
			m_spawned_this_beat = false;
			m_walled_this_beat = true;
			if (MathUtils.randInRange(0, 100) < m_difficulty * 100.0f) {
				m_player.startWall(BBTHSimulation.randInRange(0 + BeatTrack.BEAT_TRACK_WIDTH, BBTHSimulation.GAME_WIDTH/3.0f),
						BBTHSimulation.randInRange(BBTHSimulation.GAME_HEIGHT - 50, BBTHSimulation.GAME_HEIGHT - 250));
				m_player.updateWall(BBTHSimulation.randInRange(0 + BeatTrack.BEAT_TRACK_WIDTH + BBTHSimulation.GAME_WIDTH * .66f, BBTHSimulation.GAME_WIDTH),
						BBTHSimulation.randInRange(BBTHSimulation.GAME_HEIGHT - 50, BBTHSimulation.GAME_HEIGHT - 250));
				m_simulation.generateWall(m_player);
			}
		} else if (m_beats.getTouchZoneBeat() == BeatType.REST) {
			m_spawned_this_beat = false;
			m_walled_this_beat = false;
		}
	}
}
