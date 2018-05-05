package budikpet.cvut.cz.semestralwork.data.config;

import android.app.AlarmManager;

/**
 * Default values of configuration.
 * Most actual values are stored in database.
 */
public class Config {
	// Synchronization config
	public static long syncInterval = AlarmManager.INTERVAL_HOUR;
	public static long lastSyncTime = System.currentTimeMillis();
	public static long oldestEntryDays = 30;
}
