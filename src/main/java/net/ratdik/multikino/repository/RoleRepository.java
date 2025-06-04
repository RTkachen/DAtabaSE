package net.ratdik.multikino.repository;
import net.ratdik.multikino.domain.Role;
import net.ratdik.multikino.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(RoleName name);
}