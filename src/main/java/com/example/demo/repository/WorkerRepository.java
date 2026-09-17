package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Worker;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findAllByOrderByNameAsc();
}
