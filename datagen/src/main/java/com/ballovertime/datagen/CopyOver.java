//package com.ballovertime.datagen;
//
//import com.ballovertime.datagen.service.ZipUtils;
//import dev.jarcadia.dbx.Dbx;
//import io.micronaut.context.annotation.Bean;
//import jakarta.inject.Inject;
//import jakarta.inject.Named;
//import jakarta.inject.Singleton;
//import org.flywaydb.core.Flyway;
//import org.h2.jdbcx.JdbcDataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.sqlite.SQLiteDataSource;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.Instant;
//
//public class CopyOver {
//
//    private static final Logger logger = LoggerFactory.getLogger(CopyOver.class);
//
//
//    public static void main(String[] args) {
//
//        Path baseDirectory = Paths.get("/Users/michael/.ballovertime");
//
//        SQLiteDataSource srcDs = new SQLiteDataSource();
//        srcDs.setUrl(String.format("jdbc:sqlite:%s", baseDirectory.resolve("ballovertime.sqlite3")));
//
//        JdbcDataSource destDs = new JdbcDataSource();
//        destDs.setUrl(String.format("jdbc:h2:file:%s", baseDirectory.resolve("ballovertime.h2")));
//        Flyway.configure().dataSource(destDs).load().migrate();
//
//        Dbx srcDbx = new Dbx(srcDs);
//        Dbx destDbx = new Dbx(destDs);
//        srcDbx.query("select * from cached_page", rs -> {
//
//
//            while(rs.next()) {
//                String url = rs.getString("URL");
//                int code = rs.getInt("CODE");
//                byte[] content = rs.getBytes("CONTENT");
//                try {
//                    byte[] zipped = ZipUtils.compress(content);
//                    logger.info("Processing page {} ({} -> {})", rs.getString("URL"), content.length, zipped.length);
//                    Instant timestamp = Instant.ofEpochMilli(rs.getLong("TIMESTAMP"));
//                    destDbx.update("insert into cached_page (url, code, content, ts) values (?, ?, ?, ?)",
//                            url, code, zipped, timestamp);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            } ;
//
//            return null;
//        });
//
//
//
//    }
//
//
//}
