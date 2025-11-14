package com.plovdev.pronviewer.httpquering;

import com.plovdev.pronviewer.models.ModelInfo;
import com.plovdev.pronviewer.models.PornCard;
import com.plovdev.pronviewer.models.VideoInfo;

import java.util.List;

public interface PornParser {
    List<PornCard> getAll(String html);
    List<Category> getCategories(String html);
    List<ModelInfo> getModels(String html);
    VideoInfo parseVideo(String url);
}