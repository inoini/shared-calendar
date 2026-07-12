package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 日付ごとの予定（時間順）
    List<Schedule> findByDateOrderByTimeAsc(String date);

    // 日付・ユーザーごとの予定
    List<Schedule> findByDateAndUserNameOrderByTimeAsc(String date, String userName);

}