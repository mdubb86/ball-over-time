package com.ballovertime.datagen;

import com.ballovertime.datagen.service.FillService;
import io.micronaut.context.annotation.*;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Factory
@Infrastructure
public class Config {

    private final Logger logger = LoggerFactory.getLogger(Config.class);

    @Singleton
    Path baseDirectory(@Value("${ballovertime.base-dir}") Optional<String> baseDirectoryProp) {
        Path baseDir = baseDirectoryProp.isPresent() ?
                Paths.get(baseDirectoryProp.get()) :
                Paths.get(System.getProperty("user.home"), ".ballovertime");

        if (!Files.exists(baseDir)) {
            throw new RuntimeException("Base directory " + baseDirectoryProp + " does not exist");
        }
        return baseDir;
    }

    @Singleton
    @Context
    @Primary
    ConnectionFactory connectionFactory(Path baseDirectory) {

        logger.info("Creating datasource");

        Path dbPath = baseDirectory.resolve("ballovertime.h2");

        JdbcDataSource ds = new JdbcDataSource();
        ds.setUrl(String.format("jdbc:h2:file:%s", dbPath));
        Flyway.configure().dataSource(ds).load().migrate();

        logger.info("Done migrating with Flyway");

//        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
//                .option(ConnectionFactoryOptions.DRIVER, "h2")
//                .option(ConnectionFactoryOptions.PROTOCOL, "file")
////                .option(USER, "…")
////                .option(PASSWORD, "…")
//                .option(ConnectionFactoryOptions.DATABASE, String.format("r2dbc:h2:file//%s", dbPath))
//                .build();
//
////        H2ConnectionFactory factory = new H2ConnectionFactory().
////                H


        H2ConnectionConfiguration config = H2ConnectionConfiguration.builder()
                .file(dbPath.toString())
                .build();

        return new H2ConnectionFactory(config);
    }
}
