package net.ratdik.multikino.service;

import net.ratdik.multikino.domain.Sala;
import net.ratdik.multikino.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaService {
    private final SalaRepository salaRepo;

    public SalaService(SalaRepository salaRepo) {
        this.salaRepo = salaRepo;
    }

    public List<Sala> findAll() {
        return salaRepo.findAll();
    }

    @Transactional
    public Sala save(Sala sala) {
        return salaRepo.save(sala);
    }

    public void deleteById(Integer id) {
        salaRepo.deleteById(id);
    }
}
