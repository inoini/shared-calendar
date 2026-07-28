package com.example.demo.conotroller;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Crop;

import com.example.demo.entity.Schedule;
import com.example.demo.model.CalendarDay;
import com.example.demo.service.ScheduleService;
import com.example.demo.repository.CropRepository;



@Controller
public class CalendarController {


    private final ScheduleService scheduleService;

    private final CropRepository cropRepository;



    public CalendarController(
            ScheduleService scheduleService,
            CropRepository cropRepository){

        this.scheduleService = scheduleService;
        this.cropRepository = cropRepository;

    }





    // ホーム（ダッシュボード）
    @GetMapping("/")
    public String home(Model model){


        long cropCount =
                cropRepository.count();



        model.addAttribute(
                "cropCount",
                cropCount
        );




        String today =
                LocalDate.now().toString();



        List<Schedule> todaySchedules =
                scheduleService.findByDate(today);



        model.addAttribute(
                "todaySchedules",
                todaySchedules
        );



        model.addAttribute(
                "todayCount",
                todaySchedules.size()
        );




        List<Crop> harvestCrops =
                cropRepository
                .findByHarvestDateGreaterThanEqualOrderByHarvestDateAsc(
                        LocalDate.now()
                );



        if(!harvestCrops.isEmpty()){


            Crop nextHarvest =
                    harvestCrops.get(0);



            model.addAttribute(
                    "nextHarvest",
                    nextHarvest
            );


        }



        return "index";

    }







    // カレンダー表示
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

        List<CalendarDay> calendarDays = new ArrayList<>();

        // その月の1日
        LocalDate firstDay = ym.atDay(1);

        // 日曜=0～土曜=6
        int firstWeek = firstDay.getDayOfWeek().getValue() % 7;

        // 前月
        YearMonth prevMonth = ym.minusMonths(1);
        int prevLastDay = prevMonth.lengthOfMonth();

        // ===== 前月の日付 =====
        for (int i = firstWeek - 1; i >= 0; i--) {

            LocalDate date = prevMonth.atDay(prevLastDay - i);

            calendarDays.add(
                    new CalendarDay(
                            date.getDayOfMonth(),
                            date.toString(),
                            false,
                            scheduleService.findByDate(date.toString())
                    )
            );
        }

        // ===== 当月 =====
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {

            LocalDate date = ym.atDay(day);

            calendarDays.add(
                    new CalendarDay(
                            day,
                            date.toString(),
                            true,
                            scheduleService.findByDate(date.toString())
                    )
            );
        }

        // ===== 翌月 =====
        int nextDay = 1;

        while (calendarDays.size() < 42) {

            LocalDate date = next.atDay(nextDay);

            calendarDays.add(
                    new CalendarDay(
                            nextDay,
                            date.toString(),
                            false,
                            scheduleService.findByDate(date.toString())
                    )
            );

            nextDay++;
        }

        model.addAttribute("calendarDays", calendarDays);

        return "calendar";
    }







    // 保存
    
    @PostMapping("/save")
    public String save(

            @RequestParam(required = false) Long id,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String userName,
            @RequestParam String schedule,
            @RequestParam(required=false) String fieldName,
            @RequestParam(required=false) String cropName,
            @RequestParam(required=false) String workType,
            @RequestParam(required=false) String memo){

        Schedule s;

        // 編集
        if(id != null){

            s = scheduleService.findById(id);

            if(s == null){
                s = new Schedule();
            }

        }
        // 新規
        else{

            s = new Schedule();

        }

        s.setDate(date);
        s.setStartTime(startTime);
        s.setEndTime(endTime);
        s.setUserName(userName);
        s.setSchedule(schedule);
        s.setFieldName(fieldName);
        s.setCropName(cropName);
        s.setWorkType(workType);
        s.setMemo(memo);

        scheduleService.save(s);

        LocalDate d = LocalDate.parse(date);

        return "redirect:/calendar?year="
                + d.getYear()
                + "&month="
                + d.getMonthValue();
    }




    // 予定取得
    @GetMapping("/schedule")
    @ResponseBody
    public List<Schedule> getSchedule(
            @RequestParam String date){

        return scheduleService.findByDate(date);

    }







    // 編集取得
    @GetMapping("/schedule/edit")
    @ResponseBody
    public Schedule edit(
            @RequestParam Long id){

        return scheduleService.findById(id);

    }
 // ==========================
 // 更新
 // ==========================

 @PostMapping("/update")
 public String update(

         @RequestParam Long id,
         @RequestParam String date,
         @RequestParam String startTime,
         @RequestParam String endTime,
         @RequestParam String userName,
         @RequestParam String schedule,
         @RequestParam(required=false) String fieldName,
         @RequestParam(required=false) String cropName,
         @RequestParam(required=false) String workType,
         @RequestParam(required=false) String memo

 ){

     Schedule s =
             scheduleService.findById(id);


     if(s == null){

         return "redirect:/calendar";

     }


     s.setDate(date);

     s.setStartTime(startTime);

     s.setEndTime(endTime);

     s.setUserName(userName);

     s.setSchedule(schedule);

     s.setFieldName(fieldName);

     s.setCropName(cropName);

     s.setWorkType(workType);

     s.setMemo(memo);


     scheduleService.save(s);



     LocalDate d =
             LocalDate.parse(date);


     return "redirect:/calendar?year="
             + d.getYear()
             + "&month="
             + d.getMonthValue();

 }






   


    // 削除
 // ==========================
 // 削除
 // ==========================

 @PostMapping("/delete/{id}")
 @ResponseBody
 public String delete(
         @PathVariable Long id){


     scheduleService.delete(id);


     return "ok";

 }
 // ==========================
 // 日付クリック用 作業取得
 // ==========================

 @GetMapping("/calendar/day")
 @ResponseBody
 public List<Schedule> getDaySchedule(
         @RequestParam String date){


     return scheduleService.findByDate(date);


 }
}