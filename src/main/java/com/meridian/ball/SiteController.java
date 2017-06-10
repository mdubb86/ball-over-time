package com.meridian.ball;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meridian.ball.model.PlayerNameAndId;
import com.meridian.ball.model.Stat;
import com.meridian.ball.repository.GameRepository;
import com.meridian.ball.service.PlayerService;
import com.meridian.ball.service.StatService;
import com.meridian.ball.service.StatService.Aggregation;

@Controller
@RequestMapping()
public class SiteController {
    
    private final PlayerService playerService;
    private final StatService statService;
    private final GameRepository gameRepository;
    
    @Autowired
    public SiteController(PlayerService playerService,
            StatService statService,
            GameRepository gameRepository) {
        this.playerService = playerService;
        this.statService = statService;
        this.gameRepository = gameRepository;
    }
    
    @RequestMapping
    public String loadCover(Model model) {
        model.addAttribute("games", gameRepository.count());
        model.addAttribute("stats", statService.getCount() * 34);
        return "cover";
    }
    
    @RequestMapping("/interactive")
    public String loadInteractive(Model model) {
        model.addAttribute("stats", statService.getStats());
        return "interactive";
    }
    
    @RequestMapping("/players")
    public @ResponseBody List<PlayerNameAndId> getPlayers(@RequestParam("query") String query) {
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
}
