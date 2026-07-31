package com.example.demo.conotroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Stock;
import com.example.demo.service.StockService;


@Controller
@RequestMapping("/stock")
public class StockController {


    private final StockService stockService;


    public StockController(
            StockService stockService){

        this.stockService = stockService;

    }



    // 在庫画面表示
    @GetMapping
    public String index(Model model){


        model.addAttribute(
                "stock",
                new Stock()
        );


        model.addAttribute(
                "stockList",
                stockService.findAll()
        );


        return "stock";

    }




    // 保存
    @PostMapping("/save")
    public String save(
            @ModelAttribute Stock stock){


        stockService.save(stock);


        return "redirect:/stock";

    }


}