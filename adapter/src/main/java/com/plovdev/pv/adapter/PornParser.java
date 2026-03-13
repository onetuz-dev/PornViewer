package com.plovdev.pv.adapter;

import com.plovdev.pv.adapter.dto.CategoryInfo;
import com.plovdev.pv.adapter.dto.FullVideoInfo;
import com.plovdev.pv.adapter.dto.ModelInfo;
import com.plovdev.pv.adapter.dto.ShortVideoInfo;

import java.util.List;

public interface PornParser {
    List<ShortVideoInfo> parseVideos(String body);
    List<CategoryInfo> parseCategories(String body);
    List<ModelInfo> parseModels(String body);
    FullVideoInfo parseFullVideoInfo();
}