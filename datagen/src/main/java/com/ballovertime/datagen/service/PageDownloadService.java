//package com.ballovertime.datagen.service;
//
//import dev.jarcadia.http.*;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Consumer;
//
//@Singleton
//public class PageDownloadService implements CacheProvider {
//
//    private final Logger logger = LoggerFactory.getLogger(PageDownloadService.class);
//
//    private final HttpClient httpClient;
//
//    @Inject
//    public PageDownloadService(HttpClient httpClient) {
//        this.httpClient = httpClient;
//    }
//
//    public Document downloadPage(Consumer<HttpRequestBuilder> req) {
//        HttpResponse res = httpClient.query(r -> r.withCustomizer(req)
//                .withCacheProvider(this)
//                .withDelay(500, TimeUnit.MILLISECONDS)
//        );
//        String body = res.getBodyAsString(StandardCharsets.UTF_8);
//        Document doc = Jsoup.parse(body);
//        return doc;
//    }
//
//    @Override
//    public CachedResponse getCachedResponse(URI uri) {
//        logger.debug("Loading cached response for {}", uri);
////        CachedPage page = cachedPageRepo.fetch(uri.toString());
////        return page == null ? null : new CachedResponse(page.code(), page.content());
//        return null;
//    }
//
//    @Override
//    public void cacheResponse(URI uri, CachedResponse res) {
//        logger.debug("Caching response for {}", uri.toString());
////        cachedPageRepo.insert(new CachedPage(uri.toString(), res.getCode(), res.getBytes(), System.currentTimeMillis()));
//    }
//
//}
