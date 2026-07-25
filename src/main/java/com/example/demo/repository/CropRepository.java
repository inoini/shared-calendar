package com.example.demo.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Crop;


public interface CropRepository extends JpaRepository<Crop, Long>{


    // 収穫予定日の早い順
    List<Crop> findByHarvestDateGreaterThanEqualOrderByHarvestDateAsc(
            LocalDate date
    );


}