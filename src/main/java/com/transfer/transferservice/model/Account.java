package com.transfer.transferservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long number;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private Long cpf;

    @Column(nullable = false)
    private Double balance;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.balance = 0.0;
    }

}
