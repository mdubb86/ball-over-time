package com.ballovertime.datagen.service.bbref;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class CachedBoxScoreRepo {

    private final Logger logger = LoggerFactory.getLogger(CachedBoxScoreRepo.class);

    private final BbRefUriService uriService;
//    private final SQLiteDataSource dataSource;
    private final ExecutorService executor;

    @Inject
    public CachedBoxScoreRepo(Path baseDirectory, BbRefUriService uriService) {
        this.uriService = uriService;
//        dataSource = new SQLiteDataSource();
//        dataSource.setUrl(String.format("jdbc:sqlite:%s", baseDirectory.resolve("ballovertime.sqlite3")));
        this.executor = Executors.newSingleThreadExecutor();
    }

    public Mono<String> loadBoxScorePageContent(String bbRefBoxScoreId) {
//        return Mono.defer(() -> Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
//            logger.info("Loading legacy boxscore {}", bbRefBoxScoreId);
//            try(Connection conn = dataSource.getConnection();
//                PreparedStatement stmt = conn
//                        .prepareStatement("select content from cached_page where url = ?")) {
//                stmt.setString(1, uriService.buildBoxScorePageUri(bbRefBoxScoreId).toString());
//                ResultSet rs = stmt.executeQuery();
//                if (rs.next()) {
//                    return rs.getBytes("content");
//                } else {
//                    return null;
//                }
//            } catch (SQLException ex) {
//                throw new RuntimeException(ex);
//            }
//        }, executor))).map(bytes -> bytes == null ? null : new String(bytes, StandardCharsets.UTF_8));
        return Mono.empty();
    }
}
