package com.ballovertime.datagen.service.bbref;

import com.ballovertime.datagen.model.BbRefBoxScore;
import com.ballovertime.datagen.model.BbRefBoxScoreStat;
import com.ballovertime.datagen.model.Season;
import com.ballovertime.datagen.model.SeasonMonth;
import jakarta.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class BbRefParsingService {

    private final DateTimeFormatter scheduleDateFormatter;

    public BbRefParsingService() {
        this.scheduleDateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy");
    }


//    protected Player parsePlayerPage(String content) {
//        return new Player("hello", "world");
//    }

    protected List<Season> parseLeaguePage(String content) {
        Document doc = Jsoup.parse(content);
        Element tableEl = doc.selectFirst("table#stats tbody");
        return tableEl.select("tr").stream()
                .filter(rowEl -> !rowEl.hasClass("thead"))
                .map(rowEl -> {
//                    logger.info("Row el: {}", rowEl);
                    Element seasonLink = rowEl.selectFirst("th[data-stat=\"season\"] > a");
                    String yearSpan = seasonLink.text();
                    Year startYear = Year.of(Integer.parseInt(yearSpan.substring(0, 4)));
                    Year endYear = startYear.plusYears(1);
                    String bbRefSeasonHref = seasonLink.attr("href");
                    String bbRefSeasonId = bbRefSeasonHref.substring(9, bbRefSeasonHref.length() - 5);
                    String league = rowEl.selectFirst("td[data-stat=\"lg_id\"] > a").text();
                    String seasonId = String.format("%s_%s", league, endYear.toString().substring(2));
//                    boolean active = rowEl.selectFirst("td[data-stat=\"champion\"] > a") == null;

                    return new Season(seasonId, league, startYear, endYear, bbRefSeasonId);
                })
                .collect(Collectors.toList());
    }

    protected List<SeasonMonth> parseSeasonMonths(Season season, String content) {
        Document doc = Jsoup.parse(content);
        Element filterEl = doc.selectFirst("div#content div.filter");
        return filterEl.select("a").stream()
                .map(a -> a.attr("href"))
                .map(text -> text.substring(text.indexOf("_games-") + 7, text.length() - 5))
                .map(monthKey -> {

                    Month month;
                    Year year;
                    if (monthKey.contains("-")) {   // Handle special cases where year is specified (2019-20)
                        String[] bits = monthKey.split("-");
                        month = Month.valueOf(bits[0].toUpperCase());
                        year = Year.of(Integer.parseInt(bits[1]));
                    } else {
                        month = Month.valueOf(monthKey.toUpperCase());
                        year =  month.getValue() > 8 ? season.startYear() : season.endYear();
                    }

                    String seasonMonthId = String.format("%s_%d_%02d", season.league(), year.getValue(),
                            month.getValue());
                    return new SeasonMonth(seasonMonthId, season.seasonId(), month, year, monthKey);
                })
                .collect(LinkedList::new, LinkedList::addFirst, (list1, list2) -> list1.addAll(list2));
    }

    protected List<BbRefBoxScore> parseSeasonMonthBoxScores(String seasonMonthId, String content) {
        Document doc = Jsoup.parse(content);
        Element scheduleEl = doc.selectFirst("div#content table#schedule tbody");

        List<BbRefBoxScore> boxScores = scheduleEl.select("tr").stream()
                .map(rowEl -> {
                    Element boxScoreLinkEl = rowEl.selectFirst("td[data-stat=\"box_score_text\"] > a");
                    if (boxScoreLinkEl != null) {
                        String boxScoreLink = boxScoreLinkEl.attr("href");
                        String bbRefBoxScoreId = boxScoreLink.substring(11, boxScoreLink.length() - 5);

                        String dateText = scheduleEl.selectFirst("th[data-stat=\"date_game\"] > a").text();
                        LocalDate date = LocalDate.parse(dateText, scheduleDateFormatter);

                        String awayTeamLink = rowEl.selectFirst("td[data-stat=\"visitor_team_name\"] > a")
                                .attr("href");
                        String awayTeamId = awayTeamLink.substring(7, awayTeamLink.length() - 5);
                        int awayPoints = Integer.parseInt(rowEl.selectFirst("td[data-stat=\"visitor_pts\"]").text());

                        String homeTeamLink = rowEl.selectFirst("td[data-stat=\"home_team_name\"] > a")
                                .attr("href");
                        String homeTeamId = homeTeamLink.substring(7, homeTeamLink.length() - 5);
                        int homePoints = Integer.parseInt(rowEl.selectFirst("td[data-stat=\"home_pts\"]").text());

                        return new BbRefBoxScore(bbRefBoxScoreId, seasonMonthId, date, homeTeamId, awayTeamId,
                                homePoints, awayPoints);
                    } else {
                        return null;
                    }
                })
                .filter(boxScore -> boxScore != null)
                .collect(() -> new LinkedList<>(), (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));

        return boxScores;
    }

    protected List<BbRefBoxScoreStat> parseBoxScoreStats(BbRefBoxScore boxScore, String content) {
        Document doc = Jsoup.parse(content);

        Element scoreboxEl = doc.selectFirst("div.scorebox");

        Tuple2<String, Integer> awayTuple = extractTeamTotal(scoreboxEl.child(0));
        String awayTeamId = awayTuple.getT1();
        int awayPoints = awayTuple.getT2();
        Tuple2<String, Integer> homeTuple = extractTeamTotal(scoreboxEl.child(1));
        String homeTeamId = homeTuple.getT1();
        int homePoints = homeTuple.getT2();

        if (!boxScore.homeBbRefTeamId().equals(homeTeamId)) {
            throw new RuntimeException(String.format("Mismatched home team for %s (%s != %s)",
                    boxScore.bbRefBoxScoreId(), boxScore.homeBbRefTeamId(), homeTeamId));
        }

        if (!boxScore.awayBbRefTeamId().equals(awayTeamId)) {
            throw new RuntimeException(String.format("Mismatched away team for %s (%s != %s)",
                    boxScore.bbRefBoxScoreId(), boxScore.awayBbRefTeamId(), awayTeamId));
        }

        if (boxScore.homeTeamPoints() != homePoints) {
            throw new RuntimeException(String.format("Mismatched home team points for %s (%d != %d)",
                    boxScore.bbRefBoxScoreId(), boxScore.homeTeamPoints(), homePoints));
        }

        if (boxScore.awayTeamPoints() != awayPoints) {
            throw new RuntimeException(String.format("Mismatched away team points for %s (%d != %d)",
                    boxScore.bbRefBoxScoreId(), boxScore.awayTeamPoints(), awayPoints));
        }

        // TODO verify date

        List<BbRefBoxScoreStat> stats = new ArrayList<>();
        parseStats(boxScore.bbRefBoxScoreId(), doc, homeTeamId, stats);
        parseStats(boxScore.bbRefBoxScoreId(), doc, awayTeamId, stats);
        return stats;
    }


    private Tuple2<String, Integer> extractTeamTotal(Element teamScoreBoxEl) {
        String teamLinkHref = teamScoreBoxEl.selectFirst("strong a").attr("href");
        String teamId = teamLinkHref.substring(7, teamLinkHref.length() - 5);
        int points = Integer.parseInt(teamScoreBoxEl.selectFirst("div.score").text());
        return Tuples.of(teamId, points);
    }

    private void parseStats(String bbRefBoxScoreId, Document doc, String teamId, List<BbRefBoxScoreStat> stats) {
        String abbrv = teamId.substring(0, teamId.indexOf("/"));
        parseStats(bbRefBoxScoreId,
                doc.selectFirst(String.format("div#all_box-%s-game-basic table.stats_table tbody", abbrv)), stats);
        parseStats(bbRefBoxScoreId,
                doc.selectFirst(String.format("div#all_box-%s-game-advanced table.stats_table tbody", abbrv)), stats);
    }

    private void parseStats(String bbRefBoxScoreId, Element table, List<BbRefBoxScoreStat> stats) {
        if (table != null) {
            for (Element tr : table.select("tr")) {
                Element playerEl = tr.selectFirst("th");
                String bbRefPlayerId = playerEl.attr("data-append-csv");
                String playerName = playerEl.text();
                for (Element td : tr.select("td")) {
                    String stat = td.attr("data-stat");
                    String value = td.text();
                    stats.add(new BbRefBoxScoreStat(bbRefBoxScoreId, bbRefPlayerId, playerName, stat, value));
                }
            }
        }
    }

}
