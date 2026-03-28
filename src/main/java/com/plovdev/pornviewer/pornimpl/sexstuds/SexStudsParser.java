package com.plovdev.pornviewer.pornimpl.sexstuds;

import com.plovdev.pornviewer.databases.FavoriteVideos;
import com.plovdev.pornviewer.httpquering.Category;
import com.plovdev.pornviewer.httpquering.PornParser;
import com.plovdev.pornviewer.httpquering.defimpl.PBPornHandler;
import com.plovdev.pornviewer.models.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SexStudsParser implements PornParser {
    private static final String ABS_URL = "https://sex-studentki.live";
    private static final Logger log = LoggerFactory.getLogger(SexStudsParser.class);

    @Override
    public List<VideoCard> getAllVideos(String html) {
        Document doc = Jsoup.parse(html);
        List<VideoCard> cards = new ArrayList<>();
        try (ExecutorService service = Executors.newFixedThreadPool(10)) {
            List<Future<VideoCard>> futures = new ArrayList<>();

            // Используем новый селектор для новых элементов
            Elements elements = doc.select("div.video.trailer");
            elements.stream().limit(45).forEach(e -> {
                Future<VideoCard> future = service.submit(() -> (VideoCard) parseVideoBlock(e));
                futures.add(future);
            });

            futures.forEach(f -> {
                try {
                    cards.add(f.get());
                } catch (Exception e) {
                    log.error("Errot parsing video card: ", e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге всех видео блоков", e);
        }
        return cards;
    }

    @Override
    public List<ModelCard> getAllModels(String html) {
        return List.of();
    }

    private PornCard parseVideoBlock(Element videoElement) {
        VideoCard pornCard = new VideoCard();

        // 1. ID карточки из атрибута id элемента
        String idStr = videoElement.id();
        if (!idStr.isEmpty()) {
            try {
                pornCard.setCardId(Integer.parseInt(idStr));
            } catch (NumberFormatException ex) {
                // Обработка случая, если id не является числом
                System.err.println("Невозможно преобразовать ID '" + idStr + "' в число.");
                pornCard.setCardId(-1); // или другое значение по умолчанию
            }
        } else {
            // Если id отсутствует, можно установить дефолтное значение или выбросить исключение
            System.err.println("ID не найден для элемента.");
            pornCard.setCardId(-1); // или бросить исключение
        }

        // 2. Ссылка на страницу видео
        Element link = videoElement.selectFirst("a");
        if (link != null) {
            // Используем abs:href для получения абсолютной ссылки
            String absoluteUrl = link.attr("href");
            pornCard.setUrl(buildAbsoluteUrl(absoluteUrl));
        } else {
            System.err.println("Ссылка на видео не найдена для элемента с ID: " + pornCard.getCardId());
            // Можно установить дефолтную или пустую ссылку
        }

        // 3. Ссылка на превью (thumbnail)
        Element img = videoElement.selectFirst("img.image"); // Более точный селектор
        if (img != null) {
            String absoluteImgUrl = img.attr("src");
            pornCard.setPic(buildAbsoluteUrl(absoluteImgUrl));
        } else {
            System.err.println("Изображение не найдено для элемента с ID: " + pornCard.getCardId());
        }

        // 4. Название видео (из div.title внутри h3)
        Element titleDiv = videoElement.selectFirst("h3 div.title");
        if (titleDiv != null) {
            String titleText = titleDiv.text();
            pornCard.setTitle(titleText);
        } else {
            System.err.println("Название видео не найдено для элемента с ID: " + pornCard.getCardId());
        }

        // 5. Длительность (в span.column-time > span)
        Element durationSpan = videoElement.selectFirst("span.column-time span");
        if (durationSpan != null) {
            String durationText = durationSpan.text();
            pornCard.setDuration(durationText);
        } else {
            System.err.println("Длительность не найдена для элемента с ID: " + pornCard.getCardId());
        }

        // 6. Количество просмотров (в span.colum-views > span)
        Element viewsSpan = videoElement.selectFirst("span.colum-views span");
        if (viewsSpan != null) {
            String viewsText = viewsSpan.text();
            // Убираем точки и пробелы, если нужно
            // Пример: "1.01M" -> "1010000" или "1.01 M" -> "1010000"
            // Это может быть сложно, так как формат может быть разным
            // Для простоты, предположим, что это число или число с "M" или "K"
            try {
                // Убираем пробелы и точки
                String cleanViews = viewsText.replaceAll("[\\s.]", "");
                if (cleanViews.endsWith("M")) {
                    // "1.01M" -> "1010000"
                    double value = Double.parseDouble(cleanViews.replace("M", ""));
                    int viewsInt = (int) (value * 1_000_000);
                    pornCard.setViews(viewsInt);
                } else if (cleanViews.endsWith("K")) {
                    // "1.01K" -> "1010"
                    double value = Double.parseDouble(cleanViews.replace("K", ""));
                    int viewsInt = (int) (value * 1_000);
                    pornCard.setViews(viewsInt);
                } else {
                    // Просто число
                    int viewsInt = Integer.parseInt(cleanViews);
                    pornCard.setViews(viewsInt);
                }
            } catch (NumberFormatException ex) {
                System.err.println("Невозможно преобразовать просмотры '" + viewsText + "' в число.");
                pornCard.setViews(0); // или другое значение по умолчанию
            }
        } else {
            System.err.println("Просмотры не найдены для элемента с ID: " + pornCard.getCardId());
        }
        pornCard.setRating("--");

        // 8. Рейтинг (отсутствует в примере, но оставлено для совместимости)
        // Если он будет доступен, добавь здесь парсинг

        // 9. Проверка на избранное (если FavoriteVideos.getAllId() возвращает список ID)
        long cardId = pornCard.getCardId();
        if (cardId != -1) { // Проверка, что ID корректный
            for (Integer vc : FavoriteVideos.getAllId()) {
                if (vc != null && vc == cardId) {
                    pornCard.setFavorite(true);
                    break;
                }
            }
        }

        return pornCard;
    }


    @Override
    public List<Category> getCategories(String html) {
        List<Category> categories = new ArrayList<>();
        try (ExecutorService service = Executors.newFixedThreadPool(10)) {

            Document doc = Jsoup.parse(html);

            Element submenuWrapper = doc.selectFirst(".submenu.wrapper");

            Elements elements = submenuWrapper.select("a.submenu-item.sleeping-mobile:not(.mobile-button)");
            elements.forEach(e -> service.submit(() -> {
                String key = e.text().trim();
                String value = e.attr("href");
                value = buildAbsoluteUrl(value);
                categories.add(new Category(key, value));
            }));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }

    @Override
    public List<ModelInfo> getModels(String html) {
        return List.of();
    }

    @Override
    public VideoInfo parseVideo(String videoUrl) {
        VideoInfo info = new VideoInfo();

        try {
            PBPornHandler handler = new PBPornHandler();
            String html = handler.requestPorn(videoUrl);
            Document doc = Jsoup.parse(html);

            // --- Извлечение URL ---
            Map<String, String> links = new HashMap<>();
            // Если нужно извлечь URL из <source> тега внутри <video>
            Element videoElement = doc.selectFirst("video");
            if (videoElement != null) {
                Elements sources = videoElement.select("source");
                for (Element source : sources) {
                    String src = source.attr("src");
                    if (!src.isEmpty()) {
                        // Предполагаем, что ключ - это качество, но если нет, можно использовать "source"
                        links.put("source", src);
                        break; // Берем первый источник, если нужен один
                    }
                }
            }
            info.setUrls(links);
            // --- Извлечение категорий ---
            Map<String, String> categs = new HashMap<>();
            // В новом сайте категории находятся в div.categories-page
            Elements categories = doc.select("div.categories-page .category a[href]");
            categories.forEach(e -> {
                String key = e.selectFirst(".label").text().trim(); // Извлекаем текст из .label
                String url = e.attr("href");
                if (!key.isEmpty()) {
                    categs.put(key, buildAbsoluteUrl(url));
                }
            });
            info.setCategories(categs);


            // --- Извлечение моделей ---
            Map<String, String> mds = new HashMap<>();
            Elements models = doc.select("div.tags a[href]");
            models.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
                if (!key.isEmpty()) {
                    mds.put(key, buildAbsoluteUrl(url));
                }
            });
            info.setModels(mds);


            // --- Извлечение заголовка ---
            String title = doc.selectFirst("h1.video-header h1").text();
            if (title.isEmpty()) {
                title = doc.selectFirst("title").text();
            }
            info.setTitle(title);

            // --- Извлечение URL страницы ---
            String pageUrl = doc.select("link[rel=canonical]").attr("href");
            info.setUrl(buildAbsoluteUrl(pageUrl));

            // --- Извлечение ID ---
            int id = -1; // Значение по умолчанию
            if (!pageUrl.isEmpty()) {
                // Извлекаем ID из URL
                // Пример: https://sex-studentki.live/video/443143-druzya-napoili-moloduyu-shlyuhu-i-vyebali-pyanoe-telo
                // ID = 443143
                try {
                    String[] parts = pageUrl.split("/");
                    if (parts.length > 0) {
                        String lastPart = parts[parts.length - 1];
                        // Извлекаем число из строки типа "443143-druzya-napoili..."
                        String idStr = lastPart.split("-")[0];
                        id = Integer.parseInt(idStr);
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Не удалось извлечь ID из URL: " + pageUrl);
                }
            }
            info.setId(id);

            // --- Извлечение описания ---
            // В новом примере описание находится в span.description-text
            String description = doc.selectFirst("span.description-text").text();
            info.setDescription(description);

            // --- Извлечение тегов ---
            Map<String, String> tgs = new HashMap<>();
            // В новом примере теги находятся в div.tags
            Elements tags = doc.select("div.tags a.button.button-white[href]");
            tags.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
                if (!key.isEmpty()) {
                    tgs.put(key, buildAbsoluteUrl(url));
                }
            });
            info.setTags(tgs);

            Map<String, String> times = new HashMap<>();
            String descriptionText = doc.selectFirst("span.description-text").text();
            info.setTimeCodes(times);
            List<Comment> commentsList = new ArrayList<>();
            //TODO Извлечь коментарии
            info.setComments(commentsList);

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге видео: " + e.getMessage());
        }
        return info;
    }

    private static String buildAbsoluteUrl(String relativePath) {
        try {
            if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
                return relativePath; // Уже абсолютная ссылка
            }

            URL base = new URL(ABS_URL);
            URL absolute = new URL(base, relativePath);
            return absolute.toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
