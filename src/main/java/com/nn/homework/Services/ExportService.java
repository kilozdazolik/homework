package com.nn.homework.Services;

import com.nn.homework.Models.OutpayHeader;
import com.nn.homework.Models.Policy;
import com.nn.homework.Models.SurValues;
import com.nn.homework.Repositories.OutpayHeaderRepository;
import com.nn.homework.Repositories.PolicyRepository;
import com.nn.homework.Repositories.SurValuesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private SurValuesRepository surValuesRepository;
    @Autowired
    private OutpayHeaderRepository outpayHeaderRepository;

    private static final String EXPORT_FOLDER = "input_data";

    // Ee kell, hogy az OUTPAY dátumai (pl. 20200210) helyes formátumban kerüljenek vissza
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String exportAllData() throws IOException {
        if (!Files.exists(Paths.get(EXPORT_FOLDER))) {
            Files.createDirectories(Paths.get(EXPORT_FOLDER));
        }

        exportPolicy();
        exportSurValues();
        exportOutpayHeader();

        return "Mind a 3 fájl (Policy, SurValues, Outpay)";
    }

    // --- POLICY (|) ---
    private void exportPolicy() throws IOException {
        List<Policy> data = policyRepository.findAll();
        Path path = Paths.get(EXPORT_FOLDER, "CUSTCOMP01.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {

            for (Policy p : data) {
                String line = String.join("|",
                        safeStr(p.getChdrnum()),
                        safeStr(p.getCownnum()),
                        safeStr(p.getOwnerName()),
                        safeStr(p.getLifcNum()),
                        safeStr(p.getLifcName()),
                        safeStr(p.getAracde()),
                        safeStr(p.getAgntnum()),
                        safeStr(p.getMailAddress())
                );
                writer.write(line + "|");
                writer.newLine();
            }
        }
    }

    // --- OUTPAY HEADER (;) ---
    private void exportOutpayHeader() throws IOException {
        List<OutpayHeader> data = outpayHeaderRepository.findAll();
        Path path = Paths.get(EXPORT_FOLDER, "OUTPH_CUP_20200204_1829.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {

            for (OutpayHeader o : data) {
                String line = String.join(";",
                        safeStr(o.getClntnum()),
                        safeStr(o.getChdrnum()),
                        safeStr(o.getLetterType()),
                        // Itt használjuk a DATE_FORMATTER-t!
                        o.getPrintDate() != null ? o.getPrintDate().format(DATE_FORMATTER) : "",
                        safeStr(o.getDataID()),
                        safeStr(o.getClntName()),
                        safeStr(o.getClntAddress()),
                        o.getRegDate() != null ? o.getRegDate().format(DATE_FORMATTER) : "",
                        o.getBenPercent() != null ? o.getBenPercent().toString() : "",
                        safeStr(o.getRole1()),
                        safeStr(o.getRole2()),
                        safeStr(o.getCownNum()),
                        safeStr(o.getCownName()),
                        safeStr(o.getNotice01()), safeStr(o.getNotice02()), safeStr(o.getNotice03()),
                        safeStr(o.getNotice04()), safeStr(o.getNotice05()), safeStr(o.getNotice06()),
                        safeStr(o.getClaimId()),
                        o.getTp2ProcessDate() != null ? o.getTp2ProcessDate().format(DATE_FORMATTER) : ""
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // --- SUR VALUES (Fix szélességű) ---
    private void exportSurValues() throws IOException {
        List<SurValues> data = surValuesRepository.findAll();
        Path path = Paths.get(EXPORT_FOLDER, "ZTPSPF.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {

            for (SurValues s : data) {
                StringBuilder sb = new StringBuilder();

                // formázás a Start/Length alapján
                // %-Ns = Balra igazítva, N karakter szélesen (szóközökkel kiegészítve)
                sb.append(String.format("%-1s", safeStr(s.getCompany())));   // 1
                sb.append(String.format("%-8s", safeStr(s.getChdrnum())));   // 8

                // szám formázása: " 3276866.00" (15 karakter)
                sb.append(String.format("%15s", s.getSurvalue() != null ? s.getSurvalue().toString() : "0.00"));

                // adatok a végére, hogy meglegyen a sorhossz
                sb.append(String.format("%-20s", "EXPORT_USER")); // 20
                sb.append(String.format("%-10s", safeStr(s.getValidDate()))); // 10
                sb.append(String.format("%-26s", "-00.00.00.000000")); // 26

                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }

    private String safeStr(String s) {
        return s == null ? "" : s;
    }
}