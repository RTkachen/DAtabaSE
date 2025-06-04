package net.ratdik.multikino.service;

import net.ratdik.multikino.domain.Bilet;
import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.repository.BiletRepository;
import net.ratdik.multikino.repository.FilmRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StatisticsService {
    private final BiletRepository biletRepo;
    private final FilmRepository filmRepo;

    public StatisticsService(BiletRepository b, FilmRepository f) {
        this.biletRepo = b; this.filmRepo = f;
    }

    public Map<Film,Long> ticketsPerFilm() {
        Map<Film,Long> m = new HashMap<>();
        for (Film film: filmRepo.findAll()) {
            long count = biletRepo.findAll().stream()
                    .filter(b->b.getSeans().getFilm().equals(film))
                    .count();
            m.put(film,count);
        }
        return m;
    }

    public Map<Integer,Long> popularityByHour() {
        Map<Integer,Long> m = new TreeMap<>();
        biletRepo.findAll().forEach(b->{
            int h = b.getSeans().getCzasRozpoczecia().getHour();
            m.put(h, m.getOrDefault(h,0L)+1);
        });
        return m;
    }
}
