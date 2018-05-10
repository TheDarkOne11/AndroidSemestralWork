package budikpet.cvut.cz.semestralwork.data.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import budikpet.cvut.cz.semestralwork.data.config.Config;

public class SetAlarmBroadcastReceiver extends BroadcastReceiver {
	public static String scheduleBroadcastFilter = "ScheduleBroadcastReceiverFilter";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Set alarm
		Log.i("ALARM", "Setting alarm");
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent feedIntent = new Intent(context, SyncService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, feedIntent, 0);

		// TODO Použít inexactRepeating, tím by odpadl problém s nastavováním
		am.setExact(AlarmManager.RTC, Config.getLastSyncTime(), pendingIntent);
	}
}
