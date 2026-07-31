package com.example.demo.conotroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.ScheduleService;
import com.example.demo.service.StockService;

@Controller
public class DashboardController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private StockService stockService;

    @GetMapping("/")
    public String dashboard(Model model) {

        // 作業予定
        model.addAttribute("scheduleList",
                scheduleService.findAll());

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