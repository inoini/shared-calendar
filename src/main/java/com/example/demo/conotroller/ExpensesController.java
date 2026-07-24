package com.example.demo.conotroller;


import org.springframework.stereotype.Controller;
import java.util.List;
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
                .filter(e -> e.getAmount() != null)
                .mapToDouble(Expense::getAmount)
                .sum();

    }
 
    public java.util.List<Expense> getExpenses(){

        return expenseRepository.findAll();

    }
    @GetMapping("/expenses/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model){

        Expense expense =
                expenseRepository.findById(id)
                .orElseThrow();

        model.addAttribute(
                "expense",
                expense
        );

        return "expenses_edit";

    }



    @PostMapping("/expenses/update")
    public String update(
            @ModelAttribute Expense expense){

        expenseRepository.save(expense);

        return "redirect:/expenses";

    }



    @GetMapping("/expenses/delete/{id}")
    public String delete(
            @PathVariable Long id){

        expenseRepository.deleteById(id);

        return "redirect:/expenses";

    }

}