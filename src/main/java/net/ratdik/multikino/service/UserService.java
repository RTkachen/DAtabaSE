package net.ratdik.multikino.service;

import net.ratdik.multikino.domain.*;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.dto.ChangePasswordDto;
import net.ratdik.multikino.dto.UserRegistrationDto;
import net.ratdik.multikino.repository.RoleRepository;
import net.ratdik.multikino.repository.UserRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Transactional
    public void registerNewUser(UserRegistrationDto dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mail już użyty");
        }
        User u = User.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .birthDate(dto.getBirthDate())
                .password(encoder.encode(dto.getPassword()))
                .blocked(false)
                .build();

        // pierwszy = manager
        if (userRepo.count() == 0) {
            Role mgr = roleRepo.findByName(RoleName.ROLE_MANAGER).orElseThrow();
            u.getRoles().add(mgr);
        }
        Role client = roleRepo.findByName(RoleName.ROLE_CLIENT).orElseThrow();
        u.getRoles().add(client);

        userRepo.save(u);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Brak: " + email));
        if (u.isBlocked()) {
            throw new LockedException("Konto zablokowane");
        }
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                u.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                        .toList()
        );
    }

    @Transactional
    public void changePassword(Integer userId, ChangePasswordDto dto) {
        User u = userRepo.findById(userId).orElseThrow();
        if (!encoder.matches(dto.getOldPassword(), u.getPassword())) {
            throw new IllegalArgumentException("Stare hasło nie pasuje");
        }
        if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("Hasła się nie zgadzają");
        }
        u.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(u);
    }

    @Transactional
    public void blockUnblockUser(Integer userId, boolean block) {
        User u = userRepo.findById(userId).orElseThrow();
        u.setBlocked(block);
        userRepo.save(u);
    }

    @Transactional
    public void assignRole(Integer userId, RoleName roleName) {
        User u = userRepo.findById(userId).orElseThrow();
        Role r = roleRepo.findByName(roleName).orElseThrow();
        u.getRoles().add(r);
        userRepo.save(u);
    }

    @Transactional
    public void removeRole(Integer userId, RoleName roleName) {
        User u = userRepo.findById(userId).orElseThrow();
        u.getRoles().removeIf(r -> r.getName() == roleName);
        // upewnij się, że co najmniej jeden manager zostaje
        if (roleName == RoleName.ROLE_MANAGER &&
                userRepo.findAll().stream()
                        .noneMatch(x -> x.hasRole(RoleName.ROLE_MANAGER))) {
            throw new IllegalStateException("Potrzebny co najmniej jeden kierownik");
        }
        userRepo.save(u);
    }

    public User login(String email, String rawPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma takiego użytkownika"));
        if (!encoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Nieprawidłowe hasło");
        }
        return user;
    }

    /** <-- TA METODA DODAJE PANEL ADMINA **/
    public List<User> findAll() {
        return userRepo.findAll();
    }
}
