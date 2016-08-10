package service;

import java.util.List;

import bean.MusicInfo;

/**
 * Created by Administrator on 2016/8/5.
 */
public interface OnPlayerStateChangeListener {
    void onStateChange(int state, int mode, List<MusicInfo> musicList,
                       int position);
}
