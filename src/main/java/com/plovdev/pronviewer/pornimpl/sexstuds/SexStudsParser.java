package com.plovdev.pronviewer.pornimpl.sexstuds;

import com.plovdev.pronviewer.databases.FavoriteVideos;
import com.plovdev.pronviewer.httpquering.Category;
import com.plovdev.pronviewer.httpquering.PornParser;
import com.plovdev.pronviewer.httpquering.defimpl.PBPornHandler;
import com.plovdev.pronviewer.models.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    @Override
    public List<PornCard> getAll(String html) {
        Document doc = Jsoup.parse(html);
        List<PornCard> cards = new ArrayList<>();
        try (ExecutorService service = Executors.newFixedThreadPool(10)) {
            List<Future<PornCard>> futures = new ArrayList<>();

            // Используем новый селектор для новых элементов
            Elements elements = doc.select("div.video.trailer");
            elements.stream().limit(45).forEach(e -> {
                Future<PornCard> future = service.submit(() -> parseVideoBlock(e));
                futures.add(future);
            });

            futures.forEach(f -> {
                try {
                    PornCard card = f.get();
                    cards.add(card);
                } catch (Exception e) {
                    // Лучше логировать ошибку, чем просто выводить сообщение
                    // Например: logger.error("Ошибка парсинга видео блока", e);
                    System.err.println("Ошибка при парсинге видео блока: " + e.getMessage());
                    // Можно также добавить пустой или дефолтный объект, если нужно продолжить работу
                    // cards.add(new PornCard()); // Если нужно добавить заглушку
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге всех видео блоков", e);
        }
        return cards;
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
            // В новом сайте URL видео могут быть в <video> теге или в других местах.
            // Пока что оставим пустым, так как из примера видно, что основной источник URL - это <source>.
            // Но если нужна конкретная логика для разных качеств, то можно добавить.
            // Пример: если есть выбор качества в div.quality_chooser
            // Elements urls = doc.select("div.quality_chooser a[href]");
            // urls.forEach(e -> {
            //     String key = e.text().trim();
            //     String url = e.attr("href");
            //     links.put(key, url);
            // });
            // info.setUrls(links);

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
            // Если нужно извлечь URL из скрипта или других источников, можно добавить логику
            // Пример:
            // Elements scripts = doc.select("script");
            // for (Element script : scripts) {
            //     String scriptText = script.data();
            //     if (scriptText.contains("VIDEO_FILE")) {
            //         // Извлечение VIDEO_FILE из скрипта
            //         // ...
            //     }
            // }

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
            // Модели могут быть в div.video-header или в тегах
            // В данном случае, модель может быть в div.video-header или в тегах (если они там есть)
            // Но в примере она указана как "Автор: Lucker25" в span.author
            // Для простоты, извлекаем из тегов, если есть
            // Если нужно извлекать из "Автор: Lucker25", то нужно искать по классу или тексту
            // Пример:
            // Elements authorElements = doc.select("span.modifier-type-label:contains(Автор:) + a.tag-modifier");
            // if (!authorElements.isEmpty()) {
            //     Element authorLink = authorElements.first();
            //     String key = authorLink.selectFirst("span.label").text().trim();
            //     String url = authorLink.attr("href");
            //     mds.put(key, url);
            // }
            // Но если модель в тегах, то:
            Elements models = doc.select("div.tags a[href]");
            models.forEach(e -> {
                String key = e.text().trim();
                String url = e.attr("href");
                // Фильтруем, чтобы не добавлять не относящиеся к моделям теги
                // Это требует знания структуры тегов
                // Для примера добавим все теги, которые могут быть моделями
                // Если нужно более точное извлечение, можно добавить логику фильтрации
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

            // --- Извлечение timecodes ---
            Map<String, String> times = new HashMap<>();
            // В новом примере timecodes находятся в meta itemprop="description"
            // Или в div.description > span.description-text
            // Но они не в виде отдельных элементов span с href
            // Пример: "Секс на животе — 00:03; разговоры — 00:10; глубокий минет — 00:41; камшот в рот — 02:47, 03:58"
            // Если нужно извлечь их отдельно, можно сделать парсинг текста
            // Пока что оставим пустым или используем описание
            // Для примера можно использовать описание, если оно содержит timecodes
            String descriptionText = doc.selectFirst("span.description-text").text();
            if (!descriptionText.isEmpty()) {
                // Пример разбора времени из описания
                // Это упрощенный пример, может потребоваться более сложная логика
                // times.put("description", descriptionText); // Можно сохранить описание как таймкод
                // Или парсить отдельные части
                // Но так как у нас нет отдельных элементов span с href для таймкодов,
                // можно оставить пустым или использовать описание
            }
            info.setTimeCodes(times);

            // --- Извлечение комментариев ---
            List<Comment> commentsList = new ArrayList<>();
            // Комментарии находятся в div.comments-list
            // Но для получения списка комментариев нужно больше данных или API
            // В текущем HTML они не представлены как отдельные элементы
            // Поэтому пока оставим пустым
            // Пример:
            // Elements commentElements = doc.select("div.comment");
            // commentElements.forEach(commentEl -> {
            //     Comment comment = new Comment();
            //     // Извлечение данных из commentEl
            //     commentsList.add(comment);
            // });
            info.setComments(commentsList);

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге видео: " + e.getMessage());
        }
        return info;
    }

    private static String buildAbsoluteUrl(String relativePath) {
        String baseUrl = ABS_URL;
        try {
            // Проверяем, является ли относительный путь уже абсолютным (начинается с http/https)
            if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
                return relativePath; // Уже абсолютная ссылка
            }

            URL base = new URL(baseUrl);
            URL absolute = new URL(base, relativePath);
            return absolute.toString();
        } catch (MalformedURLException e) {
            // Обработка ошибки, если URL некорректен
            System.err.println("Ошибка формирования абсолютного URL: " + e.getMessage());
            return baseUrl + relativePath; // fallback на конкатенацию (не самый надежный способ)
        }
    }
}
