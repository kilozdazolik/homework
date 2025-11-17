package com.nn.homework.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "dbo.SurValues")
@Data
public class SurValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name=  "Chdrnum", nullable = false, length = 8)
    private String chdrnum;

    @Column(name=  "Survalue", precision = 15, scale = 2)
    private BigDecimal survalue;

    @Column(name=  "Company", nullable = false, length = 1)
    private String company;

    @Column(name=  "Currency", length = 3)
    private String currency;

    @Column(name=  "ValidDate", length = 10)
    private String validDate;
}
