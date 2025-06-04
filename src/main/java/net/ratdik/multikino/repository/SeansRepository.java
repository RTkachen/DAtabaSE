package net.ratdik.multikino.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.ratdik.multikino.domain.Seans;

public interface SeansRepository extends JpaRepository<Seans, Long> {


    @Query("SELECT s FROM Seans s WHERE FUNCTION('HOUR', s.czasRozpoczecia) = :hour")
    List<Seans> findByHour(@Param("hour") int hour);
}
