package net.ratdik.multikino.repository;

import net.ratdik.multikino.domain.Jezyk;
import net.ratdik.multikino.domain.JezykNapisow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JezykNapisowRepository extends JpaRepository<JezykNapisow, Integer> {
    Optional<JezykNapisow> findByNazwaJezyka(Jezyk jezyk);
}
