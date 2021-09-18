package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PlayerService {
     List<Player> readAll(Player player, Long after,Long before,Integer minExperience,Integer maxExperience,Integer minLevel,Integer maxLevel, Integer pageNumber, Integer pageSize, PlayerOrder order);
     long count(Player player, Long after, Long before, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel);
     boolean checkId(String id);
     ResponseEntity<Player> findPlayerById(String id);
     ResponseEntity<Player> createPlayer(Player player);
     void deletePlayer(Player player);
     ResponseEntity<Player> updatePlayer(Player playerToUpdate, Player playerParam);
}
