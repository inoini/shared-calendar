package com.example.demo.conotroller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Schedule;
import com.example.demo.model.CalendarDay;
import com.example.demo.service.ScheduleService;

@Controller
public class CalendarController {

    @Autowired
    private ScheduleService scheduleService;

    // ===== ホーム画面 =====
    @GetMapping("/")
    public String home() {
        return "index";
    }
    // ===== カレンダー =====
    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        YearMonth ym = (year == null || month == null)
                ? YearMonth.now()
                : YearMonth.of(year, month);

        model.addAttribute("year", ym.getYear());
        model.addAttribute("month", ym.getMonthValue());

        YearMonth prev = ym.minusMonths(1);
        YearMonth next = ym.plusMonths(1);

        model.addAttribute("prevYear", prev.getYear());
        model.addAttribute("prevMonth", prev.getMonthValue());

        model.addAttribute("nextYear", next.getYear());
        model.addAttribute("nextMonth", next.getMonthValue());

        LocalDate firstDay = ym.atDay(1);

        int startWeek = firstDay.getDayOfWeek().getValue() % 7;

        int lastDay = ym.lengthOfMonth();

        List<CalendarDay> calendarDays = new ArrayList<>();

        // ===== 前月 =====
        YearMonth previousMonth = ym.minusMonths(1);
        int previousLastDay = previousMonth.lengthOfMonth();

        for (int i = startWeek - 1; i >= 0; i--) {

            int day = previousLastDay - i;

            LocalDate date = previousMonth.atDay(day);

            String dateKey = date.toString();

            List<Schedule> scheduleList =
                    scheduleService.findByDate(dateKey);

            calendarDays.add(
                    new CalendarDay(
                            day,
                            dateKey,
                            false,
                            scheduleList));
        }

        // ===== 今月 =====
        for (int day = 1; day <= lastDay; day++) {

            LocalDate date = ym.atDay(day);

            String dateKey = date.toString();

            List<Schedule> scheduleList =
                    scheduleService.findByDate(dateKey);

            calendarDays.add(
                    new CalendarDay(
                            day,
                            dateKey,
                            true,
                            scheduleList));
        }

        // ===== 翌月 =====
        int remain = 42 - calendarDays.size();

        YearMonth followingMonth = ym.plusMonths(1);

        for (int day = 1; day <= remain; day++) {

            LocalDate date = followingMonth.atDay(day);

            String dateKey = date.toString();

            List<Schedule> scheduleList =
                    scheduleService.findByDate(dateKey);

            calendarDays.add(
                    new CalendarDay(
                            day,
                            dateKey,
                            false,
                            scheduleList));
        }

        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("today", LocalDate.now().toString());

        return "calendar";
    }
    // ===== 予定保存 =====
    @PostMapping("/save")
    public String save(
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam String userName,
            @RequestParam String schedule,
            @RequestParam(required = false) String fieldName,
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) String memo) {

        Schedule s = new Schedule();

        s.setDate(date);
        s.setTime(time);
        s.setUserName(userName);
        s.setSchedule(schedule);

        // スマート農業用項目
        s.setFieldName(fieldName);
        s.setCropName(cropName);
        s.setWorkType(workType);
        s.setMemo(memo);

        scheduleService.save(s);

        return "redirect:/calendar?year="
                + LocalDate.parse(date).getYear()
                + "&month="
                + LocalDate.parse(date).getMonthValue();
    }

    // ===== 予定取得 =====
    @GetMapping("/schedule")
    @ResponseBody
    public List<Schedule> getSchedule(@RequestParam String date) {
        return scheduleService.findByDate(date);
    }

    // ===== 編集データ取得 =====
    @GetMapping("/schedule/edit")
    @ResponseBody
    public Schedule edit(@RequestParam Long id) {
        return scheduleService.findById(id);
    }

    // ===== 更新 =====
    @PostMapping("/update")
    public String update(
            @RequestParam Long id,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam String userName,
            @RequestParam String schedule,
            @RequestParam(required = false) String fieldName,
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) String memo) {

        Schedule s = scheduleService.findById(id);

        if (s != null) {
            s.setDate(date);
            s.setTime(time);
            s.setUserName(userName);
            s.setSchedule(schedule);

            // スマート農業用項目
            s.setFieldName(fieldName);
            s.setCropName(cropName);
            s.setWorkType(workType);
            s.setMemo(memo);

            scheduleService.save(s);
        }

        return "redirect:/calendar?year="
                + LocalDate.parse(date).getYear()
                + "&month="
                + LocalDate.parse(date).getMonthValue();
    }

    // ===== 削除 =====
    @PostMapping("/delete")
    public String delete(
            @RequestParam Long id,
            @RequestParam String date) {

        scheduleService.delete(id);

        return "redirect:/calendar?year="
                + LocalDate.parse(date).getYear()
                + "&month="
                + LocalDate.parse(date).getMonthValue();
    }
}