package adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/6.
 */
public class AlbumAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> album;

    private static Map<Integer,List<String>> album_musics_map;
    /**
     * 已经展开的菜单项集合
     */
    private static List<String> expand_groups;

    public AlbumAdapter(Context context) {
        this.context = context;

        album=new ArrayList<>();
        album_musics_map=new HashMap<>();
        expand_groups=new ArrayList<>();
    }

    public void setAlbum(List<String> album) {
        this.album = album;

        notifyDataSetChanged();
    }

    public void notifactExpandGroup(int expand_group_Position, List<String> album_musics ){
        Log.d("AlbumAdapter", "notifactExpandGroup");
        expand_groups.add(expand_group_Position+"");
        album_musics_map.put(expand_group_Position,album_musics);

        notifyDataSetChanged();

    }

    public void notifactCollaspGroup(int collasp_group_id){
        Log.d("AlbumAdapter", "notifactCollaspGroup");
        expand_groups.remove(collasp_group_id+"");
        album_musics_map.remove(collasp_group_id);

        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return album == null ? 0 : album.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (expand_groups.contains(groupPosition+"")){
            return album_musics_map.get(groupPosition).size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        TextView textView=new TextView(context);
        textView.setTextSize(30);
        textView.setText(album.get(groupPosition));

        return textView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //视图与localmusic布局相同

        if (expand_groups.contains(groupPosition+"")){

            TextView textView=new TextView(context);
            textView.setTextSize(20);
            textView.setTextColor(Color.RED);
            textView.setText(album_musics_map.get(groupPosition).get(childPosition));

            return textView;
        }else {
            return null;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
