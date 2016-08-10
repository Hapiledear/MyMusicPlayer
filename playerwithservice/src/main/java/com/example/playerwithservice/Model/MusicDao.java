package com.example.playerwithservice.Model;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;


import com.example.playerwithservice.Bean.MusicBean;
import com.example.playerwithservice.R;
import com.example.playerwithservice.constant.Setting;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MusicDao {

    private Context context;
    private DbUtils db;


    public MusicDao(Context context) {
        this.context = context;

        db=DbUtils.create(context,context.getString(R.string.app_name));
    }

    public boolean reflashMusicInfo(){

        List<MusicBean> results = scanMusicInfoFromSystem();
        if (null == results){
            return false;
        }else {
            Log.d("MusicDao", "results:" + results.toString());
            addOrUpdateMusics(results);
            return true;
        }
    }

    /**
     * 扫描系统目录下的音乐文件
     * @return
     */
    private List<MusicBean> scanMusicInfoFromSystem(){

         List<MusicBean> music_info=new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor == null){
//            Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
            Log.d("MusicDao", Setting.FAIL);
            return null;
        }

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            int size = (int) cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

            if (artist.isEmpty()){
                artist="未知艺术家";
            }
            MusicBean musicBean=new MusicBean(id,title,artist,url,duration,size,album);
            music_info.add(musicBean);
            Log.v("DetailActivity",musicBean.toString());
        }
        return music_info;
    }

    /**
     * 添加/更新自己目录下的音乐信息，保持与系统的同步
     * @param musicList
     */
    public void addOrUpdateMusics(List<MusicBean> musicList){
        try {
            db.saveOrUpdateAll(musicList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询艺术家的种类
     * 返回一个map。key值为artist名称，value值为数量
     * 此外，还有flag标志位。
     * @return
     */
    public Map<String,String> getArtists(){
        Map<String,String> results=new HashMap<>();
        List<DbModel>dbModels = null;
        try {
          dbModels = db.findDbModelAll(Selector.from(MusicBean.class).groupBy("artist").select("artist","count(artist)"));
        } catch (DbException e) {
            e.printStackTrace();
        }
        //如果为空，则向数组中加入 失败 标志符
        if (dbModels.isEmpty()){
            results.put(Setting.RESULT,Setting.FAIL);
            return results;
        }

        for (int i=0;i<dbModels.size();i++){
            String artist=dbModels.get(i).getString(context.getString(R.string.music_artist));
            String count=dbModels.get(i).getInt("count(artist)")+"";

            results.put(artist,count);
        }
        results.put(Setting.RESULT,Setting.SUCCESS);

        return results;
    }

    public List<MusicBean> getAllMusics(){
        List<MusicBean> results=null;
        try {
           results= db.findAll(Selector.from(MusicBean.class));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return results;

    }

}
