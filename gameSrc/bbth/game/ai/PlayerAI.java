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
	
	public PlayerAI(Player player, Player enemy, BeatTrack beatTrack, float difficulty) {
		m_player = player;
		m_enemy = enemy;
		m_beats = beatTrack;
		m_difficulty = difficulty;
	}
	
	public void update(float seconds) {
		/*elapsedTime += seconds;
		if (elapsedTime > DEBUG_SPAWN_TIMER) {
			elapsedTime -= DEBUG_SPAWN_TIMER;
			m_player.spawnUnit(BBTHSimulation.randInRange(0 + BeatTrack.BEAT_TRACK_WIDTH, BBTHSimulation.GAME_WIDTH),
					BBTHSimulation.GAME_HEIGHT - 50);
		}*/
		
		if (m_beats.getTouchZoneBeat() == BeatType.TAP && !m_spawned_this_beat) {
			if (MathUtils.randInRange(0, 100) < m_difficulty * 100.0f) {
				m_player.spawnUnit(BBTHSimulation.randInRange(0 + BeatTrack.BEAT_TRACK_WIDTH, BBTHSimulation.GAME_WIDTH),
						BBTHSimulation.GAME_HEIGHT - 50);
			}
			m_spawned_this_beat  = true;
		} else if (m_beats.getTouchZoneBeat() == BeatType.HOLD) {
			m_spawned_this_beat = false;
			// TODO: spawn walls
		} else if (m_beats.getTouchZoneBeat() == BeatType.REST) {
			m_spawned_this_beat = false;
		}
	}
}
