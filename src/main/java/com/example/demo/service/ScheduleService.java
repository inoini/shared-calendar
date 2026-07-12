package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Schedule;
import com.example.demo.repository.ScheduleRepository;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // 予定を保存
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // 全予定取得
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    // 日付ごとの予定取得（時間順）
    public List<Schedule> findByDate(String date) {
        return scheduleRepository.findByDateOrderByTimeAsc(date);
    }

    // 日付・ユーザーごとの予定取得
    public List<Schedule> findByDateAndUserName(String date, String userName) {
        return scheduleRepository.findByDateAndUserNameOrderByTimeAsc(date, userName);
    }

    // IDで取得
    public Schedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    // 削除
    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }

    // 更新
    public Schedule update(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }
    
}