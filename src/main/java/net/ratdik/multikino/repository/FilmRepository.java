package net.ratdik.multikino.repository;

import net.ratdik.multikino.domain.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Integer> {
}
