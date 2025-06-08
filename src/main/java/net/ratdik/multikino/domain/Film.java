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

    @Column(name = "czas_trwania", nullable = false)
    private LocalTime czasTrwania;

    @Enumerated(EnumType.STRING)
    @Column(name = "gatunek", length = 50)
    private Gatunek gatunek;

    @Enumerated(EnumType.STRING)
    @Column(name = "kraj_pochodzenia", length = 50)
    private Kraj krajPochodzenia;

    @Enumerated(EnumType.STRING)
    @Column(name = "jezyk_wyswietlania", length = 50, nullable = false)
    private Jezyk jezykWyswietlania;

    @Column(name = "ocena", precision = 2, scale = 1)
    private BigDecimal ocena;
}