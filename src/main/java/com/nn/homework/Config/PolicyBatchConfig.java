package com.nn.homework.Config;

import com.nn.homework.Models.DTO.PolicyDTO;
import com.nn.homework.Models.Policy;
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

@Configuration
public class PolicyBatchConfig {

    @Bean
    public FlatFileItemReader<PolicyDTO> policyReader() {
        return new FlatFileItemReaderBuilder<PolicyDTO>()
                .name("policyReader")
                .resource(new FileSystemResource("CUSTCOMP01.TXT"))
                .lineTokenizer(new DelimitedLineTokenizer() {{
                    setDelimiter("|");
                    setNames("chdrnum", "cownum", "ownerName", "lifcNum", "lifcName", "aracde", "agntnum", "mailAddress");
                    setStrict(false);
                }})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(PolicyDTO.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<PolicyDTO, Policy> policyProcessor() {
        return input -> {

            if (input.getMailAddress() != null && input.getMailAddress().trim().length() > 50) {
                System.err.println("HIBA: Sor kihagyva - MailAddress túl hosszú. Chdrnum: " + input.getChdrnum());
                return null;
            }

            Policy output = new Policy();

            output.setChdrnum(input.getChdrnum().trim());
            output.setCownnum(input.getCownum().trim());
            output.setOwnerName(input.getOwnerName().trim());
            output.setLifcNum(input.getLifcNum().trim());
            output.setLifcName(input.getLifcName().trim());
            output.setAracde(input.getAracde().trim());
            output.setAgntnum(input.getAgntnum().trim());
            output.setMailAddress(input.getMailAddress().trim());

            return output;
        };
    }

    @Bean
    public JpaItemWriter<Policy> policyWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Policy>().entityManagerFactory(entityManagerFactory).build();
    }

    @Bean
    public Step importPolicyStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ItemReader<PolicyDTO> policyReader,
                                 ItemProcessor<PolicyDTO, Policy> policyProcessor,
                                 ItemWriter<Policy> policyWriter) {
        return new StepBuilder("importPolicyStep", jobRepository)
                .<PolicyDTO, Policy>chunk(1000, transactionManager)
                .reader(policyReader)
                .processor(policyProcessor)
                .writer(policyWriter)
                .build();
    }

    @Bean
    public Job importPolicyJob(JobRepository jobRepository, @Qualifier("importPolicyStep") Step importPolicyStep) {
        return new JobBuilder("importPolicyJob", jobRepository)
                .flow(importPolicyStep)
                .end()
                .build();
    }
}
