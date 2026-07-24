package com.example.demo.conotroller;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import com.example.demo.entity.Schedule;
import com.example.demo.model.CalendarDay;
import com.example.demo.service.ScheduleService;



@Controller
public class CalendarController {


    private final ScheduleService scheduleService;



    public CalendarController(
            ScheduleService scheduleService){

        this.scheduleService = scheduleService;

    }




    // ホーム
    @GetMapping("/")
    public String home(){

        return "index";

    }





    // カレンダー表示
    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required=false) Integer year,
            @RequestParam(required=false) Integer month,
            Model model){


        YearMonth ym =
                (year == null || month == null)
                ? YearMonth.now()
                : YearMonth.of(year, month);



        model.addAttribute(
                "year",
                ym.getYear()
        );


        model.addAttribute(
                "month",
                ym.getMonthValue()
        );



        YearMonth prev =
                ym.minusMonths(1);


        YearMonth next =
                ym.plusMonths(1);



        model.addAttribute(
                "prevYear",
                prev.getYear()
        );


        model.addAttribute(
                "prevMonth",
                prev.getMonthValue()
        );


        model.addAttribute(
                "nextYear",
                next.getYear()
        );


        model.addAttribute(
                "nextMonth",
                next.getMonthValue()
        );




        List<CalendarDay> calendarDays =
                new ArrayList<>();



        int lastDay =
                ym.lengthOfMonth();



        for(int day=1; day<=lastDay; day++){


            LocalDate date =
                    ym.atDay(day);



            String dateKey =
                    date.toString();



            List<Schedule> schedules =
                    scheduleService.findByDate(dateKey);



            calendarDays.add(
                    new CalendarDay(
                            day,
                            dateKey,
                            true,
                            schedules
                    )
            );

        }



        model.addAttribute(
                "calendarDays",
                calendarDays
        );



        return "calendar";

    }





    // 保存
    @PostMapping("/save")
    public String save(
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String userName,
            @RequestParam String schedule,
            @RequestParam(required=false) String fieldName,
            @RequestParam(required=false) String cropName,
            @RequestParam(required=false) String workType,
            @RequestParam(required=false) String memo){



        Schedule s =
                new Schedule();



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






    // 更新
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
            @RequestParam(required=false) String memo){



        Schedule s =
                scheduleService.findById(id);



        if(s != null){

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

        }




        LocalDate d =
                LocalDate.parse(date);



        return "redirect:/calendar?year="
                + d.getYear()
                + "&month="
                + d.getMonthValue();

    }






    // 削除
    @PostMapping("/delete")
    public String delete(
            @RequestParam Long id,
            @RequestParam String date){


        scheduleService.delete(id);



        LocalDate d =
                LocalDate.parse(date);



        return "redirect:/calendar?year="
                + d.getYear()
                + "&month="
                + d.getMonthValue();

    }

}