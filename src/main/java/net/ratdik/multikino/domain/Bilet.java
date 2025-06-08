package net.ratdik.multikino.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bilet")
@Getter
@Setter
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
public class Bilet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "uzytkownik_zalogowanyid", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "seansid", nullable = false)
    private Seans seans;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusid", nullable = false)
    private Status status;

    @Column(name = "data_zakupu", nullable = false)
    private LocalDateTime dataZakupu; // Dodane pole
}