package com.boardcamp.api.repositories;

import com.boardcamp.api.models.RentalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<RentalModel, Long> {
    long countByGameIdAndReturnDateIsNull(Long gameId);
}