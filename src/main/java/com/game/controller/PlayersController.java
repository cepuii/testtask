package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController

public class PlayersController {

    final PlayerService service;
    public PlayersController(PlayerService playerService) {
        this.service = playerService;
    }


    @GetMapping("rest/players")
    public ResponseEntity<List<Player>> all(@ModelAttribute Player player,
                                            @RequestParam(required = false) Long after,
                                            @RequestParam(required = false) Long before,
                                            @RequestParam(required = false) Integer minExperience,
                                            @RequestParam(required = false) Integer maxExperience,
                                            @RequestParam(required = false) Integer minLevel,
                                            @RequestParam(required = false) Integer maxLevel,
                                            @RequestParam(defaultValue = "0") Integer pageNumber,
                                            @RequestParam(defaultValue = "3") Integer pageSize,
                                            @RequestParam(defaultValue = "ID") PlayerOrder order){

        List<Player> list = service.readAll(player, after, before, minExperience, maxExperience, minLevel, maxLevel, pageNumber, pageSize, order);
       return new ResponseEntity<>( list , HttpStatus.OK);

    }


    @GetMapping("rest/players/count")
    public Integer count(@ModelAttribute Player player,
                         @RequestParam(required = false) Long after,
                         @RequestParam(required = false) Long before,
                         @RequestParam(required = false) Integer minExperience,
                         @RequestParam(required = false) Integer maxExperience,
                         @RequestParam(required = false) Integer minLevel,
                         @RequestParam(required = false) Integer maxLevel){



        return (int) service.count(player, after, before, minExperience, maxExperience, minLevel, maxLevel);
    }



    @PostMapping(value = "rest/players", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@RequestBody Player player){
        ResponseEntity<Player> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player != null) {
            result = service.createPlayer(player);
        }
        return result;
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String id){


            return service.findPlayerById(id);

    }

    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayerById(@PathVariable String id,
                                                   @RequestBody Player player){
            ResponseEntity<Player> result = service.findPlayerById(id);
            if (result.getStatusCode() == HttpStatus.OK){
               result = service.updatePlayer(result.getBody(), player);
            }
            return result;
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<Player> deletePlayerById(@PathVariable String id){

            ResponseEntity<Player> result = service.findPlayerById(id);
            if (result.getStatusCode() == HttpStatus.OK){
                service.deletePlayer(result.getBody());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return result;
            }

        }

}
