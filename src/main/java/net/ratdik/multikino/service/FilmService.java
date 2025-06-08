package net.ratdik.multikino.service;

import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    private final FilmRepository repo;

    @Autowired
    public FilmService(FilmRepository repo) {
        this.repo = repo;
    }

    public List<Film> findAll() {
        return repo.findAll();
    }

    @Transactional
    public Film save(Film film) {
        return repo.save(film);
    }

    public Optional<Film> findById(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}