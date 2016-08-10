package activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;

import adapter.AlbumAdapter;
import constant.Setting;

/**
 * Created by Administrator on 2016/8/4.
 */
public class AlbumActivity extends Activity implements View.OnClickListener{

    private ImageButton actionbar_navigation,actionbar_button;
    private TextView actionbar_title;

    private ImageView album_nothing_img;

    private ExpandableListView expandableListView;
    private AlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        init();
        initData();
        initView();
        setListener();
    }

    private void setListener() {
        actionbar_navigation.setOnClickListener(this);
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                adapter.notifactCollaspGroup(groupPosition);
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
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

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d("AlbumActivity", "groupPosition:childPosition:" + groupPosition +":" +childPosition);
                Intent intent=new Intent();
                intent.putExtra("position",childPosition);
                setResult(Setting.resultCode.Result_Success,intent);
                finish();
                return false;
            }
        });
    }

    private void initData() {
        // TODO: 2016/8/4 加载数据
//        album_nothing_img.setVisibility(View.VISIBLE);
        List<String> artist=new ArrayList<>();
        for (int i=0;i<10;i++){
            artist.add(i+"");
        }
        adapter.setAlbum(artist);

    }

    private void initView() {
        actionbar_navigation.setImageResource(R.drawable.actionbar_back);
        actionbar_button.setImageResource(R.drawable.actionbar_scan);
        actionbar_title.setText(R.string.album);
    }

    private void init() {
        actionbar_navigation= (ImageButton) findViewById(R.id.actionbar_navigation);
        actionbar_button= (ImageButton) findViewById(R.id.actionbar_button);
        actionbar_title= (TextView) findViewById(R.id.actionbar_title);

        album_nothing_img= (ImageView) findViewById(R.id.album_nothing_img);

        expandableListView= (ExpandableListView) findViewById(R.id.expandable_listView);
        adapter=new AlbumAdapter(AlbumActivity.this);
        expandableListView.setAdapter(adapter);



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
