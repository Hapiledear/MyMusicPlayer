package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/5.
 */
public class ArtistAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String>artists;

    private static Map<Integer,List<String>>artist_musics_map;
    /**
     * 已经展开的菜单项集合
     */
    private static List<String> expand_groups;



    public ArtistAdapter(Context context) {
        this.context = context;

        artists=new ArrayList<>();
        artist_musics_map=new HashMap<>();
        expand_groups=new ArrayList<>();
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;

        notifyDataSetChanged();
    }


    public void notifactExpandGroup(int expand_group_Position, List<String> artist_musics ){
        Log.d("ArtistAdapter", "notifactExpandGroup");
        expand_groups.add(expand_group_Position+"");
        artist_musics_map.put(expand_group_Position,artist_musics);

        notifyDataSetChanged();

    }

    public void notifactCollaspGroup(int collasp_group_id){
        Log.d("ArtistAdapter", "notifactCollaspGroup");
        expand_groups.remove(collasp_group_id+"");
        artist_musics_map.remove(collasp_group_id);

        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {

        return artists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (expand_groups.contains(groupPosition+"")){
            return artist_musics_map.get(groupPosition).size();
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
        textView.setText(artists.get(groupPosition));

        return textView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //视图与localmusic布局相同

        if (expand_groups.contains(groupPosition+"")){

            TextView textView=new TextView(context);
            textView.setTextSize(20);
            textView.setTextColor(Color.RED);
            textView.setText(artist_musics_map.get(groupPosition).get(childPosition));

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
