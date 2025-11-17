package com.nn.homework.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Outpay_Header")
@Data
public class OutpayHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Outpay_Header_ID")
    private Long outpayHeaderId;

    @Column(name = "Clntnum", nullable = false, length = 8)
    private String clntnum;

    @Column(name = "Chdrnum", nullable = false, length = 8)
    private String chdrnum;

    @Column(name = "LetterType", nullable = false, length = 12)
    private String letterType;

    @Column(name = "PrintDate", nullable = false)
    private LocalDateTime printDate;

    @Column(name = "DataID", length = 6)
    private String dataID;

    @Column(name = "ClntName", length = 80)
    private String clntName;

    @Column(name = "ClntAddress", length = 80)
    private String clntAddress;

    @Column(name = "RegDate")
    private LocalDateTime regDate;

    @Column(name = "BenPercent", precision = 6, scale = 2)
    private BigDecimal benPercent;

    @Column(name = "Role1", length = 2)
    private String role1;

    @Column(name = "Role2", length = 2)
    private String role2;

    @Column(name = "CownNum", length = 8)
    private String cownNum;

    @Column(name = "CownName", length = 80)
    private String cownName;

    @Column(name = "Notice01", length = 80)
    private String notice01;
    @Column(name = "Notice02", length = 80)
    private String notice02;
    @Column(name = "Notice03", length = 80)
    private String notice03;
    @Column(name = "Notice04", length = 80)
    private String notice04;
    @Column(name = "Notice05", length = 80)
    private String notice05;
    @Column(name = "Notice06", length = 80)
    private String notice06;

    @Column(name = "Claim_ID", length = 9)
    private String claimId;

    @Column(name = "TP2ProcessDate")
    private LocalDateTime tp2ProcessDate;
}
