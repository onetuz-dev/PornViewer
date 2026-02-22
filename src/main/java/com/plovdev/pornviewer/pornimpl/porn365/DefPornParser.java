package com.plovdev.pornviewer.pornimpl.porn365;

import com.plovdev.pornviewer.databases.FavoriteVideos;
import com.plovdev.pornviewer.httpquering.Category;
import com.plovdev.pornviewer.httpquering.PornParser;
import com.plovdev.pornviewer.httpquering.defimpl.PBPornHandler;
import com.plovdev.pornviewer.models.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DefPornParser implements PornParser {
    @Override
    public List<PornCard> getAll(String html) {
        Document doc = Jsoup.parse(html);
        List<PornCard> cards = new ArrayList<>();
        try (ExecutorService service = Executors.newFixedThreadPool(10)) {
            List<Future<PornCard>> futures = new ArrayList<>();

            Elements elements = doc.select("li.video_block");
            elements.stream().limit(45).forEach(e -> {
                Future<PornCard> future = service.submit(() -> parseVideoBlock(e));
                futures.add(future);
            });

            futures.forEach(f -> {
                try {
                    PornCard card = f.get();
                    cards.add(card);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cards;
    }

    private PornCard parseVideoBlock(Element videoElement) {
        VideoCard pornCard = new VideoCard();

        pornCard.setCardId(Integer.parseInt(videoElement.id())); // "45894"

        // 2. Ссылка на страницу видео
        Element link = videoElement.selectFirst("a.image");
        pornCard.setUrl(link.attr("abs:href"));

        // 3. Ссылка на превью (thumbnail)
        Element img = link.selectFirst("img");
        pornCard.setPic(img.attr("abs:src"));

        // 4. Название видео
        Element title = link.selectFirst("p");
        pornCard.setTitle(title.text());

        // 5. Длительность
        Element duration = videoElement.selectFirst("span.duration");
        pornCard.setDuration(duration.text()); // "18:53"

        // 6. Количество просмотров
        Element views = videoElement.selectFirst("span.video_views");
        pornCard.setViews(Integer.parseInt(views.text().replace(",", ""))); // "826,230" -> 826230

        // 7. Рейтинг
        Element rating = videoElement.selectFirst("span.mini-rating");
        pornCard.setRating(rating.text()); // "83%" -> 0.83f

        long cardId = pornCard.getCardId();
        for (Integer vc : FavoriteVideos.getAllId()) {
            if (vc == cardId) {
                pornCard.setFavorite(true);
                break;
            }
        }

        return pornCard;
    }

    @Override
    public List<Category> getCategories(String html) {
        List<Category> categories = new ArrayList<>();
        try (ExecutorService service = Executors.newFixedThreadPool(10)) {

            Document doc = Jsoup.parse(html);

            Elements elements = doc.select("div ul.top-menu li a[href]");
            elements.forEach(e -> service.submit(() -> {
                String key = e.text().trim();
                String value = e.attr("abs:href");
                categories.add(new Category(key, value));
            }));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }

    @Override
    public VideoInfo parseVideo(String videoUrl) {
        VideoInfo info = new VideoInfo();

        try {
            PBPornHandler handler = new PBPornHandler();
            String html = handler.requestPorn(videoUrl);
            Document doc = Jsoup.parse(html);
            Map<String, String> links = new HashMap<>();
            Elements urls = doc.select("div.quality_chooser a[href]");
            urls.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
                links.put(key, url);
            });
            info.setUrls(links);


            Map<String, String> categs = new HashMap<>();
            Elements categories = doc.select("div.video-categories a[href]");
            categories.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
                categs.put(key, url);
            });
            info.setCategories(categs);


            Map<String, String> mds = new HashMap<>();
            Elements models = doc.select("div.video-models a[href]");
            models.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
                mds.put(key, url);
            });
            info.setModels(mds);


            String title = doc.selectFirst("title").text();
            info.setTitle(title);

            String pageUrl = doc.select("link[rel=canonical]").attr("href");
            info.setUrl(pageUrl);

            int id = Integer.parseInt(pageUrl.substring(pageUrl.lastIndexOf('/')+1));
            info.setId(id);


            Elements descr = doc.select("div.story_desription");
            descr.forEach(e -> info.setDescription(e.text().trim()));

            Map<String, String> tgs = new HashMap<>();
            Elements tags = doc.select("div.video-tags a[href]");
            tags.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
                tgs.put(key, url);
            });
            info.setTags(tgs);

            Map<String, String> times = new HashMap<>();
            Elements timecodes = doc.select("div.video-tags span");
            timecodes.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
                times.put(key, url);
            });
            info.setTimeCodes(times);


            List<Comment> commentsList = new ArrayList<>();
            Elements comments = doc.select("div.video-tags span");
            comments.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
            });
            info.setComments(commentsList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return info;
    }

    @Override
    public List<ModelInfo> getModels(String html) {
        Document doc = Jsoup.parse(html);

        List<ModelInfo> cards = new ArrayList<>();
        try (ExecutorService service = Executors.newFixedThreadPool(20)) {
            List<Future<ModelInfo>> futures = new ArrayList<>();

            Elements elements = doc.select("div.item_model");
            elements.stream().limit(45).forEach(e -> {
                Future<ModelInfo> future = service.submit(() -> parseModelBlock(e));
                futures.add(future);
            });

            futures.forEach(f -> {
                try {
                    ModelInfo card = f.get();
                    cards.add(card);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cards;
    }

    private ModelInfo parseModelBlock(Element videoElement) {
        ModelInfo info = new ModelInfo();

        // 2. Ссылка на страницу видео
        Element link = videoElement.selectFirst("a[href]");
        info.setUrl(link.attr("abs:href"));

        Element country = link.selectFirst("span[class]");
        String flag = country.attr("class");
        info.setCountry(null);
        if (flag.contains("-")) {
            flag = flag.substring(flag.lastIndexOf('-')+1);
            info.setCountry(flag.toUpperCase());
        }

        // 3. Ссылка на превью (thumbnail)
        Element img = videoElement.selectFirst("img");
        info.setAvatar(img.attr("abs:src"));

        Element videos = videoElement.selectFirst("span.cnt_span");
        info.setVideos(Integer.parseInt(videos.text()));

        Element nameRu = videoElement.selectFirst(".model_rus_name");
        info.setRusName(nameRu.text());

        Element nameEn = videoElement.selectFirst(".model_eng_name");
        info.setEngName(nameEn.text());

        return info;
    }
}