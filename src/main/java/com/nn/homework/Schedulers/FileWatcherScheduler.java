package com.nn.homework.Schedulers;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;

@Component
public class FileWatcherScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("importSurValuesJob")
    private Job surValuesJob;

    @Autowired
    @Qualifier("importPolicyJob")
    private Job policyJob;

    @Autowired
    @Qualifier("importOutpayHeaderJob")
    private Job outpayHeaderJob;

    private static final String INPUT_FOLDER = "input_data";

    @Scheduled(fixedRate = 20000)
    public void watchSurValues() {
        processFile("ZTPSPF.TXT", surValuesJob);
    }

    @Scheduled(fixedRate = 10000)
    public void watchPolicy() {
        processFile("CUSTCOMP01.txt", policyJob);
    }

    @Scheduled(fixedRate = 30000)
    public void watchOutpay() throws InterruptedException {
        // talán itt a név lehetne dinamikus is a date miatt?
        processFile("OUTPH_CUP_20200204_1829.TXT", outpayHeaderJob);
    }

    private void processFile(String fileName, Job job) {
        File file = Paths.get(INPUT_FOLDER, fileName).toFile();

        if (file.exists() && file.isFile()) {
            System.out.println("LOG: Fájl található: " + fileName + ". Feldolgozás indítása...");

            try {
                JobParameters params = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .addString("fullPath", file.getAbsolutePath())
                        .toJobParameters();

                jobLauncher.run(job, params);

                System.out.println("LOG: Feldolgozás kész.");

                if (file.delete()) {
                    System.out.println("LOG: Fájl törölve: " + fileName);
                } else {
                    System.err.println("LOG: Hiba a törlés során: " + fileName);
                }

            } catch (Exception e) {
                System.err.println("LOG: Hiba a feldolgozás során: " + e.getMessage());
            }
        }
    }
}