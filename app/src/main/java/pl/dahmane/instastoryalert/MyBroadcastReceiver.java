package pl.dahmane.instastoryalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static void register(Context context){
        int repeatTime = 30;  //Repeat alarm time in seconds
        AlarmManager processTimer = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        processTimer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),repeatTime*1000, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//       scheduleJob(context);
    }


}
