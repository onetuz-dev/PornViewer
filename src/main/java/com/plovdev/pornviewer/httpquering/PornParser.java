package com.plovdev.pornviewer.httpquering;

import com.plovdev.pornviewer.models.ModelInfo;
import com.plovdev.pornviewer.models.PornCard;
import com.plovdev.pornviewer.models.VideoInfo;

import java.util.List;

public interface PornParser {
    List<PornCard> getAll(String html);
    List<Category> getCategories(String html);
    List<ModelInfo> getModels(String html);
    VideoInfo parseVideo(String url);
}