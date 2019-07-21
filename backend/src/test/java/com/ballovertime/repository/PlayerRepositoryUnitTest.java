package com.ballovertime.repository;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.ballovertime.repository.PlayerRepositoryUnitTest.PlayerRepositoryUnitTestConfig;
import com.meridian.ball.repository.PlayerRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jooq.tables.pojos.Player;

@RunWith(SpringRunner.class)
// @FlywayTest(overrideLocations = true, locationsForMigrate =
// "test/db/migration")
@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@ContextConfiguration(classes = { PlayerRepositoryUnitTestConfig.class, PlayerRepository.class })
public class PlayerRepositoryUnitTest {

    @Configuration
    static class PlayerRepositoryUnitTestConfig {

        @Autowired
        private DataSource dataSource;

        @Bean
        public DSLContext create() throws SQLException {
            Flyway flyway = Flyway.configure().dataSource(dataSource).locations("classpath:db/test-migration").load();
            flyway.migrate();
            return DSL.using(dataSource.getConnection(), SQLDialect.POSTGRES_10);
        }

    }

    @Autowired
    PlayerRepository playerRepository;

    @Test
    public void testCrud() throws SQLException {
//        System.out.println(playerRepository.fetchOne(1));
        Player player = new Player();
        player.setPlayerId("jdoe");
        player.setName("John Doe");
        player.setPictureUrl("http://www.url.com");
        
        playerRepository.store(player);
        
        Player fetched = playerRepository.fetchOne("jdoe");
        
        System.out.println(fetched);
        
    }

}