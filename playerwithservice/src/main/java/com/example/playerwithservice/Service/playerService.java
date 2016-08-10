package com.example.playerwithservice.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import com.example.playerwithservice.Activitys.DetailActivity;
import com.example.playerwithservice.Bean.MusicBean;
import com.example.playerwithservice.Utils.PlayerHelper;
import com.example.playerwithservice.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/8/6.
 */
public class playerService extends Service implements Runnable{


    /*-----------------添加action时要在fliter中相应的添加-----------------*/
    /**
     * 按下复位按钮时的广播事件
     */
    public static final String ACTION_RESTART="com.yh.service.ACTION_RESTART";
    /**
     * 按下播放/暂停按钮时的广播事件
     */
    public static final String ACTION_PLAY_OR_PAUSE="com.yh.service.ACTION_PLAY_OR_PAUSE";
    /**
     * 按下下一首按钮时的广播事件
     */
    public static final String ACTION_NEXT_MUSIC="com.yh.service.ACTION_NEXT_MUSIC";
    /**
     * 按下上一首按钮时的广播事件
     */
    public static final String ACTION_UP_MUSIC="com.yh.service.ACTION_UP_MUSIC";
    /**
     * 按下播放改变播放模式按钮的广播事件
     */
    public static final String ACTION_PLAY_STATE_CHANGE="com.yh.service.ACTION_PLAY_STATE_CHANGE";
    /**
     * 进度条被拖动时的广播事件
     */
    public static final String ACTION_SEEK="com.yh.service.ACTION_SEEK";
    /**
     * 列表中的歌曲被点击时的广播事件
     */
    public static final String ACTION_PLAY_ITEM_CLICK="com.yh.service.ACTION_PLAY_ITEM_CLICK";


    /**
     * 定义播放模式的类
     */
    public static class PlayMode{
        /**
         * 模式种类
         */
        public static final int ModeKinds=4;
        /**
         *  随机播放
         */
        public static final int MODE_RANDOM = 0;
        /**
         * 单曲循环
         */
        public static final int MODE_SINGLE = 1;
        /**
         * 顺序播放
         */
        public static final int MODE_ORDER = 2;
        /**
         * 循环播放
         */
        public static final int MODE_LOOP = 3;
    }

    /**
     * 当前播放器的播放模式，默认为单曲循环
     */
    public static int currentPlayMode=PlayMode.MODE_SINGLE;
    /**
     * 判断播放模式是否改变，从而决定是否更新ui
     */
    private boolean modeChange=false;




    public static class StateMode{
        /**
         * 等待状态,当前播放列表为空时
         */
        public static final int STATE_WAIT = 0;
        /**
         * 播放状态
         */
        public static final int STATE_PLAY = 1;
        /**
         * 暂停状态
         */
        public static final int STATE_PAUSE = 2;
        /**
         * 停止状态，当前列表播放完毕
         */
        public static final int STATE_STOP = 3;
        /**
         * 继续播放状态
         */
        public static final int STATE_CONTINUE = 4;
    }
    /**
     * 当前的播放状态，默认为等待
     */
    public static int PlayState=StateMode.STATE_STOP;
    public static boolean stateChange=false;


    /**
     * 进度条是否改变
     */
    private boolean seekChange=false;
    /**
     * 当前歌曲播放进度
     */
    private static int progress = 0;
    /**
     * 当前歌曲进度条最大值
     */
    private static int max = 0;
    /**
     * 当前播放的时间
     */
    private static String time = "0:00";
    /**
     * 当前歌曲播放的时长
     */
    private static String duration = "0:00";
    /*-------------------------------------*/

    /**
     * service中的常驻线程是否运行
     */
    public static boolean isRun=true;
    /**
     * 播放帮助类，内含一个media player
     */
    public static PlayerHelper player;

    /**
     * 播放列表中的音乐信息,通过点击列表项将其所在的列表整个的传了过来
     */
    public static ArrayList<MusicBean> playlist_musicInfo;
    /**
     * 当前播放歌曲在播放列表中的位置
     */
    public static int servicePosition=0;

    /**
     * 当前歌曲是来自本地还是网络,通过点击列表项传入
     */
    private String where="local";

    private  PlayerReceiver receiver;

    /*-------------------------------------------------------*/
    // 观察者模式，所维护的订阅队列,用于播放模式改变监听
    private static List<OnModeChangeListener> modeListenerList = new ArrayList<OnModeChangeListener>();
    private static List<OnPlayerStateChangeListener> stateListeners=new ArrayList<>();
    private static List<OnSeekChangeListener> seekListenerList=new ArrayList<>();
    /*----------------------------------------------------*/

    /*-----------------通知栏相关--------------------*/
    // 通知栏管理
    private NotificationManager notiManager;
    // 通知栏
    private Notification notifi;
    private String notifiTitle;
    private String notifiArtist;



    private Button noti_btn_play,noti_btn_next;
    /*--------------------------------------------*/


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("playerService", "service create!");
        player=new PlayerHelper();

        receiver=new PlayerReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ACTION_RESTART);
        filter.addAction(ACTION_PLAY_OR_PAUSE);
        filter.addAction(ACTION_NEXT_MUSIC);
        filter.addAction(ACTION_UP_MUSIC);
        filter.addAction(ACTION_PLAY_STATE_CHANGE);
        filter.addAction(ACTION_SEEK);
        filter.addAction(ACTION_PLAY_ITEM_CLICK);
        registerReceiver(receiver,filter);

        notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        new Thread(this).start();//开启常驻线程
    }

    //结束时，取消广播
    @Override
    public boolean stopService(Intent name) {
        unregisterReceiver(receiver);
        notiManager.cancel(0);
        return super.stopService(name);
    }

    /**
     * notification的设置更新并显示
     * @param title
     * @param artist
     */
    public void showNotification(String title,String artist){
        //通知栏初始化
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        RemoteViews  mRemoteViews= new RemoteViews(getPackageName(), R.layout.notification_layout);
        mRemoteViews.setTextViewText(R.id.notifi_song, title);
        mRemoteViews.setTextViewText(R.id.notifi_artist, artist);
        changeNotificationBtn(mRemoteViews,PlayState);
        // TODO: 2016/8/7 bug未解决，无法设置通知栏中按钮的text或img
        setNotifiClick(mRemoteViews);

        mBuilder.setContent(mRemoteViews)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在播放")
//                .setPriority( Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notiManager.notify(1, notify);

//
//        notifi = new Notification();
//        notifi.contentView =mRemoteViews;
//        notifi.contentIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        notifi.flags |= Notification.FLAG_NO_CLEAR
//                | Notification.FLAG_ONGOING_EVENT;
//        notifi.icon=R.mipmap.ic_launcher;
//        notifi.tickerText = "正在播放：" + title;
//        notiManager.notify(0, notifi);
    }

    /**
     * 改变通知栏状态中的开始/暂停按钮
     * @param
     * @param mRemoteViews
     * @param state
     */
    private void changeNotificationBtn(RemoteViews mRemoteViews, int state) {
        switch (state){
            case playerService.StateMode.STATE_CONTINUE:
            case playerService.StateMode.STATE_PLAY:
                mRemoteViews.setViewVisibility(R.id.noti_btn_play, View.VISIBLE);
                mRemoteViews.setViewVisibility(R.id.noti_btn_pause, View.GONE);
                break;
            case playerService.StateMode.STATE_PAUSE:
            case playerService.StateMode.STATE_STOP:
                mRemoteViews.setViewVisibility(R.id.noti_btn_play, View.GONE);
                mRemoteViews.setViewVisibility(R.id.noti_btn_pause, View.VISIBLE);
                break;

        }
    }

    private void setNotifiClick(RemoteViews contentView){
        //开始/暂停按钮的广播事件
        Intent buttonIntent=new Intent(ACTION_PLAY_OR_PAUSE);
        PendingIntent pButtonIntent=PendingIntent.getBroadcast(this,1,buttonIntent,0);
        contentView.setOnClickPendingIntent(R.id.noti_btn_play,pButtonIntent);
        contentView.setOnClickPendingIntent(R.id.noti_btn_pause,pButtonIntent);

        //下一曲按钮的广播事件
        Intent next_btn_intent=new Intent(ACTION_NEXT_MUSIC);
        PendingIntent pNext_btn_intent=PendingIntent.getBroadcast(this,2,next_btn_intent,0);
        contentView.setOnClickPendingIntent(R.id.noti_btn_next,pNext_btn_intent);
    }

    //广播接收者，接收改变的广播
    public class PlayerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            Log.d("PlayerReceiver", action);
//            Log.d("PlayerReceiver", "playlist_musicInfo:" + playlist_musicInfo.toString());

            if (action.equals(ACTION_PLAY_ITEM_CLICK)){
                where=intent.getStringExtra("where");
                playlist_musicInfo=intent.getParcelableArrayListExtra("play_list");
                servicePosition=intent.getIntExtra("position",0);

                PlayState=StateMode.STATE_PLAY;
                stateChange=true;
            }

            if (action.equals(ACTION_PLAY_STATE_CHANGE)){
                currentPlayMode=(currentPlayMode+1)%PlayMode.ModeKinds;
                Log.d("PlayerReceiver", "currentPlayMode:" + currentPlayMode);
                modeChange=true;
            }

            if (action.equals(ACTION_PLAY_OR_PAUSE)){
                if (playlist_musicInfo != null) {
                    // 如果接收的是点击暂停/播放键时的广播
                    // 根据当前状态点击后，进行相应状态改变
                    switch (PlayState) {
                        case StateMode.STATE_PLAY:
                        case StateMode.STATE_CONTINUE:
                            PlayState = StateMode.STATE_PAUSE;

                            break;
                        case StateMode.STATE_PAUSE:
                            PlayState = StateMode.STATE_CONTINUE;

                            break;
                        case StateMode.STATE_STOP:
                            PlayState = StateMode.STATE_PLAY;

                            break;
                    }
                    // state改变
                    stateChange = true;
                }
            }

            if (action.equals(ACTION_UP_MUSIC)) {
                if (playlist_musicInfo != null) {
                    // 点击上一首按钮，如果当前位置为0，退回歌曲列表最后一首
                    if (servicePosition == 0) {
                        servicePosition = playlist_musicInfo.size() - 1;
                    } else {
                        servicePosition--;
                    }
                    // state改变
                    PlayState = StateMode.STATE_PLAY;
                    stateChange = true;
                }
            }

            if (action.equals(ACTION_NEXT_MUSIC)) {
                if (playlist_musicInfo != null) {
                    // 点击下一首，根据播放模式不同，下一首位置不同
                    switch (currentPlayMode) {
                        case PlayMode.MODE_SINGLE:
                            PlayState = StateMode.STATE_PLAY;
                            break;
                        case PlayMode.MODE_LOOP:
                            if (servicePosition == playlist_musicInfo.size() - 1) {
                                servicePosition = 0;
                            } else {
                                servicePosition++;
                            }
                            PlayState = StateMode.STATE_PLAY;
                            break;
                        case PlayMode.MODE_RANDOM:
                            Random random = new Random();
                            int p = servicePosition;
                            while (true) {
                                servicePosition = random.nextInt(playlist_musicInfo
                                        .size());

                                if (p != servicePosition) {
                                    PlayState = StateMode.STATE_PLAY;
                                    break;
                                }
                            }
                            break;
                        case PlayMode.MODE_ORDER:
                            if (servicePosition == playlist_musicInfo.size() - 1) {
                                PlayState = StateMode.STATE_STOP;
                            } else {
                                servicePosition++;
                                PlayState = StateMode.STATE_PLAY;
                            }
                            break;
                    }
                    // state改变
                    stateChange = true;
                }
            }

            if (action.equals(ACTION_SEEK)) {
                // seekbar发送的广播
                // 得到传过来的当前进度条进度，更改歌曲播放位置
                int seekTime = intent.getIntExtra("seek_progress",
                        progress);
                Log.d("playerService", "接收到seekbar广播了" + "seekTime" + seekTime);
                player.seekToMusic(seekTime);
                // 进度条改变
                seekChange = true;
                //按钮改变
                PlayState=StateMode.STATE_PLAY;
                handler.sendEmptyMessage(HandlerMessageWhat.PLAY_STATE_CHANGE);

            }


        }
    }


    //常驻线程，用来观察 播放模式、播放状态、进度条等是否改变，并分发消息
    @Override
    public void run() {
        while (isRun){
            if (modeChange){
                handler.sendEmptyMessage(HandlerMessageWhat.PLAY_MODE_CHANGE);
                modeChange=false;
            }
            if (stateChange){
                // 判断如果state改变，player播放类执行不同的方法
                    Log.d("playerService", "stateChange=" + PlayState);
                    switch (PlayState) {
                        case StateMode.STATE_WAIT:
                            break;
                        case StateMode.STATE_PLAY:
                            if (where.equals("local")) {
                                player.play(playlist_musicInfo.get(servicePosition)
                                        .getUrl());
                            } else if (where.equals("internet")) {
                                Uri uri = Uri.parse(playlist_musicInfo.get(
                                        servicePosition).getUrl());
                                player.playInternet(getApplicationContext(), uri);
                            }
                            // 播放状态要动seekbar
                            seekChange = true;
                            break;
                        case StateMode.STATE_PAUSE:
                            player.pause();
                            break;
                        case StateMode.STATE_CONTINUE:
                            player.continuePlay();
                            // 播放状态要动seekbar
                            seekChange = true;
                            break;
                        case StateMode.STATE_STOP:
                            player.stop();
                            break;
                    }
                    // state改变为false
                    stateChange = false;
                    // 向handler发送一条消息，通知handler执行回调函数
                    handler.sendEmptyMessage(HandlerMessageWhat.PLAY_STATE_CHANGE);
                    //通知栏的更新
                    notifiTitle = playlist_musicInfo.get(servicePosition).getTitle();
                    notifiArtist = playlist_musicInfo.get(servicePosition)
                            .getArtist();
                    showNotification(notifiTitle, notifiArtist);

            }
            //播放时更新进度条
            if (player.isPlaying()) {
                seekChange = true;
            } else {
                seekChange = false;
            }

            if (seekChange){
                // 得到当前播放时间，int，毫秒单位，也是进度条的当前进度
                progress = player.getPlayCurrentTime();
                // 得到歌曲播放总时长，为进度条的最大值
                max = player.getPlayDuration();
                // 当前播放时间改变单位为分
                float floatTime = (float) progress / 1000.0f / 60.0f;
                // 当前播放时间转换为字符类型
                String timeStr = Float.toString(floatTime);
                // 根据小数点切分
                String timeSub[] = timeStr.split("\\.");
                // 初始值为0.0，在后边补0
                if (timeSub[1].length() < 2) {
                    timeSub[1] = timeSub[1] + "0";
                } else {
                    // 截取小数点后两位
                    timeSub[1] = timeSub[1].substring(0, 2);
                }
                // 拼接得到当前播放时间，用于UI界面显示
                time = timeSub[0] + ":" + timeSub[1];
                // seekChange改回false
                seekChange = false;
                try {
                    // 等1s发送消息
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 发送相应消息给handler
                handler.sendEmptyMessage(HandlerMessageWhat.SEEK_BAR_CHANGED);
            }
        }
    }

    /**
     * service中handler的消息编码表
     */
    public static class HandlerMessageWhat{
        /**
         * 播放模式发生改变
         */
        public static final int PLAY_MODE_CHANGE = 0;
        /**
         * 播放状态发生改变
         */
        public static final int PLAY_STATE_CHANGE=1;
        /**
         * 进度条发生改变
         */
        public static final int SEEK_BAR_CHANGED=2;
    }
    //每当观察的对象发生改变，通知所有的订阅者，并发生相应的改变。
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HandlerMessageWhat.PLAY_MODE_CHANGE:
                    for (OnModeChangeListener listener : modeListenerList) {
                        listener.onModeChange(currentPlayMode);
                    }
                    break;
                case HandlerMessageWhat.PLAY_STATE_CHANGE:
                    for (OnPlayerStateChangeListener listener : stateListeners) {
                        listener.onStateChange(PlayState, currentPlayMode, playlist_musicInfo,
                                servicePosition);
                    }

                    break;
                case HandlerMessageWhat.SEEK_BAR_CHANGED:
                    for (OnSeekChangeListener listener : seekListenerList) {
                        listener.onSeekChange(progress, max, time, duration);
                    }
                    break;
            }
        }
    };



    /*-------------------注册与解除注册---------------------------*/
    /**
     * 向service注册一个监听器，用于监听mode的改变
     *
     * @param modeListener
     */
    public static void registerModeChangeListener(
            OnModeChangeListener modeListener) {
        modeListener.onModeChange(currentPlayMode);
        modeListenerList.add(modeListener);
        Log.d("playerService", "注册ModeChange，当前一共有" + modeListenerList.size()
                + "个");
    }
    /**
     * 解除之前注册的监听器
     *
     * @param modeListener
     */
    public static void unRegisterModeChangeListener(
            OnModeChangeListener modeListener) {
        modeListenerList.remove(modeListener);
        Log.d("playerService", "解除注册modeChange，当前一共有" + modeListenerList.size()
                + "个");
    }

    /**
     * 向service注册一个监听器，用于监听播放状态的改变
     *
     * @param listener
     */
    public static void registerStateChangeListener(
            OnPlayerStateChangeListener listener) {
        listener.onStateChange(PlayState, currentPlayMode, playlist_musicInfo, servicePosition);
        stateListeners.add(listener);
        Log.e("playerService", "注册stateChange的监听，当前一共有" + stateListeners.size()
                + "个");
    }
    /**
     * 解除之前注册的监听器
     *
     * @param statelistener
     */
    public static void unRegisterStateChangeListener(
            OnPlayerStateChangeListener statelistener) {
        stateListeners.remove(statelistener);
        Log.d("playerService", "解除注册listener，当前一共有" + stateListeners.size()
                + "个");
    }

    /**
     * 向service注册一个监听器，用于监听seekbar改变
     *
     * @param seekListener
     */
    public static void registerSeekChangeListener(
            OnSeekChangeListener seekListener) {
        seekListener.onSeekChange(progress, max, time, duration);
        seekListenerList.add(seekListener);
        Log.d("playerService", "注册seekChange的监听，当前一共有" + seekListenerList.size() + "个");
    }


    /**
     * 解除之前注册的监听器
     *
     * @param seekListener
     */
    public static void unRegisterSeekChangeListener(
            OnSeekChangeListener seekListener) {
        seekListenerList.remove(seekListener);
        Log.d("playerService", "解除注册seekChange，当前一共有" + stateListeners.size()
                + "个");

    }


}
