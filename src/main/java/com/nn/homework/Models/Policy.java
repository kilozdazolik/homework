package com.nn.homework.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dbo.Policy")
@Data
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Chdrnum", nullable = false, length = 8)
    private String chdrnum;

    @Column(name = "Cownnum", nullable = false, length = 8)
    private String cownnum;

    @Column(name = "OwnerName", length = 50)
    private String ownerName;

    @Column(name = "LifcNum", length = 8)
    private String lifcNum;

    @Column(name = "LifcName", length = 50)
    private String lifcName;

    @Column(name = "Aracde", length = 3)
    private String aracde;

    @Column(name = "Agntnum", length = 5)
    private String agntnum;

    @Column(name = "MailAddress", length = 50)
    private String mailAddress;

}
