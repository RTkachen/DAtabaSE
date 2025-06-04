package net.ratdik.multikino.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "filmid")
    private Film film;

    @ManyToOne(optional = false)
    @JoinColumn(name = "salaid")
    private Sala sala;

    @ManyToOne
    @JoinColumn(name = "jezyk_napsiowid")
    private JezykNapisow jezykNapsiow;

    @Column(name = "czas_rozpoczecia", nullable = false)
    private LocalDateTime czasRozpoczecia;

    @Column(name = "czas_zakonczenia", nullable = false)
    private LocalDateTime czasZakonczenia;

    @Column(name = "wolne_miejsca", nullable = false)
    private Integer wolneMiejsca;

    @Column(name = "dubbing", nullable = false)
    private boolean dubbing;
}
