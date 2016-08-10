package fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymusicplayer.R;

/**
 * Created by Administrator on 2016/8/4.
 * 在线音乐界面
 */
public class OnlineMusicFragment extends Fragment implements View.OnClickListener{

    private ImageButton actionbar_navigation,actionbar_button;
    private TextView actionbar_title;

    public SwitchFragmentListener mSwitchFragmentListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwitchFragmentListener= (SwitchFragmentListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_oline_music,null);
        // TODO: 2016/8/4 界面初始化
        init(view);

        return view;
    }

    private void init(View view) {
        actionbar_navigation= (ImageButton) view.findViewById(R.id.actionbar_navigation);
        actionbar_button= (ImageButton) view.findViewById(R.id.actionbar_button);
        actionbar_title= (TextView) view.findViewById(R.id.actionbar_title);

        actionbar_navigation.setImageResource(R.drawable.actionbar_menu);
        actionbar_button.setImageResource(R.drawable.actionbar_more);
        actionbar_title.setText(R.string.online_music);

        actionbar_navigation.setOnClickListener(this);
        actionbar_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionbar_navigation:
                mSwitchFragmentListener.openMenu();
                break;
            case R.id.actionbar_button:
                //调用查询线程

                break;
        }
    }
}
