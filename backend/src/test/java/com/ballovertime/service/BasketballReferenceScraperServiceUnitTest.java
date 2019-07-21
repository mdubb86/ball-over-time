package com.ballovertime.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.AssertJProxySetup;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.assertj.AssertableReactiveWebApplicationContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.meridian.ball.model.ScrapedBoxscore;
import com.meridian.ball.model.Stat;
import com.meridian.ball.service.BasketballReferenceScraperService;
import com.meridian.ball.service.HtmlDocumentService;

import jooq.tables.pojos.Team;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {BasketballReferenceScraperService.class})
public class BasketballReferenceScraperServiceUnitTest {
    
    @MockBean
    HtmlDocumentService htmlDocumentService;
    
    @Autowired
    BasketballReferenceScraperService basketballReferenceService;
    
    @Test
    public void testTeamScrape() throws IOException {
        mockGet("https://www.basketball-reference.com/teams/SAS/2015.html", "team_sas_2015.html");
        Team team = basketballReferenceService.scrapeTeam("SAS", LocalDate.now().withYear(2015));
        assertThat("San Antonio Spurs").matches(team.getName());
    }
    
    @Test
    public void testBoxscoreScrape() throws IOException {
        mockGet("https://www.basketball-reference.com/boxscores/199710310DEN.html", "boxscore_199710310DEN.html");
        List<ScrapedBoxscore> boxscores = basketballReferenceService.scrapeBoxscore("199710310DEN");
        
        assertThat(boxscores.size()).isEqualTo(22).as("Should have 22 boxscores");
        ScrapedBoxscore timmy = boxscores.stream()
                .filter(bs -> "duncati01".equals(bs.getPlayerId()))
                .findFirst().orElse(null);
        assertThat(timmy).isNotNull().as("Boxscore should contain Tim Duncan");
        assertThat(timmy.getValues().get(Stat.POINTS)).isEqualTo(15).as("Tim Duncan scored 15 points");
        assertThat(timmy.getValues().get(Stat.FIELD_GOALS_MADE)).isEqualTo(6).as("Tim Duncan made 6 shots");
    }
    
    private void mockGet(String url, String filename) throws IOException {
        Resource resource = new ClassPathResource(filename);
        Document doc = Jsoup.parse(resource.getInputStream(), "UTF-8", "/");
        Mockito.when(htmlDocumentService.get(url)).thenReturn(doc);
    }
    
}
