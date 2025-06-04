package net.ratdik.multikino.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.repository.FilmRepository;

import java.util.List;

@Service
public class FilmService {
    private final FilmRepository filmRepo;

    public FilmService(FilmRepository filmRepo) {
        this.filmRepo = filmRepo;
    }

    public List<Film> findAll() {
        return filmRepo.findAll();
    }

    @Transactional
    public Film save(Film film) {
        return filmRepo.save(film);
    }

    public void deleteById(Integer id) {
        filmRepo.deleteById(id);
    }
}
