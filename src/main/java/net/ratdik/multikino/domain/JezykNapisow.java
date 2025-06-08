package net.ratdik.multikino.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jezyk_napisow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JezykNapisow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nazwa_jezyka", nullable = false, length = 50)
    private Jezyk nazwaJezyka;
}