package com.nn.homework.Config;

import com.nn.homework.Models.DTO.OutpayHeaderDTO;
import com.nn.homework.Models.OutpayHeader;
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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class OutpayHeaderBatchConfig {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Bean
    public FlatFileItemReader<OutpayHeaderDTO> outpayHeaderReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(";");
        tokenizer.setNames("clntnum", "chdrnum", "letterType", "printDate", "dataID",
                "clntName", "clntAddress", "regDate", "benPercent", "role1",
                "role2", "cownNum", "cownName", "notice01", "notice02",
                "notice03", "notice04", "notice05", "notice06", "claimId",
                "tp2ProcessDate");
        tokenizer.setStrict(false);

        BeanWrapperFieldSetMapper<OutpayHeaderDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(OutpayHeaderDTO.class);

        return new FlatFileItemReaderBuilder<OutpayHeaderDTO>()
                .name("outpayHeaderReader")
                .resource(new FileSystemResource("OUTPH_CUP_20200204_1829.TXT"))
                .lineTokenizer(tokenizer)
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    @Bean
    public ItemProcessor<OutpayHeaderDTO, OutpayHeader> outpayHeaderProcessor() {
        return input -> {
            OutpayHeader output = new OutpayHeader();

            output.setClntnum(input.getClntnum().trim());
            output.setChdrnum(input.getChdrnum().trim());
            output.setLetterType(input.getLetterType().trim());
            output.setDataID(input.getDataID().trim());
            output.setClntName(input.getClntName().trim());
            output.setClntAddress(input.getClntAddress().trim());
            output.setRole1(input.getRole1().trim());
            output.setRole2(input.getRole2().trim());
            output.setCownNum(input.getCownNum().trim());
            output.setCownName(input.getCownName().trim());
            output.setNotice01(input.getNotice01().trim());
            output.setNotice02(input.getNotice02().trim());
            output.setNotice03(input.getNotice03().trim());
            output.setNotice04(input.getNotice04().trim());
            output.setNotice05(input.getNotice05().trim());
            output.setNotice06(input.getNotice06().trim());
            output.setClaimId(input.getClaimId().trim());

            if (StringUtils.hasText(input.getPrintDate())) {
                LocalDate printDate = LocalDate.parse(input.getPrintDate().trim(), DATE_FORMATTER);
                output.setPrintDate(printDate.atStartOfDay());
            }

            if (StringUtils.hasText(input.getRegDate())) {
                LocalDate regDate = LocalDate.parse(input.getRegDate().trim(), DATE_FORMATTER);
                output.setRegDate(regDate.atStartOfDay());
            }

            if (StringUtils.hasText(input.getTp2ProcessDate())) {
                LocalDate tp2Date = LocalDate.parse(input.getTp2ProcessDate().trim(), DATE_FORMATTER);
                output.setTp2ProcessDate(tp2Date.atStartOfDay());
            }

            if (StringUtils.hasText(input.getBenPercent())) {
                output.setBenPercent(new BigDecimal(input.getBenPercent().trim()));
            }


            return output;
        };
    }

    @Bean
    public JpaItemWriter<OutpayHeader> outpayHeaderWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<OutpayHeader>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step importOutpayHeaderStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       ItemReader<OutpayHeaderDTO> outpayHeaderReader,
                                       ItemProcessor<OutpayHeaderDTO, OutpayHeader> outpayHeaderProcessor,
                                       ItemWriter<OutpayHeader> outpayHeaderWriter) {

        return new StepBuilder("importOutpayHeaderStep", jobRepository)
                .<OutpayHeaderDTO, OutpayHeader>chunk(1000, transactionManager)
                .reader(outpayHeaderReader)
                .processor(outpayHeaderProcessor)
                .writer(outpayHeaderWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(100)
                .build();
    }

    @Bean
    public Job importOutpayHeaderJob(JobRepository jobRepository, @Qualifier("importOutpayHeaderStep") Step importOutpayHeaderStep) {
        return new JobBuilder("importOutpayHeaderJob", jobRepository)
                .flow(importOutpayHeaderStep)
                .end()
                .build();
    }
}
