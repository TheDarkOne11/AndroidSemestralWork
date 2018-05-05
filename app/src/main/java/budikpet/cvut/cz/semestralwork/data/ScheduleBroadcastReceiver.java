package budikpet.cvut.cz.semestralwork.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduleBroadcastReceiver extends BroadcastReceiver {
	private long interval = 1 * 60 * 1000;
	public static String scheduleBroadcastFilter = "ScheduleBroadcastReceiverFilter";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Set alarm
		AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		long alertTime = System.currentTimeMillis() + interval;
		Intent feedIntent = new Intent(context, SyncService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, feedIntent, 0);
		am.setExact(AlarmManager.RTC, alertTime, pendingIntent);
	}
}
