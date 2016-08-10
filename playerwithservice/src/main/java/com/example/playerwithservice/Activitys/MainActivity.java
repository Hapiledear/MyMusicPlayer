package com.example.playerwithservice.Activitys;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.playerwithservice.Bean.MusicBean;
import com.example.playerwithservice.Fragments.AllMusicFragment;
import com.example.playerwithservice.Model.MusicDao;
import com.example.playerwithservice.R;
import com.example.playerwithservice.Service.playerService;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MainActivity extends Activity {

    @InjectView(R.id.rb_allMusic)
    RadioButton rbAllMusic;
    @InjectView(R.id.rb_album)
    RadioButton rbAlbum;
    @InjectView(R.id.rb_artist)
    RadioButton rbArtist;
    @InjectView(R.id.radioGroup)
    RadioGroup radioGroup;
    @InjectView(R.id.show_frameLayout)
    FrameLayout showFrameLayout;

    private FragmentManager fragmentManager;
    private AllMusicFragment musicFragment;
    private List<Fragment> fragments=new ArrayList<>();

    private List<MusicBean> music_info=new ArrayList<>();

    private Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        musicFragment=new AllMusicFragment();
        fragments.add(musicFragment);

        //开启线程，加载数据
         GetDataThread thread=new GetDataThread();
         thread.run();

        fragmentManager=getFragmentManager();
        //设置默认显示，为全部音乐。
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.show_frameLayout,musicFragment);
        transaction.commit();

        //启动播放服务
        service=new Intent(MainActivity.this,playerService.class);
        startService(service);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                switch (checkedId){
                    case R.id.rb_allMusic:
                        break;
                    case R.id.rb_album:
                        break;
                    case R.id.rb_artist:
                        break;

                }
            }
        });
    }



    /**
     * 加载数据线程，更新应用数据库中的数据，与系统的music数据库保持同步
     */
    private class GetDataThread implements Runnable{

        @Override
        public void run() {
            Log.d("getData", "执行线程");

            MusicDao dao=new MusicDao(MainActivity.this);
           boolean result=  dao.reflashMusicInfo();

            Log.d("getData", "线程执行完毕");
            Log.d("getData", "result:" + result);
        }
    }


}
