package net.ratdik.multikino.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "film")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nazwa", length = 50, nullable = false)
    private String nazwa;

    @Column(name = "rezyser", length = 50)
    private String rezyser;

    @Column(name = "plakat", length = 255)
    private String plakat;

    // mapowanie na TIME(6) – LocalTime w Javie
    @Column(name = "czas_trwania", nullable = false)
    private LocalTime czasTrwania;

    @Column(name = "gatunek", length = 50)
    private String gatunek;

    @Column(name = "kraj_pochodzenia", length = 50)
    private String krajPochodzenia;

    @Column(name = "jezyk_wyswietlania", length = 50, nullable = false)
    private String jezykWyswietlania;

    /**
     * Java‐owa reprezentacja DECIMAL(2,1) z bazy.
     * Dzięki BigDecimal Hibernate poprawnie użyje body precision & scale.
     */
    @Column(name = "ocena", precision = 2, scale = 1)
    private BigDecimal ocena;
}
