package com.example.playerwithservice.Bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/27.
 */
@Table(name = "music")
public class MusicBean implements Serializable,Parcelable {

    @Id(column = "id")
    @NoAutoIncrement
    private int id;
    @Column(column = "title",defaultValue = "未知歌名")
    private String title;
    @Column(column = "artist",defaultValue = "未知艺术家")
    private String artist;
    @Column(column = "album",defaultValue = "未知专辑")
    private String album;
    @Column(column = "url")
    private String url;
    @Column(column = "duration")
    private int duration;
    @Column(column = "size")
    private int size;

    public MusicBean(){

    }
    public MusicBean(int id, String title,String artist, String url, int duration, int size,String album) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.duration = duration;
        this.size = size;
        this.album=album;
    }

    protected MusicBean(Parcel in) {
        album=in.readString();
        id = in.readInt();
        title = in.readString();
        artist = in.readString();
        url = in.readString();
        duration = in.readInt();
        size = in.readInt();

    }

    public static final Creator<MusicBean> CREATOR = new Creator<MusicBean>() {
        @Override
        public MusicBean createFromParcel(Parcel in) {
            return new MusicBean(in);
        }

        @Override
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }
    };

    @Override
    public String toString() {
        return "MusicBean{" +
                "album='" + album + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", url='" + url + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(album);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(url);
        dest.writeInt(duration);
        dest.writeInt(size);
    }
}
