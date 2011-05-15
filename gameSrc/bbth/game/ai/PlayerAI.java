/*
 * An AI for the opposing player in a single-player match.
 */

package bbth.game.ai;

import bbth.game.BBTHSimulation;
import bbth.game.BeatTrack;
import bbth.game.Player;

public class PlayerAI {
	private Player m_player;
	private Player m_enemy;
	private BeatTrack m_beats;
	
	private static final float DEBUG_SPAWN_TIMER = 3.f;
	private float elapsedTime = 0;
	
	public PlayerAI(Player player, Player enemy, BeatTrack beatTrack) {
		m_player = player;
		m_enemy = enemy;
		m_beats = beatTrack;
	}
	
	public void update(float seconds) {
		elapsedTime += seconds;
		if (elapsedTime > DEBUG_SPAWN_TIMER) {
			elapsedTime -= DEBUG_SPAWN_TIMER;
			m_player.spawnUnit(BBTHSimulation.randInRange(0 + BeatTrack.BEAT_TRACK_WIDTH, BBTHSimulation.GAME_WIDTH),
					BBTHSimulation.GAME_HEIGHT - 50);
		}
	}
}
