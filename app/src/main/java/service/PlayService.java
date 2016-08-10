package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Random;

import bean.MusicInfo;
import constant.Setting;
import utils.PlayerHelper;

/**
 * Created by Administrator on 2016/8/6.
 */
public class PlayService extends Service implements Runnable{

    // 当前音乐播放状态，默认为等待
    public static int state = Setting.Player.STATE_WAIT;
    // 当前音乐循环模式，默认为随机
    public static int mode = Setting.Player.MODE_RANDOM;
    // 表示播放状态是否改变，进度条是否改变，播放模式时候改变
    public static boolean stateChange, seekChange, modeChange;
    // 常驻线程是否运行
    public static Boolean isRun = true;
    // 播放歌曲帮助类
    public static PlayerHelper player;
    // 当前播放列表
    public static List<MusicInfo> serviceMusicList;
    // 当前播放歌曲位置
    public static int servicePosition = 0;


    // 当前歌曲播放进度
    private static int progress = 0;
    // 当前歌曲进度条最大值
    private static int max = 0;
    // 当前播放的时间
    private static String time = "0:00";
    // 当前歌曲播放的时长
    private static String duration = "0:00";

    // 通知栏管理
    private NotificationManager notiManager;
    // 通知栏
    private Notification notifi;
    private String notifiTitle;
    private String notifiArtist;
    private String where;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PlayerReceiver receiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Setting.BroadCast.ACTION_PLAY_ITEM);
        filter.addAction(Setting.BroadCast.ACTION_PLAY_BUTTON);
        filter.addAction(Setting.BroadCast.ACTION_PLAY_PREVIOUS);
        filter.addAction(Setting.BroadCast.ACTION_PLAY_NEXT);
        filter.addAction(Setting.BroadCast.ACTION_MODE);
        filter.addAction(Setting.BroadCast.ACTION_SEEKBAR);
        registerReceiver(receiver, filter);
        // new歌曲播放类
        player = new PlayerHelper();
        // 开启常驻线程
        new Thread(this).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void run() {

    }

    private class PlayerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("PlayerReceiver", action);
            // 如果收到的是点击播放列表时发送的广播
            if (action.equals(Setting.BroadCast.ACTION_PLAY_ITEM)) {
                where = intent.getStringExtra(Setting.Player.PLAYER_WHERE);
                // 得到当前页面传过来的播放列表
                serviceMusicList = intent
                        .getParcelableArrayListExtra(Setting.Player.PLAYER_LIST);
                // 得到当前页面点击的item的position
                servicePosition = intent.getIntExtra(
                        Setting.Player.PLAYER_POSITION, 0);
                // state改变为play，播放歌曲
                state = Setting.Player.STATE_PLAY;
                // state改变
                stateChange = true;
            } else if (action.equals(Setting.BroadCast.ACTION_PLAY_BUTTON)) {
                if (serviceMusicList != null) {
                    // 如果接收的是点击暂停/播放键时的广播
                    // 根据当前状态点击后，进行相应状态改变
                    switch (state) {
                        case Setting.Player.STATE_PLAY:
                        case Setting.Player.STATE_CONTINUE:
                            state = Setting.Player.STATE_PAUSE;
                            break;
                        case Setting.Player.STATE_PAUSE:
                            state = Setting.Player.STATE_CONTINUE;
                            break;
                        case Setting.Player.STATE_STOP:
                            state = Setting.Player.STATE_PLAY;
                            break;
                    }
                    // state改变
                    stateChange = true;
                }
            } else if (action.equals(Setting.BroadCast.ACTION_PLAY_PREVIOUS)) {
                if (serviceMusicList != null) {
                    // 点击上一首按钮，如果当前位置为0，退回歌曲列表最后一首
                    if (servicePosition == 0) {
                        servicePosition = serviceMusicList.size() - 1;
                    } else {
                        servicePosition--;
                    }
                    // state改变
                    state = Setting.Player.STATE_PLAY;
                    stateChange = true;
                }
            } else if (action.equals(Setting.BroadCast.ACTION_PLAY_NEXT)) {
                if (serviceMusicList != null) {
                    // 点击下一首，根据播放模式不同，下一首位置不同
                    switch (mode) {
                        case Setting.Player.MODE_SINGLE:
                            state = Setting.Player.STATE_PLAY;
                            break;
                        case Setting.Player.MODE_LOOP:
                            if (servicePosition == serviceMusicList.size() - 1) {
                                servicePosition = 0;
                            } else {
                                servicePosition++;
                            }
                            state = Setting.Player.STATE_PLAY;
                            break;
                        case Setting.Player.MODE_RANDOM:
                            Random random = new Random();
                            int p = servicePosition;
                            while (true) {
                                servicePosition = random.nextInt(serviceMusicList
                                        .size());
                                Log.d("PlayService","p" + p + ":random"
                                        + servicePosition);
                                if (p != servicePosition) {
                                    state = Setting.Player.STATE_PLAY;
                                    break;
                                }
                            }
                            break;
                        case Setting.Player.MODE_ORDER:
                            if (servicePosition == serviceMusicList.size() - 1) {
                                state = Setting.Player.STATE_STOP;
                            } else {
                                servicePosition++;
                                state = Setting.Player.STATE_PLAY;
                            }
                            break;
                    }
                    // state改变
                    stateChange = true;
                }
            } else if (action.equals(Setting.BroadCast.ACTION_MODE)) {
                Log.i("PlayService", "收到播放模式更改广播，当前播放模式为" + mode);
                switch (mode) {
                    // 根据当前mode，做出mode的更改
                    case Setting.Player.MODE_SINGLE:
                        mode = Setting.Player.MODE_ORDER;
                        break;
                    case Setting.Player.MODE_LOOP:
                        mode = Setting.Player.MODE_RANDOM;
                        break;
                    case Setting.Player.MODE_RANDOM:
                        mode = Setting.Player.MODE_SINGLE;
                        break;
                    case Setting.Player.MODE_ORDER:
                        mode = Setting.Player.MODE_LOOP;
                        break;
                }
                // 播放模式改变
                modeChange = true;
            } else if (action.equals(Setting.BroadCast.ACTION_SEEKBAR)) {
                // seekbar发送的广播
                // 得到传过来的当前进度条进度，更改歌曲播放位置
                int seekTime = intent.getIntExtra(Setting.Player.SEEKBAR_PROGRESS,
                        progress);
                Log.i("playService", "接收到seekbar广播了" + "seekTime" + seekTime);
                player.seekToMusic(seekTime);
                // 进度条改变
                seekChange = true;
            }
        }
        }
    }
