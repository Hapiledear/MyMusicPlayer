package activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.mymusicplayer.R;

/**
 * Created by Administrator on 2016/8/4.
 */
public class FavorActivity extends Activity implements View.OnClickListener{

    private ImageButton actionbar_navigation,actionbar_button;
    private TextView actionbar_title;

    private ImageView favor_nothing_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor);

        init();
        initData();
        initView();
        setListener();
    }

    private void setListener() {
        actionbar_navigation.setOnClickListener(this);
    }

    private void initView() {
        actionbar_navigation.setImageResource(R.drawable.actionbar_back);
        actionbar_button.setImageResource(R.drawable.actionbar_scan);
        actionbar_title.setText(R.string.ilike);
    }

    private void initData() {
        // TODO: 2016/8/4 加载数据
        favor_nothing_img.setVisibility(View.VISIBLE);
    }

    private void init() {
        actionbar_navigation= (ImageButton) findViewById(R.id.actionbar_navigation);
        actionbar_button= (ImageButton) findViewById(R.id.actionbar_button);
        actionbar_title= (TextView) findViewById(R.id.actionbar_title);

        favor_nothing_img= (ImageView) findViewById(R.id.favor_nothing_img);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionbar_navigation:
                finish();
                break;
        }
    }
}
