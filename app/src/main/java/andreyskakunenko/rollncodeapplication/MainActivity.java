package andreyskakunenko.rollncodeapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button mButtonOff, mButtonOn;
    ProgressBar mProgressBar;
    TextView mCount, mTime;

    public final static String APP_PREFERENCES = "mysettings";
    public final static String APP_PREFERENCES_TIME = "time";
    public final static String APP_PREFERENCES_COUNT = "count";

    public final static String BROADCAST_ACTION = "DRONDev";

    SharedPreferences mSettings;
    BroadcastReceiver mBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonOff = findViewById(R.id.button_off);
        mButtonOn = findViewById(R.id.button_on);
        mProgressBar = findViewById(R.id.progress_bar);

        mCount = findViewById(R.id.count);
        mTime = findViewById(R.id.last_start_value);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        boolean hasVisited = mSettings.getBoolean("hasVisited",false);

        if(!hasVisited){
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean("hasVisited", true);
            editor.apply();
            mCount.setText("0");
            mTime.setText(R.string.none);

        }else{
            mCount.setText(String.valueOf(mSettings.getInt(APP_PREFERENCES_COUNT,0)));
            mTime.setText(mSettings.getString(APP_PREFERENCES_TIME, getResources().getString(R.string.none)));
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String time = intent.getStringExtra(APP_PREFERENCES_TIME);
                int count = intent.getIntExtra(APP_PREFERENCES_COUNT,888);
                int status = intent.getIntExtra("status",0);
                if (status == 1)
                    mCount.setText(String.valueOf(count));
                if (status == 2)
                    mTime.setText(time);
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }

    public void onButton(View view) {
        mButtonOn.setClickable(false);
        mProgressBar.setVisibility(View.VISIBLE);
        startService(new Intent(MainActivity.this,ServiceIncreaserCounter.class));
    }

    public void offButton(View view) {
        mButtonOn.setClickable(true);
        mProgressBar.setVisibility(View.INVISIBLE);
        stopService(new Intent(MainActivity.this,ServiceIncreaserCounter.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        stopService(new Intent(MainActivity.this,ServiceIncreaserCounter.class));
    }
}
