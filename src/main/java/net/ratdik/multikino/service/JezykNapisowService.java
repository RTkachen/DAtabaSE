package net.ratdik.multikino.service;

import net.ratdik.multikino.domain.JezykNapisow;
import net.ratdik.multikino.repository.JezykNapisowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JezykNapisowService {
    private final JezykNapisowRepository repo;

    public JezykNapisowService(JezykNapisowRepository repo) {
        this.repo = repo;
    }

    public List<JezykNapisow> findAll() {
        return repo.findAll();
    }

    @Transactional
    public JezykNapisow save(JezykNapisow jezyk) {
        return repo.save(jezyk);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
