package com.henrique.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "seat",
        uniqueConstraints = {
            // Garante que não existam assentos duplicados na mesma posição física.
            // Regra: Uma sala (room_id) não pode ter mais de um assento com a mesma fileira e número.
                @UniqueConstraint(
                    name = "uk_room_seat_position",
                    columnNames = {"room_id", "row_label", "column_number"}
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Column(name = "row_label", nullable = false)
    private String rowLabel;

    @Column(name = "column_number", nullable = false)
    private Integer columnNumber;

    @Column(nullable = false, updatable = false)
    private UUID createdByUserId;

    @Column(nullable = false)
    private Boolean active = true;
}
