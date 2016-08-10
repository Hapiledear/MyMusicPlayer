package com.example.playerwithservice.Activitys;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playerwithservice.Bean.MusicBean;
import com.example.playerwithservice.Service.OnModeChangeListener;
import com.example.playerwithservice.Service.OnPlayerStateChangeListener;
import com.example.playerwithservice.Service.OnSeekChangeListener;
import com.example.playerwithservice.R;
import com.example.playerwithservice.lyric.DefaultLrcBuilder;
import com.example.playerwithservice.lyric.ILrcBuilder;
import com.example.playerwithservice.lyric.LrcRow;
import com.example.playerwithservice.lyric.LrcView;
import com.example.playerwithservice.Service.playerService;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity{

    private SeekBar mSeekBar;
    private TextView txt_name,txt_nTime,txt_tTime;
    private Button btn_play_mode,btn_pause;
    private ListView mListView;
    private LrcView mLrcView;

    private List<MusicBean> musicInfo=new ArrayList<>();
    private MyAdapter adapter=new MyAdapter();


    private Intent service;
    private Intent broadcast;


    private OnModeChangeListener modeChangeListener;
    private OnPlayerStateChangeListener playerStateChangeListener;
    private OnSeekChangeListener seekChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
        initData();
        setListener();

        service=new Intent(DetailActivity.this,playerService.class);
        startService(service);

    }

    //将数据填入界面，订阅事件。
    @Override
    protected void onResume() {
        super.onResume();

        playerService.registerModeChangeListener(modeChangeListener);
        playerService.registerStateChangeListener(playerStateChangeListener);
        playerService.registerSeekChangeListener(seekChangeListener);
    }

    //退订事件
    @Override
    protected void onPause() {
        super.onPause();

        playerService.unRegisterModeChangeListener(modeChangeListener);
        playerService.unRegisterStateChangeListener(playerStateChangeListener);
        playerService.unRegisterSeekChangeListener(seekChangeListener);
    }

    //销毁服务
    @Override
    protected void onDestroy() {
        stopService(service);
        super.onDestroy();
    }

    private void setListener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DetailActivity", "position:" + position);
                broadcast.setAction(playerService.ACTION_PLAY_ITEM_CLICK);

                broadcast.putExtra("where","local");
                broadcast.putExtra("position",position);
                broadcast.putParcelableArrayListExtra("play_list", (ArrayList<? extends Parcelable>) musicInfo);

                sendBroadcast(broadcast);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean canSend=false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("DetailActivity", "fromUser:" + fromUser);
                canSend=fromUser;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 进度条改变，发送广播，回调改变播放时间
                if (canSend) {
                    broadcast.setAction(playerService.ACTION_SEEK);
                    broadcast.putExtra("seek_progress", seekBar.getProgress());
                    sendBroadcast(broadcast);
                }
            }
        });

        //按下播放模式按钮-->发送广播给service->改变播放模式->通知改变界面->回调，改变界面
        modeChangeListener=new OnModeChangeListener() {
            @Override
            public void onModeChange(int mode) {
                switch (mode){
                    case playerService.PlayMode.MODE_LOOP:
                        btn_play_mode.setText("列表循环");
                        break;
                    case playerService.PlayMode.MODE_ORDER:
                        btn_play_mode.setText("顺序播放");
                        break;
                    case playerService.PlayMode.MODE_RANDOM:
                        btn_play_mode.setText("随机播放");
                        break;
                    case playerService.PlayMode.MODE_SINGLE:
                        btn_play_mode.setText("单曲循环");
                        break;
                }
            }
        };

        playerStateChangeListener=new OnPlayerStateChangeListener() {
            @Override
            public void onStateChange(int state, int mode, List<MusicBean> musicList, int position) {
                // 更改当前界面UI
                if (musicList != null) {
                    if (txt_name == null) {
                        Log.e("mainActivity", "找不到title");
                    }
//                    String lrcName = musicList.get(position).getTitle();
//                    Log.d("mainactivity", "lrcName----->" + lrcName);
                    String lrcName="test.lrc";
                    String lrcPath = Environment.getExternalStorageDirectory()
                            .getPath() + "/Music/" + lrcName;
                    Log.e("mainactivity", "lrcPath------>" + lrcPath);
                    String lrcStr = getFromLrcFile(lrcPath);
                    // 解析歌词
                    ILrcBuilder builder = new DefaultLrcBuilder();
                    List<LrcRow> rows = builder.getLrcRows(lrcStr);
                    // 设置歌词
                    mLrcView.setLrc(rows);

                    // 显示当前播放歌曲信息
                    txt_name.setText(musicList.get(position).getTitle());

                    long l = musicList.get(position).getDuration();
                    float longF = (float) l / 1000.0f / 60.0f;
                    String longStr = Float.toString(longF);
                    String dur[] = longStr.split("\\.");
                    txt_tTime.setText(dur[0] + ":" + dur[1].substring(0, 2));

                } else {
                    txt_name.setText("欢迎来到我的音乐");
                }

                switch (state){
                    case playerService.StateMode.STATE_CONTINUE:
                    case playerService.StateMode.STATE_PLAY:
                        btn_pause.setText("暂停");
                        break;
                    case playerService.StateMode.STATE_PAUSE:
                    case playerService.StateMode.STATE_STOP:
                        btn_pause.setText("开始");
                        break;

                }
            }
        };

        seekChangeListener=new OnSeekChangeListener() {
            @Override
            public void onSeekChange(int progress, int max, String time, String duration) {
                mSeekBar.setMax(max);
                mSeekBar.setProgress(progress);
                txt_nTime.setText(time);

                mLrcView.seekLrc(progress);
            }
        };



    }

    // 从lrc中读取字节流
    private String getFromLrcFile(String lrcPath) {
        try {
            InputStreamReader inputReader = new InputStreamReader(
                    new FileInputStream(lrcPath));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                Result += line + "\r\n";
            }
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    private void initData() {
        //使用LoadManager来进行数据的加载
//        LoaderManager loaderManager=getLoaderManager();
//        loaderManager.initLoader(0,null,mLoader);
    }

    private void init() {
        broadcast=new Intent();

        btn_play_mode= (Button) findViewById(R.id.btn_play_mode);
        btn_pause= (Button) findViewById(R.id.btn_pause);

        mListView= (ListView) findViewById(R.id.playing_music_list);
        mListView.setAdapter(adapter);

        mSeekBar= (SeekBar) findViewById(R.id.seekBar);

        txt_name= (TextView) findViewById(R.id.txt_musicName);
        txt_nTime= (TextView) findViewById(R.id.txt_ntime);
        txt_tTime= (TextView) findViewById(R.id.txt_tTime);

        mLrcView= (LrcView) findViewById(R.id.lrc_view);
    }

    /**
     * 回调方法，装载数据
     */
//    private LoaderManager.LoaderCallbacks<Cursor> mLoader=new LoaderManager.LoaderCallbacks<Cursor>() {
//        //返回一个加载器
//        @Override
//        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//            return new CursorLoader(DetailActivity.this,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//        }
//
//        //查询完成后，将数据存入List,并填充界面
//        @Override
//        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//
//            if(cursor == null){
//                Toast.makeText(DetailActivity.this, "失败", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            while (cursor.moveToNext()){
//                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
//                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
//                String artist=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
//                String album=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
//                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
//                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
//                int size = (int) cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
//
//                MusicBean musicBean=new MusicBean(id,title,artist,url,duration,size,album);
//                musicInfo.add(musicBean);
//                Log.v("DetailActivity",musicBean.toString());
//            }
//
////            playerService.playlist_musicInfo=musicInfo;//将播放列表传入service
//            adapter.notifyDataSetChanged();
//
//
//        }
//
//        @Override
//        public void onLoaderReset(Loader<Cursor> loader) {
//
//        }
//    };

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return musicInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView=new TextView(DetailActivity.this);
            textView.setText(musicInfo.get(position).getTitle());
            return textView;
        }
    }

    /**
     * 复位按钮按下事件
     * @param view
     */
    public void OnMusicRestart(View view){
        broadcast.setAction(playerService.ACTION_SEEK);
        broadcast.putExtra("seek_progress",0);
        sendBroadcast(broadcast);
    }

    /**
     * 播放模式切块
     * @param view
     */
    public void OnPlayStateChange(View view){
        broadcast.setAction(playerService.ACTION_PLAY_STATE_CHANGE);
        sendBroadcast(broadcast);
    }

    /**
     * 播放下一首歌曲
     * @param view
     */
    public void OnNextMusic(View view){
        broadcast.setAction(playerService.ACTION_NEXT_MUSIC);
        sendBroadcast(broadcast);
    }

    /**
     * 播放上一首歌曲
     * @param view
     */
    public void OnUpMusic(View view){
        broadcast.setAction(playerService.ACTION_UP_MUSIC);
        sendBroadcast(broadcast);
    }

    /**
     * 开始/暂停播放
     * @param view
     */
    public void OnMusicStateChange(View view){
        broadcast.setAction(playerService.ACTION_PLAY_OR_PAUSE);
        sendBroadcast(broadcast);
    }

    /**
     * 打开/关闭播放列表
     * @param view
     */
    public void OnListMenu(View view){
        if (((CheckBox)view).isChecked()){
            mListView.setVisibility(View.VISIBLE);
            mLrcView.setVisibility(View.GONE);
        }else {
            mListView.setVisibility(View.GONE);
            mLrcView.setVisibility(View.VISIBLE);
        }
    }
}
