package pl.dahmane.instastoryalert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class CheckerService extends JobService {

    public static void scheduleJob(Context context) {
        final int interval = 10 * 60 * 1000;
        ComponentName serviceComponent = new ComponentName(context, CheckerService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1, serviceComponent);
        builder.setMinimumLatency(interval);
        builder.setBackoffCriteria(3000, JobInfo.BACKOFF_POLICY_LINEAR);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require unmetered network
        builder.setRequiresDeviceIdle(false); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("MYDATA",  "onStartJob called!");
        scheduleJob(this);
        check();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public void check(){
        new CheckTask().execute(this);
    }

    public static class CheckTask extends AsyncTask<Context, Void, Void>{
        @Override
        protected Void doInBackground(Context... contexts) {
            Magic magic = new Magic(contexts[0]);
            magic.getReady();
            if(!magic.isReady()){
                Log.i("MYDATA",  "Magic not ready!");
                return null;
            }
            ArrayList<InstaView> views = magic.getNewViews();
            int len = views.size();
            int notId = ThreadLocalRandom.current().nextInt();
            if(len == 1){
                User user = views.get(0).getUser();
                showNotification(contexts[0], notId, user.getUsername(), user.getUsername() + " watched your story!" );
            }else if(len > 1){
                showNotification(contexts[0], notId, "You got new views","You got " + views.size() + " new views on your story!" );
            }
            Log.i("MYDATA",  "Got " + views.size() + " new views!!!");
            return null;
        }
    }

    private static void showNotification(Context context, int id, String title, String text){
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alerts")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{ 500, 500 })
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(id, builder.build());
    }

}
