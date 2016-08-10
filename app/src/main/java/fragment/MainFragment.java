package fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymusicplayer.R;

import java.util.List;

import activitys.AlbumActivity;
import activitys.ArtistActivity;
import activitys.FavorActivity;
import activitys.LocalMusic;
import activitys.PlayerActivity;
import bean.MusicInfo;
import constant.Setting;
import service.OnPlayerStateChangeListener;

/**
 * Created by Administrator on 2016/8/3.
 */
public class MainFragment extends Fragment implements View.OnClickListener{

    public ImageButton main_miniplayer_play,main_miniplayer_next;

    public LinearLayout main_local_music_item,main_folder_item,main_artist_item,
            main_album_item,main_download_item,main_favor_item,main_playlist_item;

    public TextView main_local_music_item_text,main_artist_text,main_album_text,
            main_download_text,main_favor_text,main_miniplayer_song,main_miniplayer_artist;
    public RelativeLayout miniplayer;


    public SwitchFragmentListener mSwitchFragmentListener;

    private ImageButton actionbar_navigation,actionbar_button;
    private TextView actionbar_title;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwitchFragmentListener= (SwitchFragmentListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main,null);

        init(view);
        setListener();
        return view;
    }

    private void setListener() {
        actionbar_navigation.setOnClickListener(this);
        main_miniplayer_play.setOnClickListener(this);
        main_miniplayer_next.setOnClickListener(this);

        main_local_music_item.setOnClickListener(this);
        main_folder_item.setOnClickListener(this);
        main_artist_item.setOnClickListener(this);
        main_album_item.setOnClickListener(this);
        main_download_item.setOnClickListener(this);
        main_favor_item.setOnClickListener(this);
        main_playlist_item.setOnClickListener(this);

        miniplayer.setOnClickListener(this);

        // 监听器改变UI
        OnPlayerStateChangeListener stateChangeListener = new OnPlayerStateChangeListener() {

            @Override
            public void onStateChange(int state, int mode,
                                      List<MusicInfo> musicList, int position) {
                // TODO Auto-generated method stub

                // 更改当前界面UI
                if (musicList != null) {

                } else {

                }
                switch (state) {
                    case Setting.Player.STATE_PLAY:
                        main_miniplayer_play.setImageResource(R.drawable.player_pause);
                        break;
                    case Setting.Player.STATE_CONTINUE:
                        main_miniplayer_play.setImageResource(R.drawable.player_pause);
                        break;
                    case Setting.Player.STATE_PAUSE:
                        main_miniplayer_play.setImageResource(R.drawable.player_play);
                        break;
                    case Setting.Player.STATE_STOP:
                        main_miniplayer_play.setImageResource(R.drawable.player_play);
                        break;
                }
            }
        };

    }

    private void init(View view) {
        actionbar_navigation= (ImageButton) view.findViewById(R.id.actionbar_navigation);
        actionbar_button= (ImageButton) view.findViewById(R.id.actionbar_button);
        actionbar_title= (TextView) view.findViewById(R.id.actionbar_title);

        actionbar_navigation.setImageResource(R.drawable.actionbar_menu);
        actionbar_button.setImageResource(R.drawable.actionbar_more);
        actionbar_title.setText(R.string.my_music);

        main_miniplayer_play= (ImageButton) view.findViewById(R.id.miniplayer_play);
        main_miniplayer_next= (ImageButton) view.findViewById(R.id.miniplayer_next);

        main_local_music_item= (LinearLayout) view.findViewById(R.id.main_local_music_item);
        main_folder_item= (LinearLayout) view.findViewById(R.id.main_folder_item);
        main_artist_item= (LinearLayout) view.findViewById(R.id.main_artist_item);
        main_album_item= (LinearLayout) view.findViewById(R.id.main_album_item);
        main_download_item= (LinearLayout) view.findViewById(R.id.main_download_item);
        main_favor_item= (LinearLayout) view.findViewById(R.id.main_favor_item);
        main_playlist_item= (LinearLayout) view.findViewById(R.id.main_playlist_item);

        main_local_music_item_text= (TextView) view.findViewById(R.id.main_local_music_text);
        main_artist_text= (TextView) view.findViewById(R.id.main_artist_text);
        main_album_text= (TextView) view.findViewById(R.id.main_album_text);
        main_download_text= (TextView) view.findViewById(R.id.main_download_text);
        main_favor_text= (TextView) view.findViewById(R.id.main_favor_text);

        main_miniplayer_song= (TextView) view.findViewById(R.id.miniplayer_song);
        main_miniplayer_artist= (TextView) view.findViewById(R.id.miniplayer_artist);

        miniplayer= (RelativeLayout) view.findViewById(R.id.miniplayer);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.actionbar_navigation:
                Log.d("MainFragment", "main_actionbar_menu click!");
                mSwitchFragmentListener.openMenu();
                break;
            case R.id.miniplayer_play:
                // TODO: 2016/8/4 发送广播，通知service播放/暂停当前音乐
                Log.d("MainFragment", "main_miniplayer_play click!");

                break;
            case R.id.miniplayer_next:
                // TODO: 2016/8/4 发生广播，通知service播放下一曲
                Log.d("MainFragment", "main_miniplayer_next click!");
                break;

            case R.id.main_local_music_item:
                intent.setClass(getActivity(),LocalMusic.class);
                startActivityForResult(intent,Setting.reuquestCode.Request_get_music);
//                startActivity(intent);
                Log.d("MainFragment", "main_local_music_item click!");
                break;
            case R.id.main_folder_item:
                Log.d("MainFragment", "main_folder_item click!");
                break;
            case R.id.main_artist_item:
                intent.setClass(getActivity(), ArtistActivity.class);
                startActivityForResult(intent,Setting.reuquestCode.Request_get_music);
                Log.d("MainFragment", "main_artist_item click!");
                break;
            case R.id.main_album_item:
                intent.setClass(getActivity(), AlbumActivity.class);
                startActivityForResult(intent,Setting.reuquestCode.Request_get_music);
                Log.d("MainFragment", "main_album_item click!");
                break;
            case R.id.main_download_item:
                Log.d("MainFragment", "main_download_item click!");
                break;
            case R.id.main_favor_item:
                intent.setClass(getActivity(), FavorActivity.class);
                startActivityForResult(intent,Setting.reuquestCode.Request_get_music);
                Log.d("MainFragment", "main_favor_item click!");
                break;
            case R.id.main_playlist_item:
                Log.d("MainFragment", "main_playlist_item click!");
                break;
            case R.id.miniplayer:
                Log.d("MainFragment", "进入详细播放");
                intent.setClass(getActivity(), PlayerActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int index;
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == Setting.reuquestCode.Request_get_music && resultCode == Setting.resultCode.Result_Success){
           index=  data.getIntExtra("index",0);
           Log.d("MainFragment", "index:" + index);
       }

        // TODO: 2016/8/6 发送广播，切换歌曲


    }
}
