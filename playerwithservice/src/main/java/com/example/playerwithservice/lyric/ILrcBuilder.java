package com.example.playerwithservice.lyric;

import java.util.List;


public interface ILrcBuilder {
    List<LrcRow> getLrcRows(String rawLrc);
}
