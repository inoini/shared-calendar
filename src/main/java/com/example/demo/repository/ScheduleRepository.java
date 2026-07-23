package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Schedule;


@Repository
public interface ScheduleRepository 
        extends JpaRepository<Schedule, Long> {


    // 日付ごとの予定取得（開始時間順）
    List<Schedule> findByDateOrderByStartTimeAsc(String date);



    // 日付・担当者ごとの予定取得（開始時間順）
    List<Schedule> findByDateAndUserNameOrderByStartTimeAsc(
            String date,
            String userName
    );


}