package com.example.demo.conotroller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Expense;
import com.example.demo.repository.ExpenseRepository;



@Controller
public class ExpensesController {


    private final ExpenseRepository expenseRepository;


    public ExpensesController(
            ExpenseRepository expenseRepository){

        this.expenseRepository = expenseRepository;

    }



    @GetMapping("/expenses")
    public String expenses(Model model){


        model.addAttribute(
                "expenses",
                expenseRepository.findAll()
        );


        model.addAttribute(
                "expense",
                new Expense()
        );


        return "expenses";

    }




    @PostMapping("/expenses/add")
    public String add(
            @ModelAttribute Expense expense){


        expenseRepository.save(expense);


        return "redirect:/expenses";

    }



    public double getTotal(){


        return expenseRepository.findAll()
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();

    }



}