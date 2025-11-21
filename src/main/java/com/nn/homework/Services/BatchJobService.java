package com.nn.homework.Services;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class BatchJobService {

    private final JobLauncher jobLauncher;
    private final Job surValuesJob;
    private final Job policyJob;
    private final Job outpayHeaderJob;

    private static final String INPUT_FOLDER = "input_data";

    public BatchJobService(JobLauncher jobLauncher,
                           @Qualifier("importSurValuesJob") Job surValuesJob,
                           @Qualifier("importPolicyJob") Job policyJob,
                           @Qualifier("importOutpayHeaderJob") Job outpayHeaderJob) {
        this.jobLauncher = jobLauncher;
        this.surValuesJob = surValuesJob;
        this.policyJob = policyJob;
        this.outpayHeaderJob = outpayHeaderJob;
    }

    public void processFileImport(MultipartFile file, String jobType) throws Exception {
        JobConfig config = getJobConfig(jobType);
        String absolutePath = saveFileToInputFolder(file, config.targetFileName());
        runBatchJob(config.job(), absolutePath);
    }


    private record JobConfig(Job job, String targetFileName) {}

    private JobConfig getJobConfig(String jobType) {
        return switch (jobType) {
            case "SUR_VALUES" -> new JobConfig(surValuesJob, "ZTPSPF.txt");
            case "POLICY" -> new JobConfig(policyJob, "CUSTCOMP01.TXT");
            case "OUTPAY" -> new JobConfig(outpayHeaderJob, "OUTPH_CUP_20200204_1829.TXT");
            default -> throw new IllegalArgumentException("Ismeretlen típus: " + jobType);
        };
    }

    private String saveFileToInputFolder(MultipartFile file, String targetFileName) throws IOException {
        Path inputPath = Paths.get(INPUT_FOLDER);
        if (!Files.exists(inputPath)) {
            Files.createDirectories(inputPath);
        }
        Path targetPath = inputPath.resolve(targetFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toAbsolutePath().toString();
    }

    private void runBatchJob(Job job, String filePath) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("fullPath", filePath)
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
    }
}