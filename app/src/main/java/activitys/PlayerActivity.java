package activitys;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymusicplayer.R;

import java.util.List;

import bean.MusicInfo;
import constant.Setting;
import lrcView.LrcView;
import service.OnModeChangeListener;
import service.OnPlayerStateChangeListener;
import service.OnSeekChangeListener;

/**
 * Created by Administrator on 2016/8/5.
 */
public class PlayerActivity extends Activity implements View.OnClickListener{

    private ImageButton  player_mode,player_pre,player_play,player_next,player_volume;

    private ImageButton player_actionbar_back,player_actionbar_list;
    private TextView player_actionbar_song,player_actionbar_artist;

    private TextView player_seekbar_time,player_seekbar_duration;
    private SeekBar player_seekbar;

    private ListView player_queue;
    private LrcView lyric_view;

    // 回调函数更新UI
    private OnPlayerStateChangeListener stateChangeListener;
    private OnSeekChangeListener seekChangeListener;
    private OnModeChangeListener modeChangeListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        init();
        initData();
        initView();
        setListener();

    }

    private void setListener() {
        player_actionbar_back.setOnClickListener(this);
        player_actionbar_list.setOnClickListener(this);

        player_mode.setOnClickListener(this);
        player_pre.setOnClickListener(this);
        player_play.setOnClickListener(this);
        player_next.setOnClickListener(this);
        player_volume.setOnClickListener(this);

        player_seekbar.setOnSeekBarChangeListener(new SeekBarChange());


        //播放状态改变时service回调，进行界面的更新
        stateChangeListener=new OnPlayerStateChangeListener() {
            @Override
            public void onStateChange(int state, int mode, List<MusicInfo> musicList, int position) {
                if (musicList != null) {
                    switch (state) {
                        case Setting.Player.STATE_PLAY:
                            player_play.setImageResource(R.drawable.player_pause);
                            break;
                        case Setting.Player.STATE_CONTINUE:
                            player_play.setImageResource(R.drawable.player_pause);
                            break;
                        case Setting.Player.STATE_PAUSE:
                            player_play.setImageResource(R.drawable.player_play);
                            break;
                        case Setting.Player.STATE_STOP:
                            player_play.setImageResource(R.drawable.player_play);

                            break;
                    }
                } else {
                    player_actionbar_song.setText("欢迎来到我的音乐");
                    player_actionbar_artist.setText("让音乐跟我走");
                }
            }
        };
        //进度条改变了，需要更新相关组件。包括歌词
        seekChangeListener=new OnSeekChangeListener() {
            @Override
            public void onSeekChange(int progress, int max, String time, String duration) {
                player_seekbar.setMax(max);
                player_seekbar.setProgress(progress);
                player_seekbar_time.setText(time);
//                mLrcView.seekLrcToTime(progress);
            }
        };
        //播放模式改变
        modeChangeListener=new OnModeChangeListener() {
            @Override
            public void onModeChange(int mode) {
                switch (mode) {
                    case Setting.Player.MODE_SINGLE:
                        player_mode.setImageResource(R.drawable.player_mode_single);
                        Toast.makeText(PlayerActivity.this, "单曲播放", Toast.LENGTH_SHORT).show();
                        break;
                    case Setting.Player.MODE_LOOP:
                        player_mode.setImageResource(R.drawable.player_mode_loop);
                        mode = Setting.Player.MODE_LOOP;
                        Toast.makeText(PlayerActivity.this, "循环播放", Toast.LENGTH_SHORT).show();
                        break;
                    case Setting.Player.MODE_RANDOM:
                        player_mode.setImageResource(R.drawable.player_mode_random);
                        Log.d("PlayerActivity", "随机播放");
                        break;
                    case Setting.Player.MODE_ORDER:
                        player_mode.setImageResource(R.drawable.player_mode_order);
                        Toast.makeText(PlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };


    }

    private void initView() {
        // TODO: 2016/8/5 根据取得的数据进行界面相关信息的显示
    }

    private void initData() {
        // TODO: 2016/8/5 数据的加载
    }

    private void init() {
        player_mode= (ImageButton) findViewById(R.id.player_mode);
        player_pre= (ImageButton) findViewById(R.id.player_pre);
        player_play= (ImageButton) findViewById(R.id.player_play);
        player_next= (ImageButton) findViewById(R.id.player_next);
        player_volume= (ImageButton) findViewById(R.id.player_volume);

        player_actionbar_back= (ImageButton) findViewById(R.id.player_actionbar_back);
        player_actionbar_list= (ImageButton) findViewById(R.id.player_actionbar_list);
        player_actionbar_song= (TextView) findViewById(R.id.player_actionbar_song);
        player_actionbar_artist= (TextView) findViewById(R.id.player_actionbar_artist);

        player_seekbar_time= (TextView) findViewById(R.id.player_seekbar_time);
        player_seekbar_duration= (TextView) findViewById(R.id.player_seekbar_duration);
        player_seekbar= (SeekBar) findViewById(R.id.player_seekbar);

        player_queue= (ListView) findViewById(R.id.player_queue);

        lyric_view= (LrcView) findViewById(R.id.lyric_view);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.player_actionbar_back:
                //返回
                finish();
                break;
            case R.id.player_actionbar_list:
                //显示列表
                Log.d("PlayerActivity", "show list");
                break;

            // TODO: 2016/8/5 各种广播发送
            case R.id.player_mode:
                //发送广播，通过回调更新界面，模式切换
                Log.d("PlayerActivity", "switch mode");
                break;
            case R.id.player_pre:
                //发送广播，通过回调更新界面，上一首
                Log.d("PlayerActivity", "playing prevrew");
                break;
            case R.id.player_play:
                //发送广播，通过回调更新界面，播放/暂停
                Log.d("PlayerActivity", "play music");
                break;
            case R.id.player_next:
                //发送广播，通过回调更新界面，下一首
                Log.d("PlayerActivity", "playing next");
                break;
            case R.id.player_volume:
                //发送广播，通过回调更新界面，音量
                Log.d("PlayerActivity", "change volume");
                break;
        }
    }


    private class SeekBarChange implements SeekBar.OnSeekBarChangeListener{

       @Override
       public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //发送广播，回调改变时间
       }

       @Override
       public void onStartTrackingTouch(SeekBar seekBar) {

       }

       @Override
       public void onStopTrackingTouch(SeekBar seekBar) {

       }
   }
}
