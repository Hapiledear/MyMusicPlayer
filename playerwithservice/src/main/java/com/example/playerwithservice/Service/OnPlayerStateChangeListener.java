package com.example.playerwithservice.Service;

import com.example.playerwithservice.Bean.MusicBean;

import java.util.List;


public interface OnPlayerStateChangeListener {
	void onStateChange(int state, int mode, List<MusicBean> musicList,
					   int position);
}
