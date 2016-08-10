package com.example.playerwithservice.Utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

import com.example.playerwithservice.Service.playerService;

import java.io.IOException;
import java.util.Random;


/**
 * ���Ÿ���������
 * 
 * @author yanghuang
 * 
 */
public class PlayerHelper {
	/**
	 * ����ģʽ����MediaPlayer����ֻ����һ�Σ���ε��á�
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
	 * ���ź���
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
					// ����������ϣ����ݲ���ģʽѡ����һ�ײ��Ÿ�����position
					// ����ģʽ��service�д��
					// ���������б��λ�ö���service�У�����ֱ�Ӹ���service�е�position��state
                    switch (playerService.currentPlayMode) {
                        // ����ѭ��
                        case playerService.PlayMode.MODE_SINGLE:
                            myMedia.setLooping(true);
                            break;
                        // ȫ��ѭ��
                        case playerService.PlayMode.MODE_LOOP:
                            if (playerService.servicePosition == playerService.playlist_musicInfo
                                    .size() - 1) {
                                playerService.servicePosition = 0;
                            } else {
                                playerService.servicePosition++;
                            }
                            playerService.PlayState = playerService.StateMode.STATE_PLAY;
                            break;
                        // �������
                        case playerService.PlayMode.MODE_RANDOM:
                            Random random = new Random();
                            int p = playerService.servicePosition;
                            while (true) {
                                playerService.servicePosition = random
                                        .nextInt(playerService.playlist_musicInfo
                                                .size());
                                if (p != playerService.servicePosition) {
                                    playerService.PlayState = playerService.StateMode.STATE_PLAY;
                                    break;
                                }
                            }
                            break;
                        // ˳�򲥷�
                        case playerService.PlayMode.MODE_ORDER:
                            if (playerService.servicePosition == playerService.playlist_musicInfo
                                    .size() - 1) {
                                playerService.PlayState = playerService.StateMode.STATE_STOP;
                            } else {
                                playerService.servicePosition++;
                                playerService.PlayState = playerService.StateMode.STATE_PLAY;
                            }
                            break;
                    }
                    playerService.stateChange = true;
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
	 * ��ͣ����
	 */
	public void pause() {
		myMedia.pause();
	}

	/**
	 * ������������
	 */
	public void continuePlay() {
		myMedia.start();// ������������
	}

	/**
	 * ����ֹͣ
	 */
	public void stop() {
		myMedia.stop();// ����ֹͣ
	}

	/**
	 * �õ�������ǰ����λ��
	 * 
	 * @return int ����ʱ��
	 */
	public int getPlayCurrentTime() {
		return myMedia.getCurrentPosition();
	}

	/**
	 * �õ�����ʱ��
	 * 
	 * @return int ����ʱ��
	 */
	public int getPlayDuration() {
		return myMedia.getDuration();
	}

	/**
	 * ָ������λ��
	 */
	public void seekToMusic(int seek) {
		myMedia.seekTo(seek);// ָ��λ��
		myMedia.start();// ��ʼ����
	}

	/**
	 * �жϵ�ǰ�Ƿ��ڲ���
	 * 
	 * @return
	 */
	public Boolean isPlaying() {
		return myMedia.isPlaying();
	}

}
