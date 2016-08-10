package activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.administrator.mymusicplayer.R;

/**
 * Created by Administrator on 2016/8/4.
 */
public class AboutActivity extends Activity {


    private ImageButton actionbar_navigation,actionbar_button;
    private TextView actionbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();
    }

    private void init() {
        actionbar_navigation= (ImageButton) findViewById(R.id.actionbar_navigation);
        actionbar_button= (ImageButton) findViewById(R.id.actionbar_button);
        actionbar_title= (TextView) findViewById(R.id.actionbar_title);

        actionbar_navigation.setImageResource(R.drawable.actionbar_menu);
        actionbar_button.setImageResource(R.drawable.actionbar_scan);
        actionbar_button.setBackgroundResource(R.drawable.sel_main_actionbar_item);
        actionbar_title.setText(R.string.about);

        actionbar_button.setVisibility(View.GONE);
        actionbar_navigation.setVisibility(View.GONE);
    }


}
