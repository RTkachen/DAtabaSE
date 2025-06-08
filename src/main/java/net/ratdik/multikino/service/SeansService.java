package net.ratdik.multikino.service;

import net.ratdik.multikino.domain.Seans;
import net.ratdik.multikino.repository.SeansRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SeansService {
    private final SeansRepository repo;

    public SeansService(SeansRepository r) {
        this.repo = r;
    }

    public List<Seans> findAll() {
        return repo.findAll();
    }

    @Transactional
    public Seans save(Seans s) {
        return repo.save(s);
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(Long.valueOf(id));
    }

    public List<Seans> findByHour(String hhmm) {
        if (hhmm == null || hhmm.isBlank()) {
            return findAll();
        }
        int hour;
        try {
            hour = Integer.parseInt(hhmm.trim().split(":")[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nieprawidłowy format godziny: " + hhmm);
        }
        return repo.findByHour(hour);
    }

    public Optional<Seans> findById(Integer id) {
        return repo.findById(Long.valueOf(id));
    }
}