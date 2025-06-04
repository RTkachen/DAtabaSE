package net.ratdik.multikino.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bilet")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(of="id")
public class Bilet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne @JoinColumn(name="uzytkownik_zalogowanyid", nullable=false)
    private User user;

    @ManyToOne @JoinColumn(name="seansid", nullable=false)
    private Seans seans;

    @Enumerated(EnumType.STRING)
    @Column(name="statusid", nullable=false)
    private Status status;
}
