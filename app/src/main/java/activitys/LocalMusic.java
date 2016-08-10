package activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymusicplayer.R;

import adapter.LocalListAdapter;
import constant.Setting;

/**
 * Created by Administrator on 2016/8/4.
 */
public class LocalMusic extends Activity implements View.OnClickListener {

    private ImageButton actionbar_navigation, actionbar_button;
    private TextView actionbar_title;

    private ImageView local_nothing_img;
    private ListView local_listview;

    private LocalListAdapter adapter;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localmucis);

        init();
        initData();
        initView();
        setListener();
    }

    private void initView() {
        actionbar_navigation.setImageResource(R.drawable.actionbar_back);
        actionbar_button.setImageResource(R.drawable.actionbar_scan);
        actionbar_title.setText(R.string.local_music);

        actionbar_button.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        actionbar_navigation.setOnClickListener(this);
        actionbar_button.setOnClickListener(this);

        local_listview.setOnItemClickListener(new ListItemClick());
//        local_listview.setOnItemLongClickListener(new ListItemLongClick());

        registerForContextMenu(local_listview);
    }

    private void initData() {
        // TODO: 2016/8/4 开启线程，获取数据
        //获取失败，显示图片
//        local_nothing_img.setVisibility(View.VISIBLE);
    }


    private void init() {
        intent=getIntent();

        actionbar_navigation = (ImageButton) findViewById(R.id.actionbar_navigation);
        actionbar_button = (ImageButton) findViewById(R.id.actionbar_button);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);

        local_nothing_img = (ImageView) findViewById(R.id.local_nothing_img);
        local_listview = (ListView) findViewById(R.id.local_listview);


        adapter = new LocalListAdapter(LocalMusic.this, null);
        local_listview.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_navigation:
                finish();
                break;
            case R.id.actionbar_button:
                //调用查询线程
                Toast.makeText(this, "数据查询中...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private class ListItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("ListItemClick", "position:" + position);
            //返回信息
            intent.putExtra("index",position);
            setResult(Setting.resultCode.Result_Success,intent);
            finish();
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        Log.d("LocalMusic", "menuInfo:" + menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.localmusic_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.favort:
                Log.d("LocalMusic", "favort");
                // TODO: 2016/8/5 数据操作，将该项写入’喜欢‘数据库
                Log.d("LocalMusic", "info.position:" + info.position);
                return true;
            case R.id.more_info:
                //一个popupwindow显示详细信息
                Log.d("LocalMusic", "more_info");

                View view=View.inflate(LocalMusic.this,R.layout.more_info_layout,null);
                Toast toast=new Toast(LocalMusic.this);
                toast.setView(view);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();

                return true;
            case R.id.delete:
                Log.d("LocalMusic", "delete");
                //向数组中移除该项，并通知更新
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

}
