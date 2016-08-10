package adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/4.
 */
public class LocalListAdapter extends BaseAdapter {

    // 上下文
    private Context context;
    // 数据源
    private Cursor cursor;
    // 正在播放的歌曲位置
    private int playPosition = -1;


    public LocalListAdapter(Context context, Cursor cursor) {
        this.context=context;
        this.cursor=cursor;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: 2016/8/4 音乐列表的构建
        TextView textView=new TextView(context);
        textView.setText(position+1+"");
        return textView;
    }

    private class ViewHolder{

    }


    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
