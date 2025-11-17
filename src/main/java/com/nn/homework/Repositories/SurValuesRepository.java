package com.nn.homework.Repositories;

import com.nn.homework.Models.SurValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurValuesRepository extends JpaRepository<SurValues, Long> {
}
