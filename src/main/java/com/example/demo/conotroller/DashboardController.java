package com.example.demo.conotroller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Crop;
import com.example.demo.entity.Schedule;
import com.example.demo.repository.CropRepository;
import com.example.demo.service.ScheduleService;
import com.example.demo.service.StockService;

@Controller
public class DashboardController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private StockService stockService;

    @Autowired
    private CropRepository cropRepository;

    @GetMapping("/")
    public String dashboard(Model model) {

        LocalDate today = LocalDate.now();
        List<Schedule> scheduleList = scheduleService.findAll();
        List<Schedule> todaySchedules = scheduleService.findByDate(today.toString());
        List<Crop> upcomingHarvests =
                cropRepository.findByHarvestDateGreaterThanEqualOrderByHarvestDateAsc(today);

        model.addAttribute("scheduleList", scheduleList);
        model.addAttribute("todaySchedules", todaySchedules);
        model.addAttribute("todayCount", todaySchedules.size());
        model.addAttribute("todayLabel", today.format(
                DateTimeFormatter.ofPattern("M月d日（E）", Locale.JAPANESE)));

        model.addAttribute("cropCount", cropRepository.count());
        model.addAttribute("nextHarvest",
                upcomingHarvests.isEmpty() ? null : upcomingHarvests.get(0));

        // 在庫一覧
        model.addAttribute("stockList",
                stockService.findAll());

        // 不足資材
        model.addAttribute("lowStocks",
                stockService.getLowStocks());

        // 余剰資材
        model.addAttribute("overStocks",
                stockService.getOverStocks());

        return "index";
    }

}
