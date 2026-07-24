package com.example.demo.conotroller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Sales;
import com.example.demo.repository.SalesRepository;



@Controller
public class SalesController {


    private final SalesRepository salesRepository;



    public SalesController(
            SalesRepository salesRepository){

        this.salesRepository = salesRepository;

    }



    // 売上一覧
    @GetMapping("/sales")
    public String sales(Model model){


        model.addAttribute(
                "sales",
                salesRepository.findAll()
        );


        model.addAttribute(
                "sale",
                new Sales()
        );


        return "sales";

    }



    // 登録
    @PostMapping("/sales/add")
    public String add(
            @ModelAttribute Sales sale){


        salesRepository.save(sale);


        return "redirect:/sales";

    }



    // 売上合計
    public double getTotal(){


        return salesRepository.findAll()
                .stream()
                .mapToDouble(Sales::getAmount)
                .sum();

    }



    // 収支確認用
    public List<Sales> getSales(){

        return salesRepository.findAll();

    }


}