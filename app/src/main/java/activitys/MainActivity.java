package activitys;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.administrator.mymusicplayer.R;

import java.util.List;

import bean.MusicInfo;
import constant.Setting;
import fragment.MainFragment;
import fragment.MenuFragment;
import fragment.OnlineMusicFragment;
import fragment.SwitchFragmentListener;
import service.OnPlayerStateChangeListener;
import service.PlayService;

public class MainActivity extends Activity implements SwitchFragmentListener{

    private FragmentManager mFragmentManager;

    private MenuFragment menuFragment;//菜单界面，静态加载

    private MainFragment mainFragment;//主界面
    private OnlineMusicFragment onlineFragment;//在线界面


    private SlidingPaneLayout mSlidingPaneLayout;
    private FrameLayout mFrameLayout;
    private Intent service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

      init();
      setFrameLayout(mainFragment);
        // 启动service
        service = new Intent(this, PlayService.class);
        startService(service);

        // TODO: 2016/8/4 加载设置

    }

    private void setFrameLayout(Fragment showFragment) {
        FragmentTransaction transaction= mFragmentManager.beginTransaction();
        transaction.replace(R.id.main_frameLayout,showFragment);
        transaction.commit();
    }

    private void init() {

        mSlidingPaneLayout= (SlidingPaneLayout) findViewById(R.id.sliding);
        mFrameLayout= (FrameLayout) findViewById(R.id.main_frameLayout);

        mFragmentManager=getFragmentManager();

        mainFragment=new MainFragment();
        onlineFragment=new OnlineMusicFragment();
    }

    @Override
    public void switchToOnline() {
        Log.d("MainActivity", "onlie 界面");
        setFrameLayout(onlineFragment);
        mSlidingPaneLayout.closePane();
    }

    @Override

    public void switchToMain() {
        Log.d("MainActivity", "Main");
        setFrameLayout(mainFragment);
        mSlidingPaneLayout.closePane();
    }

    @Override
    public void switchToAbout() {
        Log.d("MainActivity", "About");
        Intent intent=new Intent(MainActivity.this,AboutActivity.class);
        startActivity(intent);
        mSlidingPaneLayout.closePane();
    }

    @Override
    public void openMenu() {
        mSlidingPaneLayout.openPane();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // TODO: 2016/8/4 保存数据相关操作
    }


}
