// BiletRepository.java
package net.ratdik.multikino.repository;
import net.ratdik.multikino.domain.Bilet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BiletRepository extends JpaRepository<Bilet,Integer> {
    List<Bilet> findByUserId(Integer userId);
}