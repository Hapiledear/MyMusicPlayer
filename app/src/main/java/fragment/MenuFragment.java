package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.example.administrator.mymusicplayer.R;

import constant.Setting;

/**
 * Created by Administrator on 2016/8/3.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    private LinearLayout my_music,online_music,setting,setting_set,setting_wifi,about,quit;
    private ToggleButton setting_wifi_toggle;

    private SwitchFragmentListener mSwitchFragmentListener;




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("MenuFragment", "onActivityCreated");

        mSwitchFragmentListener= (SwitchFragmentListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("MenuFragment", "onCreateView");
        View view=inflater.inflate(R.layout.fragment_menu,null);

        init(view);
        setClick();
        return view;
    }


    private void setClick() {
        my_music.setOnClickListener(this);
        online_music.setOnClickListener(this);
        setting.setOnClickListener(this);

        setting_wifi_toggle.setOnClickListener(this);
        about.setOnClickListener(this);
        quit.setOnClickListener(this);
    }

    private void init(View view) {

        my_music= (LinearLayout) view.findViewById(R.id.slide_menu_my_music);

        online_music= (LinearLayout) view.findViewById(R.id.slide_menu_online_music);

        setting= (LinearLayout) view.findViewById(R.id.slide_menu_setting);
        setting_set= (LinearLayout) view.findViewById(R.id.slide_menu_setting_set);
        setting_wifi= (LinearLayout) view.findViewById(R.id.slide_menu_setting_wifi);

        setting_wifi_toggle= (ToggleButton) view.findViewById(R.id.setting_wifi_toggle);
        setWifiPermision(Setting.only_wifi_connect_internet);

        about= (LinearLayout) view.findViewById(R.id.slide_menu_about);

        quit= (LinearLayout) view.findViewById(R.id.slide_menu_quit);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.slide_menu_my_music:
                //切换至主界面
                Log.d("MenuFragment", "my_music");
                mSwitchFragmentListener.switchToMain();

                break;
            case R.id.slide_menu_online_music:
                //切换至在线音乐界面
                Log.d("MenuFragment", "online_music");
                mSwitchFragmentListener.switchToOnline();
                break;
            case R.id.slide_menu_setting:
                //显示or隐藏设置项
                switchMenu();
                Log.d("MenuFragment", "setting");
                break;
            case R.id.setting_wifi_toggle:
                Log.d("MenuFragment", "setting_wifi_toggle");
                Setting.only_wifi_connect_internet=setting_wifi_toggle.isChecked();
                break;
            case R.id.slide_menu_about:
                Log.d("MenuFragment", "about");
                mSwitchFragmentListener.switchToAbout();
                break;
            case R.id.slide_menu_quit:
                Log.d("MenuFragment", "quit");
                getActivity().finish();
                break;
        }

    }

    private void switchMenu() {
        if (setting_set.isShown()){
            setting_set.setVisibility(View.GONE);
        }else {
            setting_set.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 外部调用接口，设置 是否仅wifi下联网 按钮
     * @param permit
     */
    public void setWifiPermision(boolean permit){
        setting_wifi_toggle.setChecked(permit);
    }


}
