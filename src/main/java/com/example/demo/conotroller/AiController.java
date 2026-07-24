package com.example.demo.conotroller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.example.demo.service.OpenAiService;



@Controller
public class AiController {



    @Autowired
    private OpenAiService openAiService;




    // AI画面表示
    @GetMapping("/ai")
    public String ai(){

        return "ai";

    }





    // 質問送信
    @PostMapping("/ai")
    public String ask(
            @RequestParam String question,
            Model model){



        String answer =
                openAiService.ask(question);



        model.addAttribute(
                "question",
                question
        );


        model.addAttribute(
                "answer",
                answer
        );



        return "ai";

    }


}