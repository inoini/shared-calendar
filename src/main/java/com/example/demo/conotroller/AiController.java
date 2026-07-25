package com.example.demo.conotroller;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.example.demo.entity.Crop;
import com.example.demo.repository.CropRepository;
import com.example.demo.service.GeminiService;



@Controller
public class AiController {


    @Autowired
    private GeminiService geminiService;


    @Autowired
    private CropRepository cropRepository;




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



        // 作物情報取得

        List<Crop> crops =
                cropRepository.findAll();



        StringBuilder cropInfo =
                new StringBuilder();



        for(Crop crop : crops){


            cropInfo.append(
                    "作物："
                    + crop.getCropName()
                    + "\n"
            );


            cropInfo.append(
                    "品種："
                    + crop.getVariety()
                    + "\n"
            );


            cropInfo.append(
                    "圃場："
                    + crop.getFieldName()
                    + "\n"
            );


            cropInfo.append(
                    "植付日："
                    + crop.getPlantingDate()
                    + "\n"
            );


            cropInfo.append(
                    "収穫予定："
                    + crop.getHarvestDate()
                    + "\n\n"
            );


        }




        String prompt =


                "あなたは農業専門AIアシスタントです。\n"
                
                + "以下は現在管理している作物情報です。\n\n"

                + cropInfo.toString()


                + "\n農家からの質問：\n"

                + question


                + "\n\n"
                + "日本語で分かりやすく回答してください。"
                + "可能なら原因と対策を説明してください。";





        String answer =
                geminiService.ask(prompt);





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