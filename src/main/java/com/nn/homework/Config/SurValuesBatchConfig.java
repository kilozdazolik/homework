package com.nn.homework.Config;

import com.nn.homework.Models.DTO.SurValuesDTO;
import com.nn.homework.Models.SurValues;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;

@Configuration
public class SurValuesBatchConfig {

    @Bean
    public FlatFileItemReader<SurValuesDTO> surValuesReader() {
        FixedLengthTokenizer tokenizer = getFixedLengthTokenizer();

        BeanWrapperFieldSetMapper<SurValuesDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(SurValuesDTO.class);

        return new FlatFileItemReaderBuilder<SurValuesDTO>()
                .name("surValuesReader")
                .resource(new FileSystemResource("ZTPSPF.txt"))
                .lineTokenizer(tokenizer)
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    @Bean
    public ItemProcessor<SurValuesDTO, SurValues> surValuesProcessor() {
        return input -> {
            SurValues output = new SurValues();

            output.setCompany(input.getCompany());
            output.setChdrnum(input.getChdrnum());
            output.setSurvalue(new BigDecimal(input.getSurrenderValue().trim()));
            output.setValidDate(input.getJobName().trim());

            return output;
        };
    }

    @Bean
    public JpaItemWriter<SurValues> surValuesWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<SurValues>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step importSurValuesStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    ItemReader<SurValuesDTO> surValuesReader,
                                    ItemProcessor<SurValuesDTO, SurValues> surValuesProcessor,
                                    ItemWriter<SurValues> surValuesWriter) {

        return new StepBuilder("importSurValuesStep", jobRepository)
                .<SurValuesDTO, SurValues>chunk(1000, transactionManager)
                .reader(surValuesReader)
                .processor(surValuesProcessor)
                .writer(surValuesWriter)
                .faultTolerant()
                .skip(NumberFormatException.class)
                .skipLimit(100)
                .build();
    }

    @Bean
    public Job importSurValuesJob(JobRepository jobRepository,
                                  @Qualifier("importSurValuesStep") Step importSurValuesStep) {

        return new JobBuilder("importSurValuesJob", jobRepository)
                .flow(importSurValuesStep)
                .end()
                .build();
    }

    private static FixedLengthTokenizer getFixedLengthTokenizer() {
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames("company", "chdrnum", "surrenderValue", "jobUser", "jobName", "jobTimestamp");
        tokenizer.setColumns(
                new Range(1, 1),   // 1. Company
                new Range(2, 9),   // 2. Chdrnum
                new Range(10, 24), // 3. SurrenderValue
                new Range(25, 44), // 4. JOB_User
                new Range(45, 54), // 5. JOB_Name
                new Range(55, 80)  // 6. JOB_Timestamp
        );
        tokenizer.setStrict(false);
        return tokenizer;
    }
}