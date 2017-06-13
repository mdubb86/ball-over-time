package com.meridian.ball;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meridian.ball.model.NameAndId;
import com.meridian.ball.model.Player;
import com.meridian.ball.model.Stat;
import com.meridian.ball.repository.GameRepository;
import com.meridian.ball.service.PlayerService;
import com.meridian.ball.service.SavedChartService;
import com.meridian.ball.service.StatService;
import com.meridian.ball.service.StatService.Aggregation;

@Controller
@RequestMapping()
public class SiteController {

    private final Logger logger = LoggerFactory.getLogger(SiteController.class);
    
    private final PlayerService playerService;
    private final StatService statService;
    private final GameRepository gameRepository;
    private final SavedChartService savedChartService;

    @Autowired
    public SiteController(PlayerService playerService,
            StatService statService,
            GameRepository gameRepository,
            SavedChartService savedChartService) {
        this.playerService = playerService;
        this.statService = statService;
        this.gameRepository = gameRepository;
        this.savedChartService = savedChartService;
    }

    @RequestMapping()
    public String loadCover(Model model) {
        model.addAttribute("games", gameRepository.count());
        model.addAttribute("stats", statService.getCount() * 34);
        return "cover";
    }
    
    @RequestMapping("/saved/{chartId}")
    public String loadSaved(@PathVariable("chartId") String chartId, HttpServletRequest req, HttpServletResponse res) {
        res.addCookie(new Cookie("savedChart", chartId));
        int port = req.getServerPort();
        String portStr = port == 80 || port == 443 ? "" : ":" + port;
        String redirectUrl = req.getScheme() + "://" + req.getServerName() + portStr + req.getServletPath() + "/interactive";
        return "redirect:" + redirectUrl;
    }

    @RequestMapping("/interactive")
    public String loadInteractive(Model model,
            @CookieValue(value="aggregation", defaultValue="season") String aggregation,
            @CookieValue(value="stat", defaultValue="points") String stat,
            @CookieValue(value="players", defaultValue="893,977,2544") String playerIds) {

        // Lookup players
        List<Player> players = playerService.findByIds(playerIds);
        logger.info("Starting with {} players", players.size());
            
        model.addAttribute("stats", statService.getStats());
        model.addAttribute("stat", stat);
        model.addAttribute("aggregation", aggregation);
        model.addAttribute("players", players.stream().map(p -> new NameAndId(p)).collect(Collectors.toList()));
        return "interactive";
    }

    @RequestMapping("/players")
    public @ResponseBody List<Player> getPlayers(@RequestParam("query") String query) {
        return playerService.findByUsernameContaining(query);
    }

    @RequestMapping("/data")
    public @ResponseBody List<Object[]> getData(
            @RequestParam("stat") String statName,
            @RequestParam("playerId") Integer playerId,
            @RequestParam("aggregation") String aggregationName) {
        Stat stat = Stat.valueOf(statName.replace("-", "_").toUpperCase());
        Aggregation aggregation = Aggregation.valueOf(aggregationName.toUpperCase());
        return statService.getStatsOverTime(stat, playerId, aggregation);
    }

    @RequestMapping("/share")
    public @ResponseBody Map<String, String> getShare(
            @RequestParam("playerIds") String playerIds,
            @RequestParam("stat") String statName,
            @RequestParam("aggregation") String aggregationName) {
        Stat stat = Stat.valueOf(statName.replace("-", "_").toUpperCase());
        Aggregation aggregation = Aggregation.valueOf(aggregationName.toUpperCase());
        String chartId = savedChartService.saveChart(stat, aggregation, playerIds);
        return Collections.singletonMap("chartId", chartId);
    }
}
