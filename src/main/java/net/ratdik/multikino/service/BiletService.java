package net.ratdik.multikino.service;

import net.ratdik.multikino.domain.Bilet;
import net.ratdik.multikino.domain.Seans;
import net.ratdik.multikino.domain.Status;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.repository.BiletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BiletService {

    private final BiletRepository biletRepository;
    private final SeansService seansService;

    @Autowired
    public BiletService(BiletRepository biletRepository, SeansService seansService) {
        this.biletRepository = biletRepository;
        this.seansService = seansService;
    }

    @Transactional
    public Bilet zakupBiletu(Seans seans, User uzytkownik) {
        if (seans.getWolneMiejsca() <= 0) {
            throw new IllegalStateException("Brak wolnych miejsc na ten seans!");
        }

        Bilet bilet = Bilet.builder()
                .dataZakupu(LocalDateTime.now())
                .seans(seans)
                .status(Status.PURCHASED)
                .user(uzytkownik)
                .build();

        seans.setWolneMiejsca(seans.getWolneMiejsca() - 1);
        seansService.save(seans);

        return biletRepository.save(bilet);
    }

    @Transactional
    public void delete(Integer id) {
        biletRepository.deleteById(id);
    }

    public List<Bilet> findAll() {
        return biletRepository.findAll();
    }

    @Transactional
    public Bilet save(Bilet bilet) {
        return biletRepository.save(bilet);
    }
}