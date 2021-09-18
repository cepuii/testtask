package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class PlayerServiceImpl implements PlayerService {

    PlayerRepository repository;
    PlayerSpec playerSpec;

    @Autowired
    public void setRepository(PlayerRepository repository) {
        this.repository = repository;
    }
    @Autowired
    public void setPlayerSpec(PlayerSpec playerSpec) {
        this.playerSpec = playerSpec;
    }

    @Override
    public ResponseEntity<Player> createPlayer(Player player) {
        HttpStatus httpStatus = HttpStatus.OK;
        String name = player.getName();
        String title = player.getTitle();
        int experience = player.getExperience();
        try {
            Calendar calBirthday = Calendar.getInstance();
            calBirthday.setTime(player.getBirthday());
            if (calBirthday.get(Calendar.YEAR) <= 2000 || calBirthday.get(Calendar.YEAR) >= 3000) {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (NullPointerException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        if (name == null || name.length() >= 12) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (title == null || title.length() >= 30) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (player.getRace() == null) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (player.getProfession() == null) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (player.getBirthday().getTime() < 0) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (experience < 0 || experience > 10000000) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            int level = (int) ((Math.sqrt(2500 + 200 * experience) - 50) / 100);
            player.setLevel(level);
            int untilNextLevel = (50 * (level + 1) * (level + 2)) - experience;
            player.setUntilNextLevel(untilNextLevel);
            repository.save(player);
            httpStatus = HttpStatus.OK;
        }


        return new ResponseEntity<>(player, httpStatus);
    }

    public boolean checkId(String id) {
        try {
            int validId = Integer.parseInt(id);
            return validId > 0;

        } catch (Exception e) {
            return false;
        }
    }

    public ResponseEntity<Player> findPlayerById(String id) {
        if (checkId(id)) {
            Optional<Player> player = repository.findById(Long.parseLong(id));
            return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }



    @Override
    public List<Player> readAll(Player player, Long after,Long before,
                                Integer minExperience,Integer maxExperience,
                                Integer minLevel,Integer maxLevel, Integer pageNumber, Integer pageSize, PlayerOrder order) {


        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        if (player == null){
            player = new Player();
        }


        Page<Player> pagedResult = repository.findAll(playerSpec.findPlayers(player, after, before, minExperience, maxExperience, minLevel, maxLevel), paging);


        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }



    @Override
    public long count(Player player, Long after, Long before, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {
        return Math.toIntExact(repository.count(playerSpec.findPlayers(player, after, before, minExperience, maxExperience, minLevel, maxLevel)));
    }

    @Override
    public void deletePlayer(Player player) {
        repository.delete(player);
    }

    @Override
    public ResponseEntity<Player> updatePlayer(Player playerToUpdate, Player player) {

        String name = player.getName();
        String title = player.getTitle();
        int experience = player.getExperience();


        if (name != null) {
            if (name.length() <= 12) {
                playerToUpdate.setName(name);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        if (title != null) {
            if (title.length() <= 30) {
                playerToUpdate.setTitle(title);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        if (player.getRace() != null) {
            playerToUpdate.setRace(player.getRace());
        }

        if (player.getProfession() != null) {
            playerToUpdate.setProfession(player.getProfession());
        }
        if (player.getBirthday() != null) {
            try {
                Calendar calBirthday = Calendar.getInstance();
                calBirthday.setTime(player.getBirthday());
                if (calBirthday.get(Calendar.YEAR) >= 2000 && calBirthday.get(Calendar.YEAR) <= 3000) {
                    playerToUpdate.setBirthday(player.getBirthday());
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } catch (NullPointerException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        if (player.getExperience() != 0) {
            if (experience >= 0 && experience < 10000000) {
                playerToUpdate.setExperience(experience);

                    int level = (int) ((Math.sqrt(2500 + 200 * experience) - 50) / 100);
                    playerToUpdate.setLevel(level);

                    int untilNextLevel = (50 * (playerToUpdate.getLevel() + 1) * (playerToUpdate.getLevel() + 2)) - playerToUpdate.getExperience();
                    playerToUpdate.setUntilNextLevel(untilNextLevel);

            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        }
        if (player.getUntilNextLevel() != 0){
            playerToUpdate.setUntilNextLevel(player.getUntilNextLevel());
        }
        if (player.getBanned() != null) {
            playerToUpdate.setBanned(player.getBanned());
        }
        repository.save(playerToUpdate);
        return new ResponseEntity<>(playerToUpdate, HttpStatus.OK);
    }
}
