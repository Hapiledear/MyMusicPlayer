package activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import adapter.ArtistAdapter;
import constant.Setting;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ArtistActivity extends Activity implements View.OnClickListener{

    private ImageButton actionbar_navigation,actionbar_button;
    private TextView actionbar_title;

    private ImageView artist_nothing_img;

    private ExpandableListView artist_expandable_listView;
    private ArtistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        init();
        initData();
        initView();
        setListener();
    }

    private void setListener() {
        actionbar_navigation.setOnClickListener(this);

        artist_expandable_listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                adapter.notifactCollaspGroup(groupPosition);
            }
        });

        artist_expandable_listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                // TODO: 2016/8/5 查询并加载子项数据
                List<String> artist_musics=new ArrayList<String>();
                for (int i=0;i<groupPosition+1;i++){
                    artist_musics.add(i+"");
                }

                adapter.notifactExpandGroup(groupPosition,artist_musics);
            }
        });

        artist_expandable_listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d("ArtistActivity", "groupPosition:childPosition:" + groupPosition +":" +childPosition);
                Intent intent=new Intent();

                intent.putExtra("position",childPosition);
                setResult(Setting.resultCode.Result_Success,intent);
                finish();
                return false;
            }
        });
        //当设置了group展开或崩坏时，该方法无效
//        artist_expandable_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("ArtistActivity", "position:" + position);
//            }
//        });
    }

    private void initData() {
        // TODO: 2016/8/4 加载数据
//        artist_nothing_img.setVisibility(View.VISIBLE);
        List<String> artist=new ArrayList<>();
        for (int i=0;i<10;i++){
            artist.add(i+"");
        }
        adapter.setArtists(artist);
    }

    private void initView() {
        actionbar_navigation.setImageResource(R.drawable.actionbar_back);
        actionbar_button.setImageResource(R.drawable.actionbar_scan);
        actionbar_title.setText(R.string.artist);
    }

    private void init() {
        actionbar_navigation= (ImageButton) findViewById(R.id.actionbar_navigation);
        actionbar_button= (ImageButton) findViewById(R.id.actionbar_button);
        actionbar_title= (TextView) findViewById(R.id.actionbar_title);

        artist_nothing_img= (ImageView) findViewById(R.id.artist_nothing_img);

        artist_expandable_listView= (ExpandableListView) findViewById(R.id.expandable_listView);
        adapter=new ArtistAdapter(ArtistActivity.this);
        artist_expandable_listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionbar_navigation:
                finish();
                break;
        }
    }
}
