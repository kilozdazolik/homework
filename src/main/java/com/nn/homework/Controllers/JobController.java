package com.nn.homework.Controllers;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class JobController {
    private final JobLauncher jobLauncher;

    private final Job surValuesJob;

    private final Job policyJob;

    private final Job outpayHeaderJob;

    public JobController(JobLauncher jobLauncher, @Qualifier("importSurValuesJob") Job surValuesJob, @Qualifier("importPolicyJob") Job policyJob, @Qualifier("importOutpayHeaderJob") Job outpayHeaderJob) {
        this.jobLauncher = jobLauncher;
        this.surValuesJob = surValuesJob;
        this.policyJob = policyJob;
        this.outpayHeaderJob = outpayHeaderJob;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("jobType") String jobType, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Kérlek válassz egy fájlt");
            return "index";
        }

        try {
            String targetFileName = "";
            Job jobToRun = switch (jobType) {
                case "SUR_VALUES" -> {
                    targetFileName = "ZTPSPF.txt";
                    yield surValuesJob;
                }
                case "POLICY" -> {
                    targetFileName = "CUSTCOMP01.TXT";
                    yield policyJob;
                }
                case "OUTPAY" -> {
                    targetFileName = "OUTPH_CUP_20200204_1829.TXT";
                    yield outpayHeaderJob;
                }
                default -> throw new IllegalArgumentException("Ismeretlen típus: " + jobType);
            };

            Path path = Paths.get(targetFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(jobToRun, jobParameters);

            model.addAttribute("message", "Sikeres importálás: " + jobType);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Hiba történt: " + e.getMessage());
        }

        return "index";
    }
}
