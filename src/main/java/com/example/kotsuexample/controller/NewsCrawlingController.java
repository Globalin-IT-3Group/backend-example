package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsCrawlingController {

    private final RedisUtil redisUtil;

    @GetMapping
    public List<NewsItem> getNews(
            @RequestParam(defaultValue = "https://www3.nhk.or.jp/news/") String url) throws Exception {

        String redisKey = "newsCache_" + url;

        if (redisUtil.hasKey(redisKey)) {
            // value는 List<NewsItem>으로 역직렬화됨
            Object cached = redisUtil.getValue(redisKey, List.class); // (아래 참고)
            if (cached != null) {
                return (List<NewsItem>) cached;
            }
        }

        // 1. Jsoup으로 해당 페이지 접속/파싱
        Document doc = Jsoup.connect(url).get();

        // 2. ul 선택 (이미지 참고: class 정확히 복붙)
        Element ul = doc.selectFirst("ul.content--list.grid--col.-column4-md.-column2-sm");
        List<NewsItem> result = new ArrayList<>();

        System.out.println("ㅋㅋㅋ");

        if (ul != null) {
            // 3. li 8개만 가져오기
            Elements lis = ul.select("> li");
            for (int i = 0; i < Math.min(lis.size(), 8); i++) {
                Element li = lis.get(i);

                // 1. 링크(a)
                Element a = li.selectFirst("a");
                String link = a != null ? a.absUrl("href") : "";

                // 2. 이미지(img)
                Element img = li.selectFirst("figure img");
                String image = img != null ? img.absUrl("data-src") : "";

                // 3. 제목(em)
                Element em = li.selectFirst("em.title");
                String title = em != null ? em.text() : "";

                result.add(new NewsItem(title, link, image));
            }
        }

        // 3. Redis에 24시간(86400초) 저장
        redisUtil.saveNews(redisKey, result, Duration.ofHours(24));

        return result;
    }

    public record NewsItem(String title, String link, String image) {}
}
