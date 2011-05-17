package bbth.engine.achievements;

import java.util.*;

import android.app.Activity;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.graphics.*;

/**
 * Singleton that handles achievement locking and unlocking
 * @author Justin
 *
 */
public enum Achievements {
	INSTANCE;

	// boolean is true iff the achievement is unlocked
	private HashMap<String, Boolean> _achievementMap;
	private ArrayList<UnlockAnimation> _unlocks;
	private SharedPreferences _settings;
	private Paint _paint;
	
	private Achievements() {
		_achievementMap = new HashMap<String, Boolean>();
		_unlocks = new ArrayList<UnlockAnimation>();
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}
		
	// Should be called before checking achievement statuses or going to an achievement screen
	@SuppressWarnings("unchecked")
	public void initialize(Context context) {
		_settings = context.getSharedPreferences("achievements", Activity.MODE_PRIVATE);
		_achievementMap = (HashMap<String, Boolean>) _settings.getAll();
	}
	
	// locks an achievement, also adding it if it doesn't yet exist
	public void lock(String achievement) {
		_achievementMap.put(achievement, false);
		Editor edit = _settings.edit();
		edit.putBoolean(achievement, false);
		edit.commit();
	}

	// unlocks an achievement
	public void unlock(String achievement) {
		if (_achievementMap.containsKey(achievement) && _achievementMap.get(achievement).equals(Boolean.TRUE)) {
			// already unlocked, no need to do anything
			return;
		}
		_achievementMap.put(achievement, true);
		_unlocks.add(new UnlockAnimation(achievement));
		Editor edit = _settings.edit();
		edit.putBoolean(achievement, true);
		edit.commit();
	}
	
	// return the status of an achievement
	public boolean isUnlocked(String achievement) {
		if (_achievementMap.containsKey(achievement)) {
			return _achievementMap.get(achievement);
		}
		return false;
	}
	
	public void tick(float seconds) {
		int n = _unlocks.size();
		for (int i = n - 1; i >= 0; --i) {
			UnlockAnimation u = _unlocks.get(i);
			if (u.isOver()) {
				_unlocks.remove(u);
			} else {
				u.tick(seconds);
			}
		}
	}
	
	// draw the unlock event animations
	public void draw(Canvas canvas, float animationWidth, float animationHeight) {
		int n = _unlocks.size();
		float top = 0;
		for (int i = 0; i < n; ++i) {
			top = _unlocks.get(i).draw(canvas, _paint, animationWidth, animationHeight, top);
		}
	}
	
	// get all achievements, useful for displaying all of them in one menu
	public Map<String, Boolean> getAll() {
		return Collections.unmodifiableMap(_achievementMap);
	}
	
}
