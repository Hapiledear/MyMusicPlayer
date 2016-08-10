package com.example.playerwithservice.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.playerwithservice.Bean.MusicBean;
import com.example.playerwithservice.Model.MusicDao;
import com.example.playerwithservice.R;

import java.util.List;


import butterknife.InjectView;

/**
 * Created by Administrator on 2016/8/8.
 */
public class AllMusicFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView listView;

    private AllMusicAdapter adapter = new AllMusicAdapter();
    private List<MusicBean> musicList;

    private ImageView oldview;
    private ImageView show_nothing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_simple_listview, null);

        listView= (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        show_nothing= (ImageView) view.findViewById(R.id.img_nothing_show);
        show_nothing.setImageResource(R.drawable.error_nothing_app);
        listView.setEmptyView(show_nothing);

        listView.setOnItemClickListener(this);

        GetDataThread getData=new GetDataThread();
        getData.run();

        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView imageView = (ImageView) view.findViewById(R.id.img_isPlaying);
        imageView.setVisibility(View.VISIBLE);

        if (null == oldview) {
            oldview=imageView;
        } else {
            oldview.setVisibility(View.GONE);
            oldview=imageView;
        }



    }

    public void setMusicList(List<MusicBean> musicList) {

        Log.d("AllMusicFragment", "musicList:" + musicList.toString());
        //如果为空，则返回
        if (musicList.isEmpty() || musicList.contains(null)){
            Log.e("AllMusicFragment","传入的列表为空");
            return;
        }
        this.musicList = musicList;
        adapter.notifyDataSetChanged();
    }

    private class AllMusicAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return musicList == null ? 0 : musicList.size();
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

            ViewHolder viewHolder=null;
            if (convertView == null){
                convertView=View.inflate(getActivity(), R.layout.layout_music_item, null);

                viewHolder=new ViewHolder();
                viewHolder.txtTitle= (TextView) convertView.findViewById(R.id.txt_title);
                viewHolder.txtArtist= (TextView) convertView.findViewById(R.id.txt_artist);
                viewHolder.txtDuration= (TextView) convertView.findViewById(R.id.music_txt_duration);
                viewHolder.imgIsPlaying= (ImageView) convertView.findViewById(R.id.img_isPlaying);

                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }

            viewHolder.txtTitle.setText(musicList.get(position).getTitle());
            viewHolder.txtArtist.setText(musicList.get(position).getArtist());
            viewHolder.txtDuration.setText(musicList.get(position).getDuration()+"");

            return convertView;
        }

        private class ViewHolder {

            TextView txtTitle;

            TextView txtArtist;

            TextView txtDuration;

            ImageView imgIsPlaying;

        }
    }

    private class GetDataThread implements Runnable{

        @Override
        public void run() {
            MusicDao dao=new MusicDao(getActivity());
            setMusicList(dao.getAllMusics());
        }
    }
}
