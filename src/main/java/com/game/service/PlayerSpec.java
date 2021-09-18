package com.game.service;

import com.game.entity.Player;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class PlayerSpec {
    public  Specification<Player> findPlayers(Player player, Long after,Long before,Integer minExperience,Integer maxExperience,Integer minLevel,Integer maxLevel){
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (player.getName() != null && !player.getName().isEmpty()){
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + player.getName() + "%" ));
            }
            if (player.getTitle() != null && !player.getTitle().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),"%" + player.getTitle() + "%"));
            }
            if (player.getRace() != null){
                predicates.add(criteriaBuilder.equal(root.get("race"), player.getRace()));
            }
            if (player.getProfession() != null){
                predicates.add(criteriaBuilder.equal(root.get("profession"), player.getProfession()));
            }
            if (player.getBanned() != null){
                predicates.add(criteriaBuilder.equal(root.get("banned"), player.getBanned()));
            }
            if (after != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after)));
            }
            if (before != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before)));
            }
            if (minExperience != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience));
            }
            if (maxExperience != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience));
            }
            if (minLevel != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel));
            }
            if (maxLevel != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel));
            }

            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }
}
