package andreyskakunenko.rollncodeapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static andreyskakunenko.rollncodeapplication.MainActivity.APP_PREFERENCES;
import static andreyskakunenko.rollncodeapplication.MainActivity.APP_PREFERENCES_COUNT;
import static andreyskakunenko.rollncodeapplication.MainActivity.APP_PREFERENCES_TIME;

public class ServiceIncreaserCounter extends Service {

    SharedPreferences mSettings;
    boolean stopThread = false;
    int newCount;

    public ServiceIncreaserCounter() {
    }

    @Override
    public void onCreate() {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_TIME,date);
        editor.apply();

        Intent intent1 = new Intent(MainActivity.BROADCAST_ACTION);
        intent1.putExtra("status",2);
        intent1.putExtra(APP_PREFERENCES_TIME,date);
        sendBroadcast(intent1);

        Thread thread = new Thread(new Thread1());
        thread.start();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        stopThread=true;
        super.onDestroy();
    }

    class Thread1 implements Runnable{
        @Override
        public void run() {
            newCount = mSettings.getInt(APP_PREFERENCES_COUNT,0);
            while (true){
                try{
                    if(stopThread) {
                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putInt(APP_PREFERENCES_COUNT, newCount);
                        editor.apply();
                        return;
                    }
                        TimeUnit.SECONDS.sleep(5);

                            newCount += 1;
                            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                            intent.putExtra("status", 1);
                            intent.putExtra(APP_PREFERENCES_COUNT, newCount);
                            sendBroadcast(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
               }
            }
        }
    }
}
