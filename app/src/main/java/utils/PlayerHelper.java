package utils;

import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;


import constant.Setting;
import service.PlayService;

/**
 * 播放歌曲帮助类
 * 
 * @author Wangyan
 * 
 */
public class PlayerHelper {
	/**
	 * 单例模式，让MediaPlayer对象只声明一次，多次调用。
	 */
	private static MediaPlayer myMedia = getMyMedia();

	private static MediaPlayer getMyMedia() {
		if (myMedia == null) {
			myMedia = new MediaPlayer();
		}
		return myMedia;
	}

	public void playInternet(Context context, Uri uri) {
		myMedia.reset();
		myMedia.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			myMedia.setDataSource(context, uri);
			myMedia.prepare();
			myMedia.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 播放函数
	 */
	public void play(String path) {
		try {
			myMedia.reset();
			myMedia.setAudioStreamType(AudioManager.STREAM_MUSIC);
			myMedia.setDataSource(path);
			myMedia.prepare();
			myMedia.start();
			myMedia.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					// 歌曲播放完毕，根据播放模式选择下一首播放歌曲的position
					// 播放模式在service中存放
					// 歌曲播放列表和位置都在service中，在这直接更改service中的position和state
					switch (PlayService.mode) {
					// 单曲循环
					case Setting.Player.MODE_SINGLE:
						myMedia.setLooping(true);
						break;
					// 全部循环
					case Setting.Player.MODE_LOOP:
						if (PlayService.servicePosition == PlayService.serviceMusicList
								.size() - 1) {
							PlayService.servicePosition = 0;
						} else {
							PlayService.servicePosition++;
						}
						PlayService.state = Setting.Player.STATE_PLAY;
						break;
					// 随机播放
					case Setting.Player.MODE_RANDOM:
						Random random = new Random();
						int p = PlayService.servicePosition;
						while (true) {
							PlayService.servicePosition = random
									.nextInt(PlayService.serviceMusicList
											.size());
							if (p != PlayService.servicePosition) {
								PlayService.state = Setting.Player.STATE_PLAY;
								break;
							}
						}
						break;
					// 顺序播放
					case Setting.Player.MODE_ORDER:
						if (PlayService.servicePosition == PlayService.serviceMusicList
								.size() - 1) {
							PlayService.state = Setting.Player.STATE_STOP;
						} else {
							PlayService.servicePosition++;
							PlayService.state = Setting.Player.STATE_PLAY;
						}
						break;
					}
					PlayService.stateChange = true;
				}
			});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 暂停函数
	 */
	public void pause() {
		myMedia.pause();
	}

	/**
	 * 歌曲继续播放
	 */
	public void continuePlay() {
		myMedia.start();// 歌曲继续播放
	}

	/**
	 * 歌曲停止
	 */
	public void stop() {
		myMedia.stop();// 歌曲停止
	}

	/**
	 * 得到歌曲当前播放位置
	 * 
	 * @return int 歌曲时长
	 */
	public int getPlayCurrentTime() {
		return myMedia.getCurrentPosition();
	}

	/**
	 * 得到歌曲时长
	 * 
	 * @return int 歌曲时长
	 */
	public int getPlayDuration() {
		return myMedia.getDuration();
	}

	/**
	 * 指定播放位置
	 */
	public void seekToMusic(int seek) {
		myMedia.seekTo(seek);// 指定位置
		myMedia.start();// 开始播放
	}

	/**
	 * 判断当前是否在播放
	 * 
	 * @return
	 */
	public Boolean isPlaying() {
		return myMedia.isPlaying();
	}

}
