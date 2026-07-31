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

    @Autowired
    private StockService stockService;

    // ==========================
    // 保存
    // ==========================
    public Schedule save(Schedule schedule) {

        // 先に予定を保存
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 使用資材・数量が入力されている場合だけ在庫を減らす
        if (schedule.getItemName() != null
                && !schedule.getItemName().isBlank()
                && schedule.getUseQuantity() != null
                && schedule.getUseQuantity() > 0) {

            stockService.useStock(
                    schedule.getItemName(),
                    schedule.getUseQuantity());
        }

        return savedSchedule;
    }

    // 全予定取得
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    // 日付ごとの予定取得
    public List<Schedule> findByDate(String date) {
        return scheduleRepository.findByDateOrderByStartTimeAsc(date);
    }

    // 日付・担当者ごとの予定取得
    public List<Schedule> findByDateAndUserName(String date, String userName) {
        return scheduleRepository
                .findByDateAndUserNameOrderByStartTimeAsc(date, userName);
    }

    // ID取得
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