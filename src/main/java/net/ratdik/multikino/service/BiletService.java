package net.ratdik.multikino.service;

import net.ratdik.multikino.domain.*;
import net.ratdik.multikino.repository.BiletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BiletService {
    private final BiletRepository repo;
    private final SeansService seansService;

    public BiletService(BiletRepository b, SeansService s) {
        this.repo = b; this.seansService = s;
    }

    @Transactional
    public void purchase(User user, Seans seans) {
        if (seans.getWolneMiejsca()<=0)
            throw new IllegalStateException("Brak miejsc");
        Bilet b = new Bilet();
        b.setUser(user);
        b.setSeans(seans);
        b.setStatus(Status.PURCHASED);
        repo.save(b);

        seans.setWolneMiejsca(seans.getWolneMiejsca()-1);
        seansService.save(seans);
    }

    public List<Bilet> findByUser(Integer userId) {
        return repo.findByUserId(userId);
    }
}
