package constant;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/4.
 */
public class Setting implements Serializable{

    public static boolean only_wifi_connect_internet=false;
    /**
     * 当前是否在播放音乐
     */
    public static boolean isPlaying=false;

    public class Player{
        public static final int MODE_RANDOM = 0;// 随机播放
        public static final int MODE_SINGLE = 1;// 单曲循环
        public static final int MODE_ORDER = 2;// 顺序播放
        public static final int MODE_LOOP = 3;// 循环播放

        public static final int STATE_WAIT = 0;// 等待状态
        public static final int STATE_PLAY = 1;// 播放状态
        public static final int STATE_PAUSE = 2;// 暂停状态
        public static final int STATE_STOP = 3;// 停止状态
        public static final int STATE_CONTINUE = 4;// 继续播放状态
        public static final int STATE_PRE = 5;// 上一首
        public static final int STATE_NEXT = 6;// 下一首

        //用于传递参数
        public static final String PLAYER_WHERE = "where";
        public static final String PLAYER_POSITION = "position";
        public static final String PLAYER_LIST = "musicList";
        public static final String SEEKBAR_PROGRESS = "progress";
        public static final String LOCAL_ARTIST = "artist";
        public static final String LOCAL_ALBUM = "album";
    }

    public class reuquestCode{
        public static final int Request_get_music=0;
    }

    public class resultCode{
        public static final int Result_Success=1;
        public static final int Result_Failt=1;
    }

    public class BroadCast{
        // 点击了播放/暂停键的时候发这个action的广播
        public static final String ACTION_PLAY_BUTTON = "com.wangyan.service.ACTION_PLAY_BUTTON";
        // 在点击了播放列表的时候发这个action的广播
        public static final String ACTION_PLAY_ITEM = "com.wangyan.service.ACTION_PLAY_ITEM";
        // 在点击了播放列表的时候发这个action的广播
        public static final String ACTION_PLAY_INTERNET = "com.wangyan.service.ACTION_PLAY_INTERNET";
        // 定义上一首，下一首action
        public static final String ACTION_PLAY_PREVIOUS = "com.wangyan.service.ACTION_PLAY_PREVIOUS";
        public static final String ACTION_PLAY_NEXT = "com.wangyan.service.ACTION_PLAY_NEXT";
        // 更改播放模式mode的action
        public static final String ACTION_MODE = "com.wangyan.service.ACTION_MODE";
        // seekbar进度更改的action
        public static final String ACTION_SEEKBAR = "com.wangyan.service.ACTION_SEEKBAR";
    }
}
