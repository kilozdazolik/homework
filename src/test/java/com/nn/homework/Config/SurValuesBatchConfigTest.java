package com.nn.homework.Config;

import com.nn.homework.Models.DTO.SurValuesDTO;
import com.nn.homework.Models.SurValues;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SurValuesBatchConfigTest {

    private final SurValuesBatchConfig config = new SurValuesBatchConfig();

    @Test
    void SurValuesBatchConfig_ItemProcessor_GetValues() throws Exception {
        ItemProcessor<SurValuesDTO, SurValues> processor = config.surValuesProcessor();

        SurValuesDTO input = new SurValuesDTO();
        input.setCompany("1");
        input.setChdrnum("30052881");
        input.setSurrenderValue("   3276866.00   ");
        input.setJobName("2020-02-15");
        input.setJobTimestamp("-08.19.59.017770");

        SurValues result = processor.process(input);

        assertNotNull(result);

        assertEquals(0, new BigDecimal("3276866.00").compareTo(result.getSurvalue()));
        assertEquals("2020-02-15", result.getValidDate());
    }
}