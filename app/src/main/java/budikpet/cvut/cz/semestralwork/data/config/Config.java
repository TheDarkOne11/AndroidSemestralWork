package budikpet.cvut.cz.semestralwork.data.config;

import android.app.AlarmManager;
import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

/**
 * Default values of configuration.
 * Most actual values are stored in database.
 */
public class Config {
	// Synchronization config
	private static final String configFileName = "semestralWorkConfig";
	private static long syncInterval = AlarmManager.INTERVAL_HOUR;
	private static long lastSyncTime = System.currentTimeMillis();
	private static long oldestEntryDays = 30;

	public static void serializeConfig(Context context) {
		// Open (or create) serialized config file
		try(
				DataOutputStream outputStream =
						new DataOutputStream(context.openFileOutput(configFileName, Context.MODE_PRIVATE));
		) {
			outputStream.writeLong(syncInterval);
			outputStream.writeLong(lastSyncTime);
			outputStream.writeLong(oldestEntryDays);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deserializeConfig(Context context) {
		// Open and deserialize config file
		try(
				DataInputStream inputStream =
						new DataInputStream(context.openFileInput(configFileName));
		) {
			syncInterval = inputStream.readLong();
			lastSyncTime = inputStream.readLong();
			oldestEntryDays = inputStream.readLong();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			serializeConfig(context);
			deserializeConfig(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static long getSyncInterval() {
		return syncInterval;
	}

	public static long getLastSyncTime() {
		return lastSyncTime;
	}

	public static void newLastSyncTime() {
		Config.lastSyncTime = System.currentTimeMillis() + syncInterval;
	}

	public static long getOldestEntryDays() {
		return oldestEntryDays;
	}
}
