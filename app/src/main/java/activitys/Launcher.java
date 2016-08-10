package activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.administrator.mymusicplayer.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/8/3.
 */
public class Launcher extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activty_launcher);

        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent(Launcher.this,MainActivity.class);
                startActivity(intent);

                finish();
//                overridePendingTransition(R.anim.actiivty_switch_in,R.anim.activity_switch_out);
            }
        };

        timer.schedule(task,2000);
    }
}
